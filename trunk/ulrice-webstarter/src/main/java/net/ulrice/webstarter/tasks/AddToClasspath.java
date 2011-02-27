package net.ulrice.webstarter.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class AddToClasspath extends AbstractTask {

	public static final String FILTER_PARAM = "filter";
	public static final String URL_PARAM = "url";

	
	@Override
	public boolean doTask(ProcessThread thread) {

		String filter = getParameterAsString(FILTER_PARAM);
		String urlStr = getParameterAsString(URL_PARAM);
		String localDirString = thread.getAppDescription().getLocalDir();
		
		URL fileUrl = null;
		if(urlStr != null) {
			try {
				fileUrl = new URL(urlStr);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String file = fileUrl.getFile();
		String[] split = file.split("\\/");
		
		
		String fileName = split[split.length - 1];
		

		if (fileName.matches(filter)) {
			LinkedList<String> classPath = thread.getContext().getValue(ProcessContext.CLASSPATH, new LinkedList<String>());
			classPath.add(localDirString + fileName);
		}
		
		return true;
	}

}
