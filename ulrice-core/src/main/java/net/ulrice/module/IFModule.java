package net.ulrice.module;

import javax.swing.ImageIcon;


/**
 * Interface of a module description. 
 * 
 * @author ckuhlmeyer
 */
public interface IFModule<T extends IFController> extends IFModuleTitleProvider { 

	String getUniqueId();

	ImageIcon getIcon(ModuleIconSize preferredSize);
	
	ModuleType getModuleInstanceType();
	
	/**
	 * Creates a new instance of the module.
	 */
	void instantiateModule (ControllerProviderCallback<T> callback);
}
