package net.ulrice.module.impl;

import net.ulrice.module.IFModuleTitleProvider;

/**
 * A simple non translating module title renderer.
 * 
 * @author ckuhlmeyer
 */
public class SimpleModuleTitleRenderer implements IFModuleTitleProvider {

	/** The title of this module. */
	private String title;

	/**
	 * Creates a new simple module title renderer returning the given string as the title.
	 * 
	 * @param title The title.
	 */
	public SimpleModuleTitleRenderer(String title) {
		this.title = title;
	}
	
	/**
	 * @see net.ulrice.module.IFModuleTitleProvider#getModuleTitle(net.ulrice.module.IFModuleTitleProvider.Usage)
	 */
	public String getModuleTitle(Usage usage) {
		return title;
	}
}
