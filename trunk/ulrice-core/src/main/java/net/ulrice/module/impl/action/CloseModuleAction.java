/**
 * 
 */
package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;

/**
 * Default action closing the current module.
 * 
 * @author christof
 */
public class CloseModuleAction extends UlriceAction {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -7876276315275574028L;
	
	public static final String ACTION_ID = "CLOSE";

    private IFController controller;

    public CloseModuleAction(String name, Icon icon) {
        super(ACTION_ID, name, true, ActionType.SystemAction, icon);
    }

    public CloseModuleAction(String name, Icon icon, IFController controller) {
        super(ACTION_ID, name, true, ActionType.SystemAction, icon);
        this.controller = controller;
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    IFController controllerToClose = null;
	    if(controller == null) {
	        controllerToClose = Ulrice.getModuleManager().getCurrentController();
	    } else {
	        controllerToClose = controller;
	    }
		
		if(controllerToClose != null) {
		    Ulrice.getModuleManager().closeController(controllerToClose, null);
		}
	}
}
