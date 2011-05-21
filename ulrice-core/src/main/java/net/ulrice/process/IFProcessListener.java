package net.ulrice.process;

import java.util.EventListener;

public interface IFProcessListener extends EventListener {

	/**
	 * Called, if the state of the process changed.
	 * 
	 * @param process The process.
	 */
	public void stateChanged(IFBackgroundProcess process);
	
	/**
	 * Called, if the progress of the process changed.
	 * 
	 * @param process The process.
	 */
	public void progressChanged(IFBackgroundProcess process);
}
