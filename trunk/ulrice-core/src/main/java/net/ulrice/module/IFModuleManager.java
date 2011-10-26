package net.ulrice.module;

import java.util.List;

import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.exception.ModuleInstantiationException;
import net.ulrice.module.impl.IFCloseHandler;

/**
 * Interface of the module managers.
 * 
 * @author ckuhlmeyer
 */
public interface IFModuleManager {

	/**
	 * Registers a module as an instanceable module.
	 */
	void registerModule(IFModule module);

	/**
	 * Removes a module from the list of instanceable modules.
	 */
	void unregisterModule(IFModule module);

	/**
	 * Opens a module, i.e. initialize it and display its view. If the callback
	 * is non-null, it is called with the controller
	 */
	void openModule(String moduleId, ControllerProviderCallback callback) throws ModuleInstantiationException;

	/**
	 * opens a module as a "child", i.e. creates a new controller instance that
	 * is closed automatically when the parent controller is closed. Passing <pre>null</pre>
	 * as a parent makes the newly created controller top-level.
	 */
	void openModule(String moduleId, IFController parent, ControllerProviderCallback callback) throws ModuleInstantiationException;

	/**
	 * Activates an instance of a module, i.e. give it the focus.
	 */
	void activateModule(IFController controller);

	/**
	 * Close all modules. The {@code Runnable} will only be executed if all
	 * modules where closed. If the closing is canceled by a module, the
	 * {@code Runnable} is not executed.
	 */
	void closeAllControllers(Runnable afterClosingAllModules);

	/**
	 * Close all modules except the one given by the parameter
	 * 
	 * @param controller The module the should be left open.
	 */
	void closeOtherControllers(IFController controller, IFCloseHandler closeHandler);

	/**
	 * Close the controller and all its child controllers. The child controllers
	 * will be closed first.
	 */
	void closeController(IFController controller, IFCloseHandler closeHandler);

	/**
	 * Returns the module of a controller
	 * 
	 * @param controller The controller.
	 * @return The module of the controller
	 */
	IFModule getModule(IFController controller);

	/**
	 * Returns the title provider of a controller
	 * 
	 * @param controller The controller
	 * @return The title provider able to render the title of the controller.
	 */
	IFModuleTitleProvider getTitleProvider(IFController controller);
	
	/**
	 * Returns the title of a module
	 * 
	 * @param controller The controller of the module
	 * @param usage The usage of the title
	 * @return
	 */
	String getModuleTitle(IFController controller, IFModuleTitleProvider.Usage usage);

	/**
	 * Returns the controller of the current active module.
	 * 
	 * @return The controller.
	 */
	IFController getCurrentController();

	/**
	 * Returns the parent controller of a given controller or null, if this controller has no parent.
	 * 
	 * @param controller The controller
	 * @return The parent controller or null.
	 */
	IFController getParentController(IFController controller);

	/**
	 * Returns the list of the controller of all active modules.
	 * 
	 * @return A controller list.
	 */
	List<IFController> getActiveControllers();

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
	 * Add a blocker to a module.
	 * 
	 * @param controller The controller of the module which is blocked by the object
	 * @param blocker The blocking object. This object is needed to unblock the module again
	 */
	void addBlocker(IFController controller, Object blocker);

	/**
	 * Remove a blocker from a module
	 * 
	 * @param controller The controller of the module which is blocked by the object.
	 * @param blocker The blocking object.
	 */
	void removeBlocker(IFController controller, Object blocker);

	/**
	 * Returns, if the module is blocked. If a module is blocked, the user
	 * cannot click on gui elements in the module. Also the actions of the
	 * controller are disabled.
	 * 
	 * @param controller The controller of the module. 
	 * @return true, if the module is blocked, false otherwise.
	 */
	boolean isBlocked(IFController controller);

	/**
	 * Returns a list of all known modules.
	 * 
	 * @return A list of modules.
	 */
	List<IFModule> getAllModules();
}
