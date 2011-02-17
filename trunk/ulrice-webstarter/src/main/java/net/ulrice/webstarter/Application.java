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

import net.ulrice.webstarter.tasks.IFTask;

import org.xml.sax.SAXException;

public class Application implements IFProcessEventListener, ActionListener {


	private static final String appDirectoryName = "applications";
	
	private ProcessThread thread;
	private ApplicationFrame frame;

	private Properties appSettings;

	public Application() {				
		
		frame = new ApplicationFrame();
		frame.getStartButton().addActionListener(this);
		frame.getCancelButton().addActionListener(this);
		

		try {

			File appDirectory = new File(appDirectoryName);
			File[] files = appDirectory.listFiles();
			for (File file : files) {
				if (file.getName().endsWith(".ws.xml")) {
					XMLDescriptionReader reader = new XMLDescriptionReader(new FileInputStream(file), appDirectoryName);
					ApplicationDescription appDescription = new ApplicationDescription();
					appDescription.setId(file.getName());
					reader.parseXML(appDescription);
					frame.addApplication(appDescription);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadSettings();
	}

	private void loadSettings() {
		appSettings = new Properties();
		try {
			appSettings.load(new FileInputStream("webstarter.properties"));
			frame.getUserIdField().setText(appSettings.getProperty("UserId"));
			frame.setSelectedApplication(appSettings.getProperty("Application"));
		} catch (FileNotFoundException e1) {
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	

	private void saveSettings() {
		try {

			appSettings.put("UserId", frame.getUserIdField().getText());
			appSettings.put("Application", frame.getSelectedApplication().getId());
			appSettings.store(new FileOutputStream("webstarter.properties"), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Application();
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
		
		ApplicationDescription appDescription = (ApplicationDescription) frame.getSelectedApplication();

		thread = new ProcessThread(appDescription);
		thread.getContext().setValue(ProcessContext.USERID, userId);
		thread.getContext().setValue(ProcessContext.PASSWORD, password);

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
}
