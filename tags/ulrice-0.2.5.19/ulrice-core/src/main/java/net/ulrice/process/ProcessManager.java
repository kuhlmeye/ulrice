package net.ulrice.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.message.MessageSeverity;
import net.ulrice.module.IFController;
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
    private Map<String, List<Runnable>> executeAfterProcessMap = new Hashtable<String, List<Runnable>>();

    private EventListenerList listenerList = new EventListenerList();

    private static long currentId = System.currentTimeMillis();
    
    /**
     * Registers a background process at the process manager of ulrice. 
     * 
     * @param process The process.
     * @return The unique id of the process.
     */
    public String registerProcess(IFBackgroundProcess process) {
        process.addProcessListener(this);
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
        fireStateChanged(process);

        if (ProcessState.Started.equals(process.getProcessState()) && (process.getOwningController() != null && process.blocksWorkarea())) {
            Ulrice.getModuleManager().addBlocker(process.getOwningController(), process);
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

        Ulrice.getMessageHandler().handleMessage(process.getOwningController(), MessageSeverity.Status, process.getProcessProgressMessage() + " (" + process.getProcessState() + ")");
        if (ProcessState.Started.equals(process.getProcessState()) && process.getOwningController() != null && process.blocksWorkarea()) {
            Ulrice.getModuleManager().addBlocker(process.getOwningController(), process);                
        }
        
    	String processId = getIdOfProcess(process);
    	if((ProcessState.Cancelled.equals(process.getProcessState()) || ProcessState.Done.equals(process.getProcessState())) && executeAfterProcessMap.containsKey(processId)) {
    		executeRunnables(executeAfterProcessMap.remove(processId));
    	}
        
        if (ProcessState.Done.equals(process.getProcessState())) {
            if (process.getOwningController() != null && process.blocksWorkarea()) {
                Ulrice.getModuleManager().removeBlocker(process.getOwningController(), process);
            } 

            String uniqueId = processIdMap.get(process);
            idProcessMap.remove(uniqueId);
            processIdMap.remove(process);
            IFController controller = process.getOwningController();
            List<IFBackgroundProcess> list = null;
            list = controller == null ? globalProcesses : processMap.get(process.getOwningController());

            if (!list.remove(process)) {
                LOG.warning("Process "
                    + process
                    + " not found in list of controller.");
            }else{
                if(list.size() ==0 && controller != null){
                    processMap.remove(controller);
                }
            }
        }
        fireStateChanged(process);
    }
    
    public IFBackgroundProcess getProcessById(String uniqueId) {
        return idProcessMap.get(uniqueId);
    }
    
    public String getIdOfProcess(IFBackgroundProcess process) {
    	return processIdMap.get(process);
    }
    
    /**
     * Starts the runnable 
     */
    public void doAfterProcess(final String processId, Runnable runnable) {
    	if(executeAfterProcessMap.containsKey(processId)) {
    		executeAfterProcessMap.get(processId).add(runnable);
    	} else {
        	executeAfterProcessMap.put(processId, Collections.singletonList(runnable));
    	}
    	
    	IFBackgroundProcess process = getProcessById(processId);
    	if(process == null || !executeAfterProcessMap.containsKey(processId)) {
    		executeRunnables(executeAfterProcessMap.remove(processId));    		
    	}
    }

    private void executeRunnables(List<Runnable> runnableList) {
		if(runnableList != null) {
			for(Runnable runnable : runnableList) {
				runnable.run();
			}
		}
	}

	public void fireStateChanged(final IFBackgroundProcess process) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
            	
                IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
                if (listeners != null) {
                    for (IFProcessListener listener : listeners) {
                        listener.stateChanged(process);
                    }
                }
            }
        });
    }

    public void fireProgressChanged(final IFBackgroundProcess process) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {    
                IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
                if (listeners != null) {
                    for (IFProcessListener listener : listeners) {
                        listener.progressChanged(process);
                    }
                }
            }
        });    
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
