package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.ulrice.module.impl.ModuleActionState;

/**
 * Represents the state of an action
 * 
 * @author Manfred HANTSCHEL
 */
public class ActionState implements Serializable {

    private static final long serialVersionUID = 8725021099593075698L;

    /**
     * Creates an action state from the specified action
     * 
     * @param action the action
     * @return the action state, null if the action is null
     */
    public static ActionState inspect(ModuleActionState action) {
        if (action == null) {
            return null;
        }

        return new ActionState(action);
    }

    /**
     * Creates a collection of action states from the specified actions. Null elements in the collection are ignored
     * 
     * @param actions the actions
     * @return the action states
     */
    public static Collection<ActionState> inspect(Collection<ModuleActionState> actions) {
        Collection<ActionState> results = new ArrayList<ActionState>();

        for (ModuleActionState action : actions) {
            ActionState state = inspect(action);

            if (state != null) {
                results.add(state);
            }
        }

        return results;
    }

    private final transient ModuleActionState action;

    private final String uniqueId;
    private final boolean enabled;

    protected ActionState(ModuleActionState action) {
        super();

        this.action = action;

        uniqueId = action.getAction().getUniqueId();
        enabled = action.isEnabled();
    }

    /**
     * The source action. The property is transient and may be null.
     * 
     * @return the action
     */
    public ModuleActionState getAction() {
        return action;
    }

    /**
     * The unique id of the action.
     * 
     * @return the unique id
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * True if the action is enabled.
     * 
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ActionState [uniqueId=" + uniqueId + ", enabled=" + enabled + "]";
    }

}
