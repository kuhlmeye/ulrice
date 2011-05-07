package net.ulrice.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.module.IFController;

public class ProcessManager {

	private Map<IFController, List<IFBackgroundProcess>> processMap = new HashMap<IFController, List<IFBackgroundProcess>>();
	
	public ProcessManager() {
		
	}
	
	public void registerProcess(IFBackgroundProcess process) {
		IFController owningController = process.getOwningController();		
		List<IFBackgroundProcess> processList = processMap.get(owningController);
		if(processList == null) {
			processList = new ArrayList<IFBackgroundProcess>();
			processMap.put(owningController, processList);
		}
		processList.add(process);
		
		
		
	}
	
}
