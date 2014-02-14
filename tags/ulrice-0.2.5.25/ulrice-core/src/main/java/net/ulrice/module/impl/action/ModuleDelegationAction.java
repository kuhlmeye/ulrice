/**
 * 
 */
package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import net.ulrice.Ulrice;

/**
 * An action which delegates to the module.
 * 
 * @author christof
 */
public class ModuleDelegationAction extends UlriceAction {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -8075590936981607102L;

	/**
	 * Create a new module action
	 * 
	 * @param uniqueId The unique id of this action.
	 * @param name The name of this action.
	 * @param enabled If this action is enabled. This sets also the initial enabled state.
	 * @param type The type of the action.
	 * @param icon The icon of this action.
	 */
	public ModuleDelegationAction(String uniqueId, String name, boolean enabled, Icon icon) {
		super(uniqueId, name, enabled, ActionType.ModuleAction, icon);
	}

	/**
	 * @see net.ulrice.module.impl.action.UlriceAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Ulrice.getActionManager().performAction(this, e);
	}
}
