package net.ulrice.translator.service;

import java.io.Serializable;
import java.util.Locale;

/**
 * This data transfer object represents an entry in the property file.
 * 
 * @author christof
 */
public class TranslationDTO implements Serializable {

	private static final long serialVersionUID = -5987553148334670992L;

	private String application;
	
	private String module;
	
	private String usage;
	
	private String attribute;
	
	private Locale language;
	
	private String translation;

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

	public Locale getLanguage() {
		return language;
	}

	public void setLanguage(Locale language) {
		this.language = language;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}
}
