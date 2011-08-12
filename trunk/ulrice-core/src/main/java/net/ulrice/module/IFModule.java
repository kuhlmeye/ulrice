package net.ulrice.module;

import javax.swing.ImageIcon;

import net.ulrice.module.exception.ModuleInstanciationException;


/**
 * Interface of a module description. 
 * 
 * @author ckuhlmeyer
 */
public interface IFModule extends IFModuleTitleProvider, Comparable<IFModule> { //TODO ehaasec remove the comparable requirement and use external ordering on registration 

	String getUniqueId();

	ImageIcon getIcon(ModuleIconSize preferredSize);
	
	ModuleType getModuleInstanceType();
	
	/**
	 * Creates a new instance of the module.
	 */
	IFController instantiateModule() throws ModuleInstanciationException;
}
