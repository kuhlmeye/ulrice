package net.ulrice.webstarter.tasks;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;

public interface IFTask  {
	
	String getName();
	
	void addParameter(String key, Object value);
	
	void addSubTask(TaskDescription task);
	
	
	boolean doTask(ProcessThread thread);

	String getParameterAsString(String currentFileName);
	
}
