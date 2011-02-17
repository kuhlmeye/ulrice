package net.ulrice.webstarter.tasks;

import java.util.LinkedList;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class AddToClasspath extends AbstractTask {

	private static final String FILTER_NAME = "filter";
	
	@Override
	public boolean doTask(ProcessThread thread) {

		String localPath = getParameterAsString(ReadDescription.LOCAL_PATH);
		String fileName = getParameterAsString(ReadDescription.CURRENT_FILE_NAME);
		
		String filter = getParameterAsString(FILTER_NAME);

		if (fileName.matches(filter)) {
			LinkedList<String> classPath = thread.getContext().getValue(ProcessContext.CLASSPATH, new LinkedList<String>());
			classPath.add(localPath + fileName);
		}
		
		return true;
	}

}
