package net.ulrice.process;

import net.ulrice.module.IFController;

public interface IFBackgroundProcess extends Runnable {

	static enum ProcessState {
		Initialized,
		Started,
		Done, 
		Cancelled				
	}
	
	public boolean hasProgressInformation();
	
	public ProcessState getProcessState();
	
	public double getProcessProgress();

	public String getProcessProgressMessage();
	
	public IFController getOwningController();
	
	public void addProcessListener(IFProcessListener listener);
	
	public void removeProcessListener(IFProcessListener listener);
	
	public boolean blocksWorkarea();
	
	public boolean supportsCancel();
	
	public void cancelProcess();
}
