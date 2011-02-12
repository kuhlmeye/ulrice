package net.ulrice.webstarter;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.ulrice.webstarter.tasks.DownloadFile;
import net.ulrice.webstarter.tasks.IFTask;

public class ProcessThread {

	private ProcessContext context;

	private List<IFTask> tasks;
	
	private EventListenerList eventListeners = new EventListenerList();


	public ProcessThread() {
		this.context = new ProcessContext();
		this.tasks = new ArrayList<IFTask>();
	}

	public void addTask(IFTask task) {
		tasks.add(task);
	}

	public void startProcess() {

		if (tasks != null) {
			for (IFTask task : tasks) {
				task.doTask(this);
			}
		}
	}


	/**
	 * @return the context
	 */
	public ProcessContext getContext() {
		return context;
	}

	public void addProcessEventListener(IFProcessEventListener eventListener) {
		eventListeners.add(IFProcessEventListener.class, eventListener);
	}

	public void removeProcessEventListener(IFProcessEventListener eventListener) {
		eventListeners.remove(IFProcessEventListener.class, eventListener);
	}


	public int getTaskCounter() {
		int taskCounter = 0; 
		if(tasks != null) {
			for(IFTask task : tasks) {
				taskCounter += task.getSubTaskCounter() + 1;
			}
		}		
		return taskCounter;
	}


	public void fireTasksLoadedEvent() {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.tasksLoaded();
				}
			}
		}
	}

	public void fireTaskStarted(IFTask task, String message) {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.taskStarted(task, message);
				}
			}
		}
	}

	public void fireTaskFinished(IFTask task) {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.taskFinished(task);
				}
			}
		}
	}

	public void fireTaskProgressed(IFTask task, String message) {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.taskProgressed(task, message);
				}
			}
		}
	}

}
