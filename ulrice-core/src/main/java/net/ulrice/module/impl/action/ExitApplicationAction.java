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
public class ExitApplicationAction extends UlriceAction {

	private static final long serialVersionUID = -7876276315275574028L;
	
	public static final String ACTION_ID = "EXIT_APPLICATION";

	public ExitApplicationAction(String name, Icon icon) {
		super(ACTION_ID, name, true, ActionType.SystemAction, icon);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    
		Ulrice.getModuleManager().closeAllControllers(new Runnable() {
            
            @Override
            public void run() {
                System.exit(0);
            }
        });
	}
}
