package net.ulrice.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.process.IFBackgroundProcess.ProcessState;

/**
 * This class manages the execution of background processes started by modules.
 * 
 * @author DL10KUH
 */
public class ProcessManager implements IFProcessListener {

    private static final Logger LOG = Logger.getLogger(ProcessManager.class.getName());

    private Map<IFController, List<IFBackgroundProcess>> processMap = new HashMap<IFController, List<IFBackgroundProcess>>();
    private Map<String, IFBackgroundProcess> idProcessMap = new HashMap<String, IFBackgroundProcess>();
    private Map<IFBackgroundProcess, String> processIdMap = new HashMap<IFBackgroundProcess, String>();
    private List<IFBackgroundProcess> globalProcesses = new ArrayList<IFBackgroundProcess>();

    private EventListenerList listenerList = new EventListenerList();

    private static long currentId = System.currentTimeMillis();
    
    /**
     * Registers a background process at the process manager of ulrice. 
     * 
     * @param process The process.
     * @return The unique id of the process.
     */
    public String registerProcess(IFBackgroundProcess process) {
        String uniqueId = getNextUniqueId();
        IFController owningController = process.getOwningController();
        if (owningController != null) {
            List<IFBackgroundProcess> processList = processMap.get(owningController);
            if (processList == null) {
                processList = new ArrayList<IFBackgroundProcess>();
                processMap.put(owningController, processList);
            }
            processList.add(process);
        }
        else {
            globalProcesses.add(process);
        }
        idProcessMap.put(uniqueId, process);
        processIdMap.put(process, uniqueId);
        process.addProcessListener(this);
        fireStateChanged(process);

        if (ProcessState.Started.equals(process.getProcessState())) {
            if (process.getOwningController() != null && process.blocksWorkarea()) {
                Ulrice.getModuleManager().block(process.getOwningController(), process);
            }
        }
        return uniqueId;
    }

    public List<IFBackgroundProcess> getRunningProcesses(IFController controller) {        
        return controller != null ? processMap.get(controller) : globalProcesses;
    }

    @Override
    public void progressChanged(IFBackgroundProcess process) {
        fireProgressChanged(process);
    }

    @Override
    public void stateChanged(IFBackgroundProcess process) {
        if (ProcessState.Started.equals(process.getProcessState())) {
            if (process.getOwningController() != null && process.blocksWorkarea()) {
                Ulrice.getModuleManager().block(process.getOwningController(), process);
            }
        }
        if (ProcessState.Done.equals(process.getProcessState())) {
            if (process.getOwningController() != null && process.blocksWorkarea()) {
                Ulrice.getModuleManager().unblock(process.getOwningController(), process);
            }

            String uniqueId = processIdMap.get(process);
            idProcessMap.remove(uniqueId);
            IFController controller = process.getOwningController();
            List<IFBackgroundProcess> list = null;
            list = controller == null ? globalProcesses : processMap.get(process.getOwningController());

            if (!list.remove(process)) {
                LOG.warning("Process "
                    + process.getProcessName()
                    + " not found in list of controller "
                        + Ulrice.getModuleManager().getModule(process.getOwningController())
                            .getModuleTitle(Usage.Default) + ".");
            }
        }
        fireStateChanged(process);
    }
    
    public IFBackgroundProcess getProcessById(String uniqueId) {
        return idProcessMap.get(uniqueId);
    }

    public void fireStateChanged(IFBackgroundProcess process) {
        IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
        if (listeners != null) {
            for (IFProcessListener listener : listeners) {
                listener.stateChanged(process);
            }
        }
    }

    public void fireProgressChanged(IFBackgroundProcess process) {
        IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
        if (listeners != null) {
            for (IFProcessListener listener : listeners) {
                listener.progressChanged(process);
            }
        }
    }

    public void addProcessListener(IFProcessListener listener) {
        listenerList.add(IFProcessListener.class, listener);
    }

    public void removeProcessListener(IFProcessListener listener) {
        listenerList.remove(IFProcessListener.class, listener);
    }


    private synchronized String getNextUniqueId() {
        return Long.toHexString(currentId++);
    }      
}
