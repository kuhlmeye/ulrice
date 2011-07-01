package net.ulrice.webstarter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.tasks.IFTask;

import org.xml.sax.SAXException;

/**
 * Client application of the webstarter.
 * 
 * @author christof
 */
public class Application implements IFProcessEventListener, ActionListener {

	private static final Logger LOG = Logger.getLogger(Application.class.getName());

	/**
	 * Constant of the sub-directory containing the application description xml
	 * files.
	 */
	private static final String appDirectoryName = "applications";

	/** The main thread in which the webstarter process runs. */
	private ProcessThread thread;

	/** The frame in which the webstarter is displayed. */
	private ApplicationFrame frame;

	/** The application setting properties. */
	private Properties appSettings;

	private boolean isInErrorState = false;

	public Application() {

		frame = new ApplicationFrame();
		frame.getStartButton().addActionListener(this);
		frame.getCancelButton().addActionListener(this);

		try {

			File appDirectory = new File(appDirectoryName);
			File[] files = appDirectory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().endsWith(".ws.xml")) {
						XMLDescriptionReader reader = new XMLDescriptionReader(new FileInputStream(file), appDirectoryName);
						ApplicationDescription appDescription = new ApplicationDescription();
						appDescription.setId(file.getName());
						reader.parseXML(appDescription);
						frame.addApplication(appDescription);
					}
				}
			}

		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "Configuration File not found.", e);
		} catch (SAXException e) {
			LOG.log(Level.SEVERE, "Error parsing configuration file.", e);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error reading configuration file.", e);
		}

		loadSettings();
	}

	private void loadSettings() {
		appSettings = new Properties();
		try {
			appSettings.load(new FileInputStream("webstarter.properties"));
			String userId = appSettings.getProperty("UserId");
			frame.getUserIdField().setText(userId);
			frame.setSelectedApplication(appSettings.getProperty("Application"));

			String proxyHost = appSettings.getProperty("http.proxyHost");
			String proxyPort = appSettings.getProperty("http.proxyPort");
			String proxyUser = appSettings.getProperty("http.proxyUser");
			String proxyPassword = appSettings.getProperty("http.proxyPassword");

			if (proxyHost != null && !"".equals(proxyHost)) {
				System.setProperty("http.proxyHost", proxyHost);
				System.setProperty("https.proxyHost", proxyHost);
			}

			if (proxyPort != null && !"".equals(proxyPort)) {
				System.setProperty("http.proxyPort", proxyPort);
				System.setProperty("https.proxyPort", proxyPort);
			}

			if (proxyUser != null && !"".equals(proxyUser)) {
				System.setProperty("http.proxyUser", proxyUser);
				System.setProperty("https.proxyUser", proxyUser);
			}

			if (proxyPassword != null && !"".equals(proxyPassword)) {
				proxyPassword = EncryptionUtils.decrypt(proxyPassword);
				if(proxyPassword != null) {
					System.setProperty("http.proxyPassword", proxyPassword);
					System.setProperty("https.proxyPassword", proxyPassword);
				}
			}

		} catch (FileNotFoundException e) {
			LOG.log(Level.FINE, "Settings-file not found. Ignoring.", e);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Error reading application settings.", e);
		}
	}

	private void saveSettings() {
		try {
			appSettings.clear();
			
			String userId = frame.getUserIdField().getText();
			String application = frame.getSelectedApplication().getId();
			String proxyHost = System.getProperty("http.proxyHost");
			String proxyPort = System.getProperty("http.proxyPort");
			String proxyUser = System.getProperty("http.proxyUser");
			String proxyPass = System.getProperty("http.proxyPassword");

			if(userId != null) {
				appSettings.put("UserId", userId);
			}
			
			if(application != null) {
				appSettings.put("Application", application);
			}

			if(proxyHost != null) {
				appSettings.put("http.proxyHost", proxyHost);
			}
			
			if(proxyPort != null) {
				appSettings.put("http.proxyPort", proxyPort);
			}
			
			if(proxyUser != null) {
				appSettings.put("http.proxyUser", proxyUser);
			}
			
			if(proxyPass != null) {
				appSettings.put("http.proxyPassword", EncryptionUtils.encrypt(proxyPass));
			}

			appSettings.store(new FileOutputStream("webstarter.properties"), "");
		} catch (FileNotFoundException e) {
			LOG.log(Level.WARNING, "Error saving application settings.", e);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Error saving application settings.", e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		saveSettings();
		if (ApplicationFrame.START_CMD.equals(e.getActionCommand())) {
			frame.getPasswordField().setEnabled(false);
			frame.getUserIdField().setEnabled(false);
			frame.getApplicationChooser().setEnabled(false);
			frame.getStartButton().setEnabled(false);
			startProcess();
		} else if (ApplicationFrame.CANCEL_CMD.equals(e.getActionCommand())) {
			frame.dispose();
			if (thread != null) {				
				thread.cancelProcess();
			}
		}
	}

	private void startProcess() {
		frame.getStartButton().setEnabled(false);

		String userId = frame.getUserIdField().getText();
		String password = new String(frame.getPasswordField().getPassword());

		ApplicationDescription appDescription = frame.getSelectedApplication();
		if (thread == null) {
			thread = new ProcessThread(appDescription);
			thread.addProcessEventListener(this);
		} else {
			appDescription.restoreTasks();
		}
		thread.getContext().setUserId(userId);
		thread.getContext().setPassword(password);

		isInErrorState = false;
		frame.setProgressError(false);
		appDescription.backupTasks();
		thread.startProcess();
	}

	@Override
	public void taskFinished(ProcessThread thread, IFTask task) {
		frame.getGlobalProgress().getModel().setMaximum(thread.getTaskQueueSize());
		frame.getGlobalProgress().getModel().setValue(thread.getNumberOfCurrentTask());

		frame.getTaskProgress().getModel().setValue(100);
		frame.getTaskProgress().setString("");
	}

	@Override
	public void taskProgressed(ProcessThread thread, IFTask task, int progress, String shortMessage, String longMessage) {
		frame.getTaskProgress().getModel().setValue(progress);
		frame.getTaskProgress().setString(shortMessage);

		if (longMessage != null) {
			frame.appendMessage(longMessage + "\n");
		}
	}

	@Override
	public void taskStarted(ProcessThread thread, IFTask task) {
		int taskQueueSize = thread.getTaskQueueSize();
		int numberOfCurrentTask = thread.getNumberOfCurrentTask() + 1;

		frame.getGlobalProgress().getModel().setMinimum(0);
		frame.getGlobalProgress().getModel().setMaximum(taskQueueSize);
		frame.getGlobalProgress().getModel().setValue(numberOfCurrentTask);
		frame.getGlobalProgress().setString(task.getName() + " (" + numberOfCurrentTask + "/" + taskQueueSize + ")");

		frame.getTaskProgress().getModel().setMinimum(0);
		frame.getTaskProgress().getModel().setMaximum(100);
		frame.getTaskProgress().getModel().setValue(0);
		frame.getTaskProgress().setString("");
	}

	@Override
	public void handleError(ProcessThread thread, IFTask task, String shortMessage, String longMessage) {
		isInErrorState = true;
		frame.getPasswordField().setEnabled(frame.getSelectedApplication().isNeedsLogin());
		frame.getUserIdField().setEnabled(frame.getSelectedApplication().isNeedsLogin());
		frame.getApplicationChooser().setEnabled(true);

		frame.getStartButton().setEnabled(true);
		frame.appendMessage("Error: " + shortMessage + "\n" + longMessage);

		frame.setProgressError(true);
		frame.getTaskProgress().getModel().setValue(0);
		frame.getTaskProgress().setString(shortMessage);
	}

	public static void main(String[] args) {
		Application app = new Application();
		app.parseArguments(args);
		app.frame.setVisible(true);
	}

	private void parseArguments(String[] args) {
		if (args != null) {
			ApplicationDescription appDescription = null;
			String userId = null;
			String password = null;

			for (String arg : args) {
				if ("-h".equalsIgnoreCase(arg) || "--help".equalsIgnoreCase(arg) || "/?".equalsIgnoreCase(arg)) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("Ulrice-Webstarter");
					buffer.append("Usage: net.ulrice.webstarter.Application <param>*\n");
					buffer.append(" Param: \n");
					buffer.append("  -h, --help, /?             Show this help.");
					buffer.append("  -userId=<userId>           Set the username used for the login.\n");
					buffer.append("  -password=<password>       Set the password used for the login.\n");
					buffer.append("  -application=<application> Start application. Application is the name of the .ws.xml file without extension.\n");
					System.out.println(buffer.toString());
				} else if (arg.indexOf('=') >= 0) {
					String[] split = arg.split("=");
					if (split.length > 1) {
						String key = split[0];
						String value = split[1];

						if ("-userId".equalsIgnoreCase(key)) {
							userId = value;
						} else if ("-password".equalsIgnoreCase(key)) {
							password = value;
						} else if ("-application".equalsIgnoreCase(key)) {
							String application = value;
							File appDescr = new File(appDirectoryName, application + ".ws.xml");
							if (appDescr.exists()) {
								try {
									XMLDescriptionReader reader = new XMLDescriptionReader(new FileInputStream(appDescr), appDirectoryName);
									appDescription = new ApplicationDescription();
									appDescription.setId(appDescr.getName());
									reader.parseXML(appDescription);
								} catch (FileNotFoundException e) {
									LOG.log(Level.WARNING, "Error loading application description.", e);
								} catch (SAXException e) {
									LOG.log(Level.WARNING, "Error parsing application description.", e);
								} catch (IOException e) {
									LOG.log(Level.WARNING, "Error parsing application description.", e);
								}
							}
						}
					}
				}
			}

			if (appDescription != null && (!appDescription.isNeedsLogin() || userId != null)) {
				frame.setShowApplicationDialog(false);
				thread = new ProcessThread(appDescription);
				thread.getContext().setUserId(userId);
				thread.getContext().setPassword(password);
				thread.addProcessEventListener(this);
				thread.startProcess();
			}
		}
	}

	@Override
	public void allTasksFinished(ProcessThread processThread) {
		if (!isInErrorState) {
			frame.dispose();
		}
	}
}
