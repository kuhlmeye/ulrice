package net.ulrice.module;

import javax.swing.ImageIcon;
import java.util.Map;


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
	 * @param parent parent controller or null if opened from menu
	 */
	void instantiateModule (ControllerProviderCallback<T> callback, IFController parent, Map<String, ModuleParam> params);
}
