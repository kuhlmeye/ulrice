package net.ulrice.webstarter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.ulrice.webstarter.tasks.IFTask;
import net.ulrice.webstarter.tasks.TamLogin;

public class ProcessThread {

	private ProcessContext context;

	private ApplicationDescription appDescription;
	
	private EventListenerList eventListeners = new EventListenerList();

	private List<IFTask> taskQueue;

	private int numberOfCurrentTask;

	private Thread taskWorker;

	public ProcessThread(ApplicationDescription appDescription) {
		this.context = new ProcessContext();
		this.appDescription = appDescription;
		this.taskQueue = new ArrayList<IFTask>();
	}

	public void startProcess() {

		
		taskWorker = new Thread(new Runnable() {
			
			@Override
			public void run() {
				

				// TODO Auto-generated method stub
				if(taskQueue != null) {

					fillTaskQueue();

					for(numberOfCurrentTask = 0; numberOfCurrentTask < taskQueue.size(); numberOfCurrentTask++) {

						IFTask task = taskQueue.get(numberOfCurrentTask);
						fireTaskStarted(task);
						if(!task.doTask(ProcessThread.this)) {
							return;
						}
						fireTaskFinished(task);						

						fillTaskQueue();
					}
				}
			}

			private void fillTaskQueue() {
				if(appDescription != null && appDescription.getTasks() != null) {
					List<TaskDescription> tasks = appDescription.getTasks();
					while(tasks.size() > 0) {
						TaskDescription taskDescription = tasks.remove(0);
						try {
							taskQueue.add(taskDescription.instanciateTask());
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		taskWorker.start();
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

	public void addSubTasks(IFTask task, IFTask... subtasks) {
		int taskPos = taskQueue.indexOf(task);
		if(taskPos == -1) {
			taskPos = taskQueue.size() > 0 ? taskQueue.size() - 1 : 0;
		}
		
		if(taskPos > 0) {
			taskPos ++;
		}
		
		if(subtasks != null) {
			for(IFTask subtask : subtasks) { 
				taskQueue.add(taskPos++, subtask);
			}
		}
	}
	
	public int getNumberOfCurrentTask() {
		return numberOfCurrentTask;
	}
	
	public int getTaskQueueSize() {
		return taskQueue.size();
	}
	

	public void handleError(IFTask task, String shortErrorMessage, String longErrorMessage) {		
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.handleError(this, task, shortErrorMessage, longErrorMessage);
				}
			}
		}
	}
	
	public void fireTaskStarted(IFTask task) {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.taskStarted(this, task);
				}
			}
		}
	}

	public void fireTaskFinished(IFTask task) {
		
		
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.taskFinished(this, task);
				}
			}
		}
	}

	public void fireTaskProgressed(IFTask task, int progress, String shortMessage, String longMessage) {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.taskProgressed(this, task, progress, shortMessage, longMessage);
				}
			}
		}
	}

	public ApplicationDescription getAppDescription() {
		return appDescription;
	}
}
