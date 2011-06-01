package net.ulrice.module;

import java.util.List;

import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.exception.ModuleInstanciationException;

/**
 * Interface of the module managers.
 * 
 * @author ckuhlmeyer
 */
public interface IFModuleManager {

	/**
	 * Registers a module as an instanceable module.
	 * 
	 * @param module The module
	 */
	void registerModule(IFModule module);

	/**
	 * Removes a module from the list of instanceable modules.
	 * 
	 * @param module The module
	 */
	void unregisterModule(IFModule module);

	/**
	 * Opens a module.
	 * 
	 * @param moduleId The id description of the module.
	 * @return The controller of the instanciated module.
	 * @throws ModuleInstanciationException If the module could not be instanciated
	 */
	IFController openModule(String moduleId) throws ModuleInstanciationException;

	/**
	 * Activates an instance of a module.
	 * 
	 * @param controller The controller of the module that should be activated.
	 */
	void activateModule(IFController controller);

	/**
	 * Close an instance of a module.
	 * 
	 * @param controller The controller of the module that should be closed.
	 */
	void closeModule(IFController controller);

	/**
	 * Returns the controller of the current active module.
	 * 
	 * @return The controller.
	 */
	IFController getCurrentModule();

	/**
	 * Returns the list of the controler of all active modules.
	 * 
	 * @return A controller list.
	 */
	List<IFController> getActiveModules();

	/**
	 * Adds a module event listener to the list of listeners.
	 * 
	 * @param listener The listener that should be added to the list.
	 */
	void addModuleEventListener(IFModuleEventListener listener);

	/**
	 * Removes a module event listener from the list of listeners.
	 * 
	 * @param listener The listener that should be removed from the list.
	 */
	void removeModuleEventListener(IFModuleEventListener listener);

	/**
	 * Close all modules.
	 */
	void closeAllModules();

	/**
	 * Close all modules except the one given by the parameter
	 * 
	 * @param controller The module the should be left open.
	 */
	void closeOtherModules(IFController controller);

	void fireControllerBlocked(IFController abstractController);

	void fireControllerUnblocked(IFController abstractController);
}
