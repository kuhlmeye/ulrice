package net.ulrice.webstarter.tasks;

import net.ulrice.webstarter.ProcessThread;

public interface IFTask {
	
	String getName();
	
	void addParameter(String key, String value);
	
	void addSubTask(IFTask task);
	
	int getSubTaskCounter();
	
	void doTask(ProcessThread thread);
	
}
