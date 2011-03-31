package net.ulrice.webstarter.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.ProcessThread;

/**
 * Starts a java application
 * 
 * @author christof
 */
public class StartApplication extends AbstractTask {

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(StartApplication.class.getName());
	
	/** Parameter containing the main class value. */
	private static final String PARAM_MAIN_CLASS = "mainClass";
	
	/** Optional parameter containing a location jre path. */
	private static final String PARAM_LOCAL_JRE = "localJre";

	@Override
	public boolean doTask(ProcessThread thread) {

		List<String> classPath = thread.getContext().getClassPath();

		StringBuffer commandBuffer = new StringBuffer();
		String localDir = thread.getAppDescription().getLocalDir();
		String localJre = getParameterAsString(PARAM_LOCAL_JRE);
		if (localJre != null) {
			commandBuffer.append(localDir).append(localJre).append(File.separator).append("bin").append(File.separator);
		}
		commandBuffer.append("java ");

		commandBuffer.append("-cp ");
		for (String element : classPath) {
			commandBuffer.append(element).append(File.pathSeparator);
		}
		commandBuffer.append(" ");

		String mainClass = getParameterAsString(PARAM_MAIN_CLASS);
		if (mainClass != null) {
			commandBuffer.append(mainClass);
		}

		List<String> appParameters = thread.getAppDescription().getAppParameters();
		if (appParameters != null) {
			for (String appParameter : appParameters) {

				String param = replacePlaceholders(thread, appParameter);
				if (param != null) {
					commandBuffer.append(" ").append(param);
				}
			}
		}

		try {			
			// Start application			
			Process process = Runtime.getRuntime().exec(commandBuffer.toString(), null, new File(localDir));						
			StreamGobbler isGobbler = new StreamGobbler("OUT", process.getInputStream(), System.out);
			StreamGobbler esGobbler = new StreamGobbler("ERR", process.getErrorStream(), System.out);
			isGobbler.start();
			esGobbler.start();						
		} catch (IOException e) {
			thread.handleError(this, "Error starting application.", "Error starting application: " + e.getMessage());
			LOG.log(Level.SEVERE, "IOException during application startup", e);
		}


		return true;
	}

	protected String replacePlaceholders(ProcessThread thread, String appParameter) {
		appParameter = appParameter.replace("${USERID}", thread.getContext().getUserId());

		int cookieIdxStart = appParameter.indexOf("${COOKIE=");
		int cookieIdxEnd = appParameter.indexOf("}", cookieIdxStart);
		if (cookieIdxStart >= 0 && cookieIdxEnd >= 0) {
			String key = appParameter.substring(cookieIdxStart, cookieIdxEnd + 1);
			String cookiePlaceholder = appParameter.substring(cookieIdxStart + "${COOKIE=".length(), cookieIdxEnd);
			String replacement = thread.getContext().getCookieMap().get(cookiePlaceholder);
			if (replacement == null) {
				return null;
			}
			appParameter = appParameter.replace(key, replacement);
		}
		return appParameter;
	}

	class StreamGobbler extends Thread {

		InputStream is;
		OutputStream os;
		String name;

		public StreamGobbler(String name, InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
			this.name = name;
		}

		@Override
		public void run() {

			PrintWriter pw = new PrintWriter(os);
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);

			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					pw.println(name + " > " + line);
				}
			} catch (IOException e) {
				e.printStackTrace(pw);
			}
		}
	}
}
