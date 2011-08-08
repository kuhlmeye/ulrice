package net.ulrice.module;

import javax.swing.ImageIcon;

import net.ulrice.module.exception.ModuleInstanciationException;

/**
 * Interface of a module description. 
 * 
 * @author ckuhlmeyer
 */
public interface IFModule extends IFModuleTitleRenderer, Comparable<IFModule> {

	/**
	 * Returns a parameter by key
	 * 
	 * @param key The key
	 * @return The parameter value as object
	 */
	Object getParameter(String key);
	
	/**
	 * Returns the unique id of the controller.
	 * 
	 * @return The unique id as a string.
	 */
	String getUniqueId();

	/**
	 * 
	 * @param preferredSize
	 * @return
	 */
	ImageIcon getIcon(ModuleIconSize preferredSize);
	
	/**
	 * Return the type of the module
	 * 
	 * @return The type of the module.
	 */
	ModuleType getModuleInstanceType();
	
	/**
	 * Creates the module.
	 * 
	 * @return The instance of the controller of the module
	 * @throws ModuleInstanciationException If the module could not be instanciated
	 */
	IFController instanciateModule() throws ModuleInstanciationException;
}
