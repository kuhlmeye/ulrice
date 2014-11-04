package net.ulrice.module.impl.action;

import javax.swing.Icon;

import net.ulrice.security.Authorization;

/**
 * An extended action handling default 	
 * 
 * @author christof
 */
public class AuthModuleDelegationAction extends ModuleDelegationAction {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -3194687027367977235L;
	
	/**
	 * The authorization object
	 */
	private Authorization authorization;

	/**
	 * Create a new module action
	 * 
	 * @param uniqueId The unique id of this action.
	 * @param name The name of this action.
	 * @param enabled If this action is enabled. This sets also the initial enabled state.
	 * @param type The type of the action.
	 * @param icon The icon of this action.
	 */
	public AuthModuleDelegationAction(String uniqueId, String name, boolean enabled, Icon icon) {
		super(uniqueId, name, enabled, icon);
	}
	
	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}
}
