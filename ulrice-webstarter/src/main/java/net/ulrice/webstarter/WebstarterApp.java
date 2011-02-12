package net.ulrice.webstarter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.xml.sax.SAXException;

import net.ulrice.webstarter.tasks.IFTask;

public class WebstarterApp extends JFrame implements IFProcessEventListener {

	private JProgressBar globalProgress;
	
	private JProgressBar taskProgress;
	
	private JProgressBar subProgress;

	private ProcessThread thread;
	
	public WebstarterApp() {
		this.globalProgress = new JProgressBar();
		this.globalProgress.setStringPainted(true);
		this.taskProgress = new JProgressBar();
		this.taskProgress.setStringPainted(true);
		this.subProgress = new JProgressBar();
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(globalProgress);
		getContentPane().add(taskProgress);
		getContentPane().add(subProgress);
		
		try {
			thread = new ProcessThread();
			thread.addProcessEventListener(this);

			XMLDescriptionReader reader = new XMLDescriptionReader(new FileInputStream("Test.xml"));
			reader.parseXML(thread);
			
			thread.startProcess();
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
		
		pack();
		setVisible(true);
	}

	@Override
	public void taskFinished(IFTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskProgressed(IFTask task, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskStarted(IFTask task, String message) {
		globalProgress.setValue(globalProgress.getValue() + 1);
		globalProgress.setString(task.getName());
		taskProgress.setString(message);
		System.out.println("Started: " + task.getName());
	}

	@Override
	public void tasksLoaded() {
		globalProgress.setMinimum(0);
		globalProgress.setMaximum(thread.getTaskCounter());
		getContentPane().invalidate();
		getContentPane().repaint();		
		System.out.println("Size: " + thread.getTaskCounter());
	}

	
	public static void main(String[] args) {
		new WebstarterApp();
	}
}
