package net.ulrice.process;

import net.ulrice.module.IFController;

public interface IFBackgroundProcess {

	static enum ProcessState {
		Initialized,
		Started,
		Done				
	}
	
	public ProcessState getProcessState();
	
	public double getProcessProgress();

	public String getProcessProgressMessage();
	
	public String getProcessName();
	
	public IFController getOwningController();
	
	public void addProcessListener(IFProcessListener listener);
	
	public void removeProcessListener(IFProcessListener listener);
}
