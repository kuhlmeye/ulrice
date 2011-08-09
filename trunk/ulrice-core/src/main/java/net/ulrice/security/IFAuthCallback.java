package net.ulrice.security;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.impl.action.UlriceAction;

/**
 * Authorization callback.
 */
public interface IFAuthCallback {

	/**
	 * Grants permission to open a module.
	 * 
	 * @param module
	 *            The module of the controller
	 * @param ctrl
	 *            The controller that should be opened.
	 * 
	 * @return True, if permission is granted, false otherwise.
	 */
	boolean allowOpenModule(IFModule module, IFController ctrl);

	/**
	 * Grants permission to register an action
	 * 
	 * @param ctrl
	 *            The controller in which wants to register the action. This
	 *            param is null, if the action is a system action.
	 * @param action
	 *            The action that is registered.
	 * @return True, if permission is granted, false otherwise.
	 */
	boolean allowRegisterAction(IFController ctrl, UlriceAction action);

	/**
	 * Grants permission to enable an action
	 * 
	 * @param ctrl
	 *            The controller of the action. The controller is null if the
	 *            action is a application action
	 * @param action
	 *            The action itself.
	 * @return True, if permission is granted, false otherwise.
	 */
	boolean allowEnableAction(IFController ctrl, UlriceAction action);

	/**
	 * Grants permission to execute an action
	 * 
	 * @param ctrl
	 *            The controller in which the action is executed.
	 * @param action
	 *            The action itself.
	 * @return True, if permission is granted, false otherwise.
	 */
	boolean allowExecuteAction(IFController ctrl, UlriceAction action);

	/**
	 * Grants permission to register a module.
	 * 
	 * @param module
	 *            The module that should be registered.
	 * 
	 * @return True, if permission is granted, false otherwise.
	 */
	boolean allowRegisterModule(IFModule module);
}
