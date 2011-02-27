package net.ulrice.webstarter;

import java.util.EventListener;

import net.ulrice.webstarter.tasks.IFTask;

/**
 * Interface for the process listeners. 
 * 
 * @author christof
 */
public interface IFProcessEventListener extends EventListener {

	/**
	 * Called, if a task was started in a thread.
	 * 
	 * @param thread The thread in which the task was started.
	 * @param task The task that was started.
	 */
	void taskStarted(ProcessThread thread, IFTask task);
		
	/**
	 * Called, if a task has been finished.
	 *  
	 * @param thread The thread in which the task ran
	 * @param task The task that was finished.
	 */
	void taskFinished(ProcessThread thread, IFTask task);

	/**
	 * Called, if a task progressed.
	 * 
	 * @param thread The thread of the task.
	 * @param task The task that made the progress.
	 * @param progress The process as a value between 0 and 100
	 * @param shortMessage A short description of the progress.
	 * @param longMessage A long description of the progress.
	 */
	void taskProgressed(ProcessThread thread, IFTask task, int progress, String shortMessage, String longMessage);
	
	/**
	 * Called, if an error ocurred in a task.
	 * 
	 * @param thread The thread of the task.
	 * @param task The task in which the error ocurred.
	 * @param shortMessage A short description of the error.
	 * @param longMessage The long description of the error.
	 */
	void handleError(ProcessThread thread, IFTask task, String shortMessage, String longMessage);
}
