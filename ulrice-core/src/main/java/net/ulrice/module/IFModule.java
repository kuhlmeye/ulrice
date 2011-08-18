package net.ulrice.module;

import javax.swing.ImageIcon;

import net.ulrice.module.exception.ModuleInstantiationException;


/**
 * Interface of a module description. 
 * 
 * @author ckuhlmeyer
 */
public interface IFModule extends IFModuleTitleProvider { 

	String getUniqueId();

	ImageIcon getIcon(ModuleIconSize preferredSize);
	
	ModuleType getModuleInstanceType();
	
	/**
	 * Creates a new instance of the module.
	 */
	void instantiateModule (ControllerProviderCallback callback);
}
