package net.ulrice.webstarter.tasks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.ulrice.webstarter.Placeholder;
import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;

public class ReadDescription extends AbstractTask {

	private static final String URL_PARAM_NAME = "descriptionUrl";
	private static final String BASE_URL = "baseUrl";
	
	public static final String CURRENT_FILE_URL = "CURRENT_FILE_URL";
	
	@Override
	public boolean doTask(ProcessThread thread) {
		String baseUrl = getParameterAsString(BASE_URL);

		Properties prop = loadProperties(thread);
		if(prop != null) {
			
			List<String> files = new LinkedList<String>();
			for(int i = 0; prop.containsKey("RemoteFile." + i); i++) {
				String file = prop.getProperty("RemoteFile." + i);
				files.add(file.substring(0, file.indexOf(',')));
			}			
			
			List<IFTask> subTaskList = new ArrayList<IFTask>();
			for(String file : files) {
				String fileUrlStr = baseUrl + file;
				
				try {

					

					thread.fireTaskProgressed(this, 60, "Instanciating subtasks..", null);
					for(TaskDescription subTask : getSubTasks()) {						
						Placeholder placeholder = new Placeholder("${FILE_URL}", fileUrlStr);						
						IFTask task = subTask.instanciateTask(placeholder);		
						subTaskList.add(task);
					}
					
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								
			}
			thread.addSubTasks(this, subTaskList.toArray(new IFTask[subTaskList.size()]));
		}

		return true;
	}

	private Properties loadProperties(ProcessThread thread) {
		String urlStr = getParameterAsString(URL_PARAM_NAME);

		if (urlStr == null) {
			return null;
		}

		try {

			thread.fireTaskProgressed(this, 40, "Connecting..", "Connecting to " + urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
			String cookieString = thread.getContext().getCookieAsString();
			if (cookieString != null) {
				con.setRequestProperty("Cookie", cookieString);
			}
            con.connect();
            

			thread.fireTaskProgressed(this, 60, "Reading properties..", null);
			Properties prop = new Properties();
			prop.load(con.getInputStream());
			
			return prop;
						
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
