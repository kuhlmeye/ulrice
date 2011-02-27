package net.ulrice.webstarter.tasks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.ulrice.webstarter.ApplicationDescription;
import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;
import net.ulrice.webstarter.XMLDescriptionReader;

import org.xml.sax.SAXException;

public class ReadTasks extends AbstractTask {

	private static final String URL_PARAM_NAME = "descriptionUrl";

	@Override
	public boolean doTask(ProcessThread thread) {

		List<TaskDescription> tasks = loadTasks(thread);
		if (tasks != null) {

			try {

				List<IFTask> subTaskList = new ArrayList<IFTask>();
				while (tasks.size() > 0) {
					TaskDescription taskDescr = tasks.remove(0);
					IFTask task = taskDescr.instanciateTask();
					subTaskList.add(task);
				}
				thread.addSubTasks(this, subTaskList.toArray(new IFTask[subTaskList.size()]));

			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	private List<TaskDescription> loadTasks(ProcessThread thread) {
		String urlStr = getParameterAsString(URL_PARAM_NAME);

		if (urlStr == null) {
			return null;
		}

		try {

			thread.fireTaskProgressed(this, 40, "Connecting..", "Connecting to " + urlStr);
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				String cookieString = thread.getContext().getCookieAsString();
				if (cookieString != null) {
					httpConnection.setRequestProperty("Cookie", cookieString);
				}
			}
			connection.connect();


			thread.fireTaskProgressed(this, 60, "Reading properties..", null);

			XMLDescriptionReader reader = new XMLDescriptionReader(connection.getInputStream(), null);
			ApplicationDescription descr = thread.getAppDescription();
			reader.parseXML(descr);

			return descr.getTasks();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
