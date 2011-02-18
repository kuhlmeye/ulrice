package net.ulrice.webstarter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class ApplicationDescription {

	private String id;
	
	private String name;
	
	private String localDir;
	
	private ImageIcon icon;
	
	private List<String> appParameters = new ArrayList<String>();
	
	private List<TaskDescription> tasks = new ArrayList<TaskDescription>();
	
	private boolean needsLogin;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public List<TaskDescription> getTasks() {
		return tasks;
	}

	public void addTask(TaskDescription task) {
		this.tasks.add(task);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public boolean isNeedsLogin() {
		return needsLogin;
	}

	public void setNeedsLogin(boolean needsLogin) {
		this.needsLogin = needsLogin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocalDir() {
		return localDir;
	}

	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	public List<String> getAppParameters() {
		return appParameters;
	}

	public void setAppParameters(List<String> appParameters) {
		this.appParameters = appParameters;
	}
}
