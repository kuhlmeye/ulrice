package net.ulrice.process;

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
}
