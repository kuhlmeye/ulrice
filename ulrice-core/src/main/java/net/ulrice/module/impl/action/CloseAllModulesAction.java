/**
 * 
 */
package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import net.ulrice.Ulrice;

/**
 * Default action closing all modules.
 * 
 * @author christof
 */
public class CloseAllModulesAction extends UlriceAction {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -7876276315275574028L;
	
	/** The unique id of the close all action. */
	public static final String ACTION_ID = "CLOSE_ALL";

	/**
	 * Create the close all action.
	 * 
	 * @param name The translated name of the close-all action.
	 * @param icon The icon of the close-all action
	 */
	public CloseAllModulesAction(String name, Icon icon) {
		super(ACTION_ID, name, true, ActionType.SystemAction, icon);
	}
	
	/**
	 * @see net.ulrice.module.impl.action.UlriceAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Ulrice.getModuleManager().closeAllControllers();
	}
}