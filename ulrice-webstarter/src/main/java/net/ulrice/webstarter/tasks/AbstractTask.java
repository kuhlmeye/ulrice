package net.ulrice.webstarter.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.webstarter.ProcessThread;

public abstract class AbstractTask implements IFTask {

	private Map<String, String> parameters = new HashMap<String, String>();
	
	private List<IFTask> subTasks = new ArrayList<IFTask>();

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	
	@Override
	public void addParameter(String key, String value) {
		parameters.put(key, value);	
	}
	
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	@Override
	public void addSubTask(IFTask task) {
		subTasks.add(task);
	}	
	
	protected List<IFTask> getSubTasks() {
		return subTasks;
	}
	
	@Override
	public int getSubTaskCounter() {
		
		int result = 0;
		if(subTasks != null) {
			for(IFTask subTask : subTasks) {
				result += subTask.getSubTaskCounter() + 1;
			}
		}
		
		return result;
	}
	
	@Override
	public void doTask(ProcessThread thread) {
		thread.fireTaskStarted(this, "");
		executeTask(thread);
		thread.fireTaskFinished(this);
	}

	public void executeTask(ProcessThread thread) {
		
	}
	
}
