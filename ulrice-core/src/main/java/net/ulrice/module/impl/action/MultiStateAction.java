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
        int newActionState = (actionState + 1) % actions.size();
        
        updateAction(newActionState);
        actionState = newActionState;
        
        Ulrice.getActionManager().performAction(this, e);
    }
    
    protected void updateAction(int actionStateNumber) {
        putValue(SMALL_ICON, actions.get(actionStateNumber).getIcon());
        putValue(SHORT_DESCRIPTION, actions.get(actionStateNumber).getName());
    }

    public String getCurrentActionId() {
        return actions.get(actionState).getUniqueId();
    }
    
    public int getActionState() { 
        return actionState;
    }
    
    public void setActionState(int actionState) {
        this.actionState = actionState;
    }
    
    public void setCurrentActionId(String id) {
        for(int i = 0; i < actions.size(); i++) {
            if (actions.get(i).getUniqueId().equals(id)) {
                actionState = i;
                updateAction(actionState);
                break;
            }
        }
    }
}
