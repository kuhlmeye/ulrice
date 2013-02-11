package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;

public class CloseOtherModulesAction extends UlriceAction {


    private static final long serialVersionUID = -7876276315275574028L;
    
    public static final String ACTION_ID = "CLOSE_OTHER";

    public CloseOtherModulesAction(String name, Icon icon) {
        super(ACTION_ID, name, true, ActionType.SystemAction, icon);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        final IFController currentModule = Ulrice.getModuleManager().getCurrentController();
        
        if(currentModule != null) {
            Ulrice.getModuleManager().closeOtherControllers(currentModule, null);
        }
    }
}
