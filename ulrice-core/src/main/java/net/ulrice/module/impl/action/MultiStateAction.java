package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.ulrice.Ulrice;

/**
 * Multi state action that is handled by the ulrice action manager and delegated to the ulrice modules.
 * 
 * @author andre
 *
 */
public class MultiStateAction extends ModuleDelegationAction  {

    /**Generated id*/
    private static final long serialVersionUID = 1L;
    
    private List<UlriceAction> actions;
    
    private int actionState = 0;
    
    public MultiStateAction(String uniqueId, String name, boolean enabled, Icon icon, UlriceAction ... actions) {
        super(uniqueId, actions[0].getName(), enabled, icon);
        this.actions = Arrays.asList(actions);

        actionState = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionState = (actionState + 1) % actions.size();
        
        updateAction();
        
        Ulrice.getActionManager().performAction(this, e);
    }
    
    private void updateAction() {
        putValue(SMALL_ICON, actions.get(actionState).getIcon());
        putValue(SHORT_DESCRIPTION, actions.get(actionState).getName());
    }

    public String getCurrentActionId() {
        return actions.get(actionState).getUniqueId();
    }
    
    public void setCurrentActionId(String id) {
        for(int i = 0; i < actions.size(); i++) {
            if (actions.get(i).getUniqueId().equals(id)) {
                actionState = i;
                updateAction();
                break;
            }
        }
    }
}
