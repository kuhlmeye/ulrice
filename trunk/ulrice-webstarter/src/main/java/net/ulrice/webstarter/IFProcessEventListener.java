package net.ulrice.webstarter;

import java.util.EventListener;
import java.util.List;

import net.ulrice.webstarter.tasks.IFTask;

public interface IFProcessEventListener extends EventListener {

		
	void taskFinished(IFTask task);
	
	void taskStarted(IFTask task, String message);
	

	void tasksLoaded();

	void taskProgressed(IFTask task, String message);
	
}
