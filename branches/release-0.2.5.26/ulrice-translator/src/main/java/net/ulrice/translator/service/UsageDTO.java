package net.ulrice.translator.service;

import java.io.Serializable;

/**
 * This is the data transfer object for the usage of a text key in the application.
 * This defines the key of the property entry. 
 * 
 * @author christof
 */
public class UsageDTO implements Serializable {

	private static final long serialVersionUID = -8548895173239294633L;

	private String application;
	
	private String module;
	
	private String usage;
	
	private String attribute;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	
	
}
