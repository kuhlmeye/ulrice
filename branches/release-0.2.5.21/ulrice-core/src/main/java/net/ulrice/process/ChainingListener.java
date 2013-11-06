package net.ulrice.process;

/**
 * Process listener starting a job if another one has finished.
 * 
 * @author DL10KUH
 */
public class ChainingListener implements IFProcessListener {

	/** The process executor used to start the chained process. */
	private CtrlProcessExecutor processExecutor;
	
	/** The chained process. */
	private IFBackgroundProcess process;

	public ChainingListener(CtrlProcessExecutor processExecutor, IFBackgroundProcess process) {
		this.processExecutor = processExecutor;
		this.process = process;
	}

	@Override
	public void stateChanged(IFBackgroundProcess finishedProcess) {
		switch (finishedProcess.getProcessState()) {
	    case Cancelled:
		case Done:
			finishedProcess.removeProcessListener(this);
			processExecutor.executeProcess(process);			
			break;
		default:
			break;
		}
	}

	@Override
	public void progressChanged(IFBackgroundProcess process) {
		// Empty on purpose
	}
}
