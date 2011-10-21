package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.ulrice.module.impl.ModuleActionState;

public class ActionState implements Serializable {

    private static final long serialVersionUID = 8725021099593075698L;

    public static ActionState inspect(ModuleActionState action) {
        if (action == null) {
            return null;
        }

        return new ActionState(action);
    }

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

    public ModuleActionState getAction() {
        return action;
    }

    public String getUniqueId() {
        return uniqueId;
    }

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
