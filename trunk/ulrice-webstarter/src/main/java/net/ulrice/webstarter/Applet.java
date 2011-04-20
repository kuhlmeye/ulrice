package net.ulrice.webstarter;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.ulrice.webstarter.tasks.IFTask;

import org.xml.sax.SAXException;

/**
 * Ulrice applet.
 * 
 * @author christof
 */
public class Applet extends java.applet.Applet implements IFProcessEventListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 7441995340585975668L;
	private AppletComponent view;
	
	private static final Logger LOG = Logger.getLogger(Applet.class.getName());

	@Override
	public void init() {
		super.init();

		
		
		
		String applicationUrl = getParameter("applicationUrl");
		String userId = getParameter("userId");
		String cookieString = getParameter("cookie");

		
		
		
		view = new AppletComponent();
		setLayout(new BorderLayout());
		add(view, BorderLayout.CENTER);

		if (applicationUrl != null) {
			try {
				URL url = new URL(getCodeBase(), applicationUrl);
				LOG.info("Using Application Url: " + url.toString());
				
				XMLDescriptionReader reader = new XMLDescriptionReader(url.openStream(), null);
				ApplicationDescription appDescription = new ApplicationDescription();
				
				appDescription.setId(applicationUrl);
				reader.parseXML(appDescription);

				view.setApplString(appDescription.getName());
				ProcessThread thread = new ProcessThread(appDescription);
				thread.getContext().setUserId(userId);
				
				if (cookieString != null) {

					StringTokenizer tok = new StringTokenizer(cookieString, ";");
					while (tok.hasMoreTokens()) {
						String cookiePart = tok.nextToken();
						String[] cookieParts = cookiePart.split("=");
						String cookieKey = cookieParts[0].trim();
						String cookieValue = null;
						if (cookieParts.length > 1) {
							cookieValue = cookieParts[1].trim();
						}
						if (!"expires".equalsIgnoreCase(cookieKey) && !"domain".equalsIgnoreCase(cookieKey)
								&& !"path".equalsIgnoreCase(cookieKey) && !"secure".equalsIgnoreCase(cookieKey)) {
							thread.getContext().getCookieMap().put(cookieKey, cookieValue);
						}
					}
				}

				thread.addProcessEventListener(this);
				thread.startProcess();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void taskStarted(ProcessThread thread, IFTask task) {

		int taskQueueSize = thread.getTaskQueueSize();
		int numberOfCurrentTask = thread.getNumberOfCurrentTask() + 1;

		view.setGlobalString(task.getName());
		view.setGlobalProgress((int) (100.0 / numberOfCurrentTask * taskQueueSize));
		view.repaint(20);
	}

	@Override
	public void taskFinished(ProcessThread thread, IFTask task) {
		view.setTaskString("");
		view.setTaskProgress(0);
		view.repaint(20);
	}

	@Override
	public void taskProgressed(ProcessThread thread, IFTask task, int progress, String shortMessage, String longMessage) {
		view.setTaskProgress(progress);
		view.setTaskString(shortMessage);
		view.repaint(20);
	}

	@Override
	public void handleError(ProcessThread thread, IFTask task, String shortMessage, String longMessage) {
		view.setError(shortMessage);
	}

	@Override
	public void allTasksFinished(ProcessThread processThread) {
	}

}
