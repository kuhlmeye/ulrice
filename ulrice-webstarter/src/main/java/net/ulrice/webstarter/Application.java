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
						XMLDescriptionReader reader = new XMLDescriptionReader(new FileInputStream(file),
								appDirectoryName);
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
			frame.getUserIdField().setText(appSettings.getProperty("UserId"));
			frame.setSelectedApplication(appSettings.getProperty("Application"));
		} catch (FileNotFoundException e) {
			LOG.log(Level.FINE, "Settings-file not found. Ignoring.", e);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Error reading application settings.", e);
		}
	}

	private void saveSettings() {
		try {

			appSettings.put("UserId", frame.getUserIdField().getText());
			appSettings.put("Application", frame.getSelectedApplication().getId());
			appSettings.store(new FileOutputStream("webstarter.properties"), "");
		} catch (FileNotFoundException e) {
			LOG.log(Level.WARNING, "Error saving application settings.", e);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Error saving application settings.", e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ApplicationFrame.START_CMD.equals(e.getActionCommand())) {
			startProcess();
		} else if (ApplicationFrame.CANCEL_CMD.equals(e.getActionCommand())) {
			frame.dispose();
		}
	}

	private void startProcess() {
		frame.getStartButton().setEnabled(false);

		saveSettings();

		String userId = frame.getUserIdField().getText();
		String password = new String(frame.getPasswordField().getPassword());

		ApplicationDescription appDescription = frame.getSelectedApplication();
		thread = new ProcessThread(appDescription);
		thread.getContext().setUserId(userId);
		thread.getContext().setPassword(password);
		thread.addProcessEventListener(this);
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
		frame.getStartButton().setEnabled(true);
		frame.appendMessage("Error: " + shortMessage + "\n" + longMessage);

		frame.getTaskProgress().setBackground(new Color(250, 150, 150));
		frame.getTaskProgress().getModel().setValue(0);
		frame.getTaskProgress().setString(shortMessage);
	}
	

	public static void main(String[] args) {
		new Application();
	}
}
