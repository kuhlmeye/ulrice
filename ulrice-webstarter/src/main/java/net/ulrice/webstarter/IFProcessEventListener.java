package net.ulrice.webstarter;

import java.util.EventListener;

import net.ulrice.webstarter.tasks.IFTask;

public interface IFProcessEventListener extends EventListener {

		
	void taskFinished(ProcessThread thread, IFTask task);
	
	void taskStarted(ProcessThread thread, IFTask task);

	void taskProgressed(ProcessThread thread, IFTask task, int progress, String shortMessage, String longMessage);
	
	void handleError(ProcessThread thread, IFTask task, String shortMessage, String longMessage);
}
