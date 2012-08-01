/**
 * 
 */
package net.ulrice.frame.impl;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;

import net.ulrice.Ulrice;
import net.ulrice.module.event.IFModuleActionManagerEventListener;
import net.ulrice.module.impl.action.UlriceAction;

/**
 * @author christof
 */
public class Toolbar extends JToolBar implements IFModuleActionManagerEventListener {

    /** Default generated serial version uid. */
    private static final long serialVersionUID = 5037645080800551907L;

    /** Placeholder for adding the module actions. */
    public static final String MODULE_ACTIONS = "Toolbar.Placeholder.ModuleActions";

    /** Placeholder for adding a separator. */
    public static final String SEPARATOR = "Toolbar.Placeholder.Separator";

    /** The action order. */
    private String[] actionArrays = new String[0];

    private boolean hideUnusedModuleActions = true;

    /**
     * Create a new toolbar.
     */
    public Toolbar(String actionOrder) {
        this();
        setActionOrder(actionOrder);
    }

    /**
     * Create a new toolbar.
     */
    public Toolbar() {
        Ulrice.getActionManager().addModuleActionManagerEventListener(this);
    }

    public void setActionOrder(String actionOrder) {
        actionArrays = actionOrder.split(",");
    }

    /**
     * Rebuild all actions.
     */
    protected void rebuildActions() {
        removeAll();
        if (actionArrays != null) {
            boolean lastWasSeparator = true;
            for (String action : actionArrays) {
                if (action == null) {
                    continue;
                }

                action = action.trim();
                if (MODULE_ACTIONS.equals(action)) {
                    // Add all module actions to the toolbar.
                    List<UlriceAction> moduleActions = Ulrice.getActionManager().getModuleActions();
                    if (moduleActions != null) {
                        for (UlriceAction moduleAction : moduleActions) {
                            lastWasSeparator = false;
                            add(createButton(moduleAction));
                        }
                    }

                }
                else if (SEPARATOR.equals(action) && !lastWasSeparator) {
                    lastWasSeparator = true;
                    addSeparator();                    
                }
                else {
                    UlriceAction applicationAction = Ulrice.getActionManager().getApplicationAction(action);
                    if (applicationAction == null) {
                        continue;
                    }
                    if (!isHideUnusedModuleActions()) {
                        lastWasSeparator = false;
                        add(createButton(applicationAction));
                    }
                    else if (isHideUnusedModuleActions() && Ulrice.getActionManager().isActionUsedByModule(applicationAction.getUniqueId())) {
                        lastWasSeparator = false;
                        add(createButton(applicationAction));
                    }
                }

            }
        }
        doLayout();
        repaint();
    }

    /**
     * Create the button displayed in the toolbar.
     * 
     * @param moduleAction The module action.
     * @return The button.
     */
    protected JButton createButton(UlriceAction moduleAction) {
        JButton button = new JButton(moduleAction);
        button.setOpaque(false);
        button.setRolloverEnabled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        if (button.getIcon() != null) {
            button.setText(null);
        }
        return button;
    }

    /**
     * @see net.ulrice.module.event.IFModuleActionManagerEventListener#applicationActionsChanged()
     */
    @Override
    public void applicationActionsChanged() {
        rebuildActions();
    }

    public void setHideUnusedModuleActions(boolean hideUnusedModuleActions) {
        this.hideUnusedModuleActions = hideUnusedModuleActions;
    }

    public boolean isHideUnusedModuleActions() {
        return hideUnusedModuleActions;
    }
}
