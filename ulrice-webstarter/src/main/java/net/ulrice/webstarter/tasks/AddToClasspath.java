package net.ulrice.webstarter.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.XMLDescriptionReader;
import net.ulrice.webstarter.util.WebstarterUtils;

public class AddToClasspath extends AbstractTask {

	private static final Logger LOG = Logger.getLogger(XMLDescriptionReader.class.getName());
	public static final String FILTER_PARAM = "filter";
	public static final String URL_PARAM = "url";

	
	@Override
	public boolean doTask(ProcessThread thread) {

		String filter = getParameterAsString(FILTER_PARAM);
		String urlStr = getParameterAsString(URL_PARAM);
		String localDirString = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());
		
		URL fileUrl = null;
		if(urlStr != null) {
			try {
				fileUrl = new URL(urlStr);
			} catch (MalformedURLException e) {
            	LOG.log(Level.SEVERE, "Malformed url.", e);
			}
		}
		
		String file = fileUrl.getFile();
		String[] split = file.split("\\/");
		
		
		String fileName = split[split.length - 1];
		

		if (fileName.matches(filter)) {
			List<String> classPath = thread.getContext().getClassPath();
			classPath.add(localDirString + fileName);
		}
		
		return true;
	}

}
