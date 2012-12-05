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

	private static final long serialVersionUID = -7876276315275574028L;
	
	/** The unique id of the close all action. */
	public static final String ACTION_ID = "CLOSE_ALL";

	public CloseAllModulesAction(String name, Icon icon) {
		super(ACTION_ID, name, true, ActionType.SystemAction, icon);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    
		Ulrice.getModuleManager().closeAllControllers(null);
	}
}
