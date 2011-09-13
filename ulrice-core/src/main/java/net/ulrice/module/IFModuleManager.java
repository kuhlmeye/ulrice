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
	 * Opens a module, i.e. initialize it and display its view. If the callback is non-null, it
	 *  is called with the controller
	 */
	void openModule (String moduleId, ControllerProviderCallback callback, Object... parameters) throws ModuleInstantiationException;

	/**
	 * opens a module as a "child", i.e. creates a new controller instance that is closed automatically when the parent controller
	 *  is closed. Passing <pre>null</pre> as a parent makes the newly created controller top-level.
	 */
	void openModule (String moduleId, IFController parent, ControllerProviderCallback callback, Object... parameters) throws ModuleInstantiationException;
	
	/**
	 * Activates an instance of a module, i.e. give it the focus.
	 */
	void activateModule(IFController controller);

    /**
     * Close all modules. The {@code Runnable} will only be executed if all modules where closed. If the closing is
     * canceled by a module, the {@code Runnable} is not executed.
     */
    void closeAllControllers(Runnable afterClosingAllModules);

    /**
     * Close all modules except the one given by the parameter
     * 
     * @param controller The module the should be left open.
     */
    void closeOtherControllers(IFController controller, IFCloseHandler closeHandler);

	/**
	 * Close the controller and all its child controllers. The child controllers will be closed first.
	 */
	void closeController(IFController controller, IFCloseHandler closeHandler);
	
	IFModule getModule (IFController controller);
	IFModuleTitleProvider getTitleProvider (IFController controller);
	
	/**
	 * Returns the controller of the current active module.
	 * 
	 * @return The controller.
	 */
	IFController getCurrentController();
	
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

	void block (IFController controller, Object blocker);
	void unblock (IFController controller, Object blocker);
	boolean isBlocked (IFController controller);

	List<IFModule> getAllModules();
	
//	void fireControllerBlocked(IFController abstractController);
//	void fireControllerUnblocked(IFController abstractController);
}