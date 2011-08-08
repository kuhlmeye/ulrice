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

	/** Map holding the processes grouped by controller. */
	private Map<IFController, List<IFBackgroundProcess>> processMap = new HashMap<IFController, List<IFBackgroundProcess>>();

	private EventListenerList listenerList = new EventListenerList();


	public void registerProcess(IFBackgroundProcess process) {
		IFController owningController = process.getOwningController();
		List<IFBackgroundProcess> processList = processMap.get(owningController);
		if (processList == null) {
			processList = new ArrayList<IFBackgroundProcess>();
			processMap.put(owningController, processList);
		}		
		processList.add(process);		
		process.addProcessListener(this);			
		fireStateChanged(process);
		

		if(ProcessState.Started.equals(process.getProcessState())) {
			if(process.blocksWorkarea()) {
				Ulrice.getModuleManager().block (process.getOwningController(), process);
			}
		}
	}

	public List<IFBackgroundProcess> getRunningProcesses(IFController controller) {
		return processMap.get(controller);
	}

	@Override
	public void progressChanged(IFBackgroundProcess process) {
		fireProgressChanged(process);
	}

	@Override
	public void stateChanged(IFBackgroundProcess process) {
		if(ProcessState.Started.equals(process.getProcessState())) {
			if(process.blocksWorkarea()) {
				Ulrice.getModuleManager().block(process.getOwningController(), process);
			}
		}
		if (ProcessState.Done.equals(process.getProcessState())) {
			if(process.blocksWorkarea()) {
			    Ulrice.getModuleManager().unblock(process.getOwningController(), process);
			}

			
			List<IFBackgroundProcess> list = processMap.get(process.getOwningController());

			if (!list.remove(process)) {
				LOG.warning("Process " + process.getProcessName() + " not found in list of controller "
						+ Ulrice.getModuleManager().getModule(process.getOwningController()).getModuleTitle(Usage.Default) + ".");
			}
		}
		fireStateChanged(process);
	}

	public void fireStateChanged(IFBackgroundProcess process) {
		IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
		if(listeners != null)  {
			for(IFProcessListener listener : listeners) {
				listener.stateChanged(process);
			}
		}
	}
	
	public void fireProgressChanged(IFBackgroundProcess process) {
		IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
		if(listeners != null)  {
			for(IFProcessListener listener : listeners) {
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
}
