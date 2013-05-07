package net.ulrice.process;

import net.ulrice.module.IFController;

public interface IFBackgroundProcess extends Runnable {

	static enum ProcessState {
		Initialized,
		Started,
		Done, 
		Cancelled				
	}
	
	boolean hasProgressInformation();
	
	ProcessState getProcessState();
	
	double getProcessProgress();

	String getProcessProgressMessage();
	
	IFController getOwningController();
	
	void addProcessListener(IFProcessListener listener);
	
	void removeProcessListener(IFProcessListener listener);
	
	boolean blocksWorkarea();
	
	boolean supportsCancel();
	
	void cancelProcess();
}
