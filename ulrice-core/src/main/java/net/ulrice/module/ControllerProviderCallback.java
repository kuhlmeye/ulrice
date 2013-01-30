package net.ulrice.module;

import net.ulrice.Ulrice;
import net.ulrice.module.exception.ModuleInstantiationException;

/**
 * Callback for the instantiation of a controller.
 */
public abstract class ControllerProviderCallback<T extends IFController> {

	/**
	 * Called by the module manager after controller initialization is finished
	 * and the controller is completely registered at the application.
	 */
	public void onControllerReady(T controller) {		
	}

	/**
	 * Called by the module manager during the initialization phase of the
	 * controller. The controller is not yet fully registered at the
	 * application. In this method basic data initialization could be done.
	 */
	public void onControllerInitialization(T controller) {	
	}

	/**
	 * Called by the module manager in case of an error.
	 */
	public void onFailure(ModuleInstantiationException exc) {		
		Ulrice.getMessageHandler().handleException(exc);
	}
}
