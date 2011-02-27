package net.ulrice.webstarter.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;

public abstract class AbstractTask implements IFTask {

	private Map<String, Object> parameters = new HashMap<String, Object>();

	private List<TaskDescription> subTasks = new ArrayList<TaskDescription>();

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void addParameter(String key, Object value) {
		parameters.put(key, value);
	}

	public Object getParameter(String key) {
		return parameters.get(key);
	}

	public Object getParameter(String key, Object defaultValue) {
		Object object  = parameters.get(key);
		return object == null ? defaultValue : object;
	}

	@Override
	public void addSubTask(TaskDescription task) {
		subTasks.add(task);
	}

	protected List<TaskDescription> getSubTasks() {
		return subTasks;
	}


	public String getParameterAsString(String key) {
		Object value = getParameter(key);
		return value != null ? value.toString() : null;
	}
	
	public String getParameterAsString(String key, String defaultValue) {
		Object value = getParameter(key);
		return value != null ? value.toString() : defaultValue;
	}

	@Override
	public abstract boolean doTask(ProcessThread thread);


}
