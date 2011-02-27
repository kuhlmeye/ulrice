package net.ulrice.webstarter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ulrice.webstarter.tasks.IFTask;
import net.ulrice.webstarter.tasks.StartApplication;

public class TaskDescription {

	private Class<? extends IFTask> taskClass;

	private List<TaskDescription> subTasks = new ArrayList<TaskDescription>();
	
	private Map<String, String> parameters; 
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public TaskDescription(Class<? extends IFTask> taskClass, Map<String, String> parameters) {
		this.taskClass = taskClass;
		this.parameters = parameters;
	}
	
	public IFTask instanciateTask(Placeholder... placeholders) throws InstantiationException, IllegalAccessException {
		IFTask task = taskClass.newInstance();
		if(parameters != null) {
			Set<Entry<String,String>> entrySet = parameters.entrySet();
			for(Entry<String, String> entry : entrySet) {
				task.addParameter(entry.getKey(), replacePlaceholders(entry.getValue(), placeholders));
			}
		}
		if(subTasks != null) {
			for(TaskDescription subTask : subTasks) {
				task.addSubTask(subTask);
			}
		}
		
		return task;
	}


	private String replacePlaceholders(String value, Placeholder[] placeholders) {
		if(placeholders != null) {
			for(Placeholder placeholder : placeholders) {
				value = value.replace(placeholder.getKey(), placeholder.getValue());
			}
		}
		return value;
	}

	public void addSubTask(TaskDescription task) {
		subTasks.add(task);
	}

	protected List<TaskDescription> getSubTasks() {
		return subTasks;
	}

	public boolean instanceOf(Class<? extends IFTask> taskClass) {
		return this.taskClass.isAssignableFrom(taskClass);
	}
}
