package net.ulrice.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleRenderer.Usage;
import net.ulrice.process.IFBackgroundProcess.ProcessState;

public class ProcessManager implements IFProcessListener {

	private static final Logger LOG = Logger.getLogger(ProcessManager.class.getName());

	private Map<IFController, List<IFBackgroundProcess>> processMap = new HashMap<IFController, List<IFBackgroundProcess>>();

	private EventListenerList listenerList;

	public ProcessManager() {

	}

	public void registerProcess(IFBackgroundProcess process) {
		IFController owningController = process.getOwningController();
		List<IFBackgroundProcess> processList = processMap.get(owningController);
		if (processList == null) {
			processList = new ArrayList<IFBackgroundProcess>();
			processMap.put(owningController, processList);
		}
		processList.add(process);

		process.addProcessListener(this);
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
		if (ProcessState.Done.equals(process.getProcessState())) {
			List<IFBackgroundProcess> list = processMap.get(process.getOwningController());

			if (!list.remove(process)) {
				LOG.warning("Process " + process.getProcessName() + " not found in list of controller "
						+ process.getOwningController().getModule().getModuleTitle(Usage.Default) + ".");
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
