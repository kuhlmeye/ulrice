package net.ulrice.webstarter.tasks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class ReadDescription extends AbstractTask {

	private static final String URL_PARAM_NAME = "descriptionUrl";
	private static final String BASE_URL = "baseUrl";

	@Override
	public void executeTask(ProcessThread thread) {
		String baseUrl = getParameter(BASE_URL);

		Properties prop = loadProperties(thread.getContext());
		if(prop != null) {
			
			List<String> files = new LinkedList<String>();
			for(int i = 0; prop.containsKey("RemoteFile." + i); i++) {
				String file = prop.getProperty("RemoteFile." + i);
				files.add(file.substring(0, file.indexOf(',')));
			}			
			
			for(String file : files) {
				
				try {
					thread.getContext().setValue(ProcessContext.CURRENT_FILE_URL, new URL(baseUrl + file));
					thread.getContext().setValue(ProcessContext.CURRENT_FILE_NAME, file);
					
					for(IFTask subTask : getSubTasks()) {
						subTask.doTask(thread);
					}
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
	}

	private Properties loadProperties(ProcessContext context) {
		String urlStr = getParameter(URL_PARAM_NAME);

		if (urlStr == null) {
			return null;
		}

		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
			String cookieString = context.getValueAsString(ProcessContext.COOKIE);
			if (cookieString != null) {
				con.setRequestProperty("Cookie", cookieString);
			}
            con.connect();
            
			
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
