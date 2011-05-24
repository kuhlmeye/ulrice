package net.ulrice.webstarter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * Class describing the startup process of an application.
 * 
 * @author christof
 */
public class ApplicationDescription {

	/** Internal id used for identifing in the settings. */
	private String id;
	
	/** Name displayed in the frontend. */
	private String name;
	
	/** Directory where the application is stored. */
	private String localDir;
	
	/** Icon of the application displayed in the frontend. */
	private ImageIcon icon;
	
	/** Flag, if the proxy should be used. */
	private boolean useProxy = false;
	
	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	/** List of application parameters. */
	private List<String> appParameters = new ArrayList<String>();
	
	/** List of the tasks description needed to run for application startup. */
	private List<TaskDescription> tasks = new ArrayList<TaskDescription>();

	private List<TaskDescription> taskListBackup = new ArrayList<TaskDescription>();
	
	/** Boolean, if login is needed for this application and the fields in the application must be unblocked. */
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
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void backupTasks() {
		taskListBackup = new ArrayList<TaskDescription>(tasks);
	}
	
	public void restoreTasks() {
		tasks = new ArrayList<TaskDescription>(taskListBackup);
	}
}
