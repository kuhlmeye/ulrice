package net.ulrice.webstarter.tasks;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class StartApplication extends AbstractTask {

	@Override
	public boolean doTask(ProcessThread thread) {
		
		LinkedList<String> classPath = thread.getContext().getValue(ProcessContext.CLASSPATH, new LinkedList<String>());
		
		StringBuffer commandBuffer = new StringBuffer();
		String localDir = thread.getAppDescription().getLocalDir();
		String localJre = getParameterAsString("localJre");
		if(localJre != null) {
			commandBuffer.append(localDir).append(localJre).append(File.separator).append("bin").append(File.separator).append("java ");
		}
		
		commandBuffer.append("-cp ");
		for(String element : classPath) {
			commandBuffer.append(element).append(";");
		}
		commandBuffer.append(" ");


		String mainClass = getParameterAsString("mainClass");
		if(mainClass != null) {
			commandBuffer.append(mainClass);
		}
		
		List<String> appParameters = thread.getAppDescription().getAppParameters();
		if(appParameters != null) {
			for(String appParameter : appParameters) {

				String param = replacePlaceholders(thread, appParameter);
				if(param != null)  {
					commandBuffer.append(" ").append(param);
				}
			}
		}
		
		try {
			Process process = Runtime.getRuntime().exec(commandBuffer.toString(), new String[0], new File(localDir));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(commandBuffer);
		
		return true;
	}

	protected String replacePlaceholders(ProcessThread thread, String appParameter) {
		appParameter = appParameter.replace("${USERID}", thread.getContext().getValueAsString(ProcessContext.USERID, ""));
//		appParameter = appParameter.replace("${PD-H-SESSION-ID}", thread.getContext().getValueAsString(ProcessContext.COOKIE));
//		appParameter = appParameter.replace("${PD-H-SESSION-ID}", thread.getContext().getValueAsString(ProcessContext.COOKIE));
//		
		int cookieIdxStart = appParameter.indexOf("${COOKIE=");
		int cookieIdxEnd = appParameter.indexOf("}", cookieIdxStart);
		if(cookieIdxStart >= 0 && cookieIdxEnd >= 0) {
			String key = appParameter.substring(cookieIdxStart, cookieIdxEnd + 1);
			String cookiePlaceholder = appParameter.substring(cookieIdxStart + "${COOKIE=".length(), cookieIdxEnd);
			String replacement = thread.getContext().getCookieMap().get(cookiePlaceholder);
			if(replacement == null) {
				return null;
			}
			appParameter = appParameter.replace(key, replacement);
		}		
		return appParameter;
	}

}
