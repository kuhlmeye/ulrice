package net.ulrice.module.impl;

import java.util.IdentityHashMap;

import net.ulrice.module.impl.action.UlriceAction;

/**
 * This class is a wrapper for a module action which holds the state of an action for a module.
 * 
 * @author christof
 */
public class ModuleActionState {

	/** If this action is enabled for a module. */
	private boolean enabled;
	
	/** The reference to a module action. */
	private UlriceAction action;
	
	private IdentityHashMap<Object,Object> blockers = new IdentityHashMap<Object,Object>();
	
	/**
	 * Creates a new module action state.
	 * 
	 * @param enabled True, if this action is currently enabled, false otherwise.
	 * @param controller Reference to the controller
	 * @param action Reference to the action.
	 */
	public ModuleActionState(boolean enabled, UlriceAction action) {
		this.enabled = enabled;
		this.action = action;
	}
		
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled && blockers.isEmpty();
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
	public UlriceAction getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(UlriceAction action) {
		this.action = action;
	}
	
	public void addBlocker(Object blocker) {
	    blockers.put(blocker,blocker);
	}
	
	public void removeBlocker(Object blocker) {
	    blockers.remove(blocker);	    
	}
	
		
}
