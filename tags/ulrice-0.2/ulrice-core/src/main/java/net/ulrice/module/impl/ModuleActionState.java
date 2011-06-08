package net.ulrice.module.impl;

import net.ulrice.module.IFController;
import net.ulrice.module.impl.action.Action;

/**
 * This class is a wrapper for a module action which holds the state of an action for a module.
 * 
 * @author christof
 */
public class ModuleActionState {

	/** If this action is enabled for a module. */
	private boolean enabled;
	
	/** The reference to a module action. */
	private Action action;
	
	/** The reference to the controller handling this action. */
	private IFController controller;
	
	/**
	 * Creates a new module action state.
	 * 
	 * @param enabled True, if this action is currently enabled, false otherwise.
	 * @param controller Reference to the controller
	 * @param action Reference to the action.
	 */
	public ModuleActionState(boolean enabled, IFController controller, Action action) {
		this.enabled = enabled;
		this.action = action;
		this.controller = controller;
	}
		
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return the controller
	 */
	public IFController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(IFController controller) {
		this.controller = controller;
	}
}
