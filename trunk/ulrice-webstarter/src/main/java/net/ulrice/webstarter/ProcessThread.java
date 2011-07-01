package net.ulrice.webstarter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import net.ulrice.webstarter.tasks.IFTask;

/**
 * The thread downloading and starting the application.
 * 
 * @author christof
 */
public class ProcessThread {

	private static final Logger LOG = Logger.getLogger(ProcessThread.class.getName());

	/** The description of the application stated with this thread. */
	private ApplicationDescription appDescription;

	/** The thread handling the execution of the tasks. */
	private Thread taskWorker;

	private ProcessContext context;

	private EventListenerList eventListeners = new EventListenerList();

	/** The task queue. */
	private List<IFTask> taskQueue;

	private int numberOfCurrentTask;

	private boolean threadStopped = false;

	private Properties appSettings = new Properties();
	
	public ProcessThread(ApplicationDescription appDescription) {
		this.context = new ProcessContext();
		this.appDescription = appDescription;
	}

	/**
	 * Starts the startup process of the application.
	 */
	public void startProcess() {
		this.taskQueue = new ArrayList<IFTask>();
		taskWorker = new Thread(new StartupProcess());
		taskWorker.start();
	}

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
		if (taskPos == -1) {
			taskPos = taskQueue.size() > 0 ? taskQueue.size() - 1 : 0;
		}

		if (taskPos >= 0) {
			taskPos++;
		}

		if (subtasks != null) {
			for (IFTask subtask : subtasks) {
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

	public void fireAllTasksFinished() {
		if (eventListeners != null) {
			IFProcessEventListener[] listeners = eventListeners.getListeners(IFProcessEventListener.class);
			if (listeners != null) {
				for (IFProcessEventListener listener : listeners) {
					listener.allTasksFinished(this);
				}
			}
		}
	}

	public ApplicationDescription getAppDescription() {
		return appDescription;
	}

	/**
	 * Process handling the tasks.
	 * 
	 * @author christof
	 */
	private final class StartupProcess implements Runnable {

		@Override
		public void run() {

			File propertyFile = new File(getAppDescription().getLocalDir(), "ulrice-webstarter.properties");
			try {
				getContext().getPersistentProperties().load(new FileInputStream(propertyFile));
			} catch (FileNotFoundException e) {
				LOG.warning("Could not found property file " + propertyFile + ". Creating empty file.");
			} catch (IOException e) {
				LOG.log(Level.WARNING, "Error reading from property file.", e);
			}

			System.setProperty("http.proxySet", Boolean.toString(getAppDescription().isUseProxy()));
			if (getAppDescription().isUseProxy() && System.getProperty("http.proxyUser") != null) {
				LOG.info("Using Proxy:\n-Host: " + System.getProperty("http.proxyHost") + ":" + System.getProperty("http.proxyPort")
						+ "\n-User: " + System.getProperty("http.proxyUser"));
				Authenticator.setDefault(new ProxyAuthenticator(System.getProperty("http.proxyUser"), System
						.getProperty("http.proxyPassword")));
			} else {
				Authenticator.setDefault(null);
			}

			if (taskQueue != null && !threadStopped) {

				// Prefill the task-queue
				fillTaskQueue();
				for (numberOfCurrentTask = 0; numberOfCurrentTask < taskQueue.size() && !threadStopped; numberOfCurrentTask++) {

					// Execute next task.
					IFTask task = taskQueue.get(numberOfCurrentTask);
					fireTaskStarted(task);
					if (!task.doTask(ProcessThread.this)) {
						break;
					}
					fireTaskFinished(task);

					// Instanciate subtasks.
					fillTaskQueue();
				}
				fireAllTasksFinished();

			}

			try {
				getContext().getPersistentProperties().store(new FileOutputStream(propertyFile), "");
			} catch (FileNotFoundException e) {
				LOG.warning("Could not found property file " + propertyFile + ". Creating empty file.");
			} catch (IOException e) {
				LOG.log(Level.WARNING, "Error reading from property file.", e);
			}

		}

		private void fillTaskQueue() {
			if (appDescription != null && appDescription.getTasks() != null) {
				List<TaskDescription> tasks = appDescription.getTasks();
				while (tasks.size() > 0) {
					TaskDescription taskDescription = tasks.remove(0);
					try {
						taskQueue.add(taskDescription.instanciateTask());
					} catch (InstantiationException e) {
						LOG.log(Level.SEVERE, "Error instanciating task.", e);
					} catch (IllegalAccessException e) {
						LOG.log(Level.SEVERE, "Error instanciating task.", e);
					}
				}
			}
		}
	}

	public void cancelProcess() {
		threadStopped = true;
	}
	

	public Properties getAppSettings() {
		return appSettings;
	}

	public void setAppSettings(Properties appSettings) {
		this.appSettings = appSettings;
	}

}
