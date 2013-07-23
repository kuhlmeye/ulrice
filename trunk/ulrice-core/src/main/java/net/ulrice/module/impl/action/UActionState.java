package net.ulrice.module.impl.action;

public class UActionState {
    
    private final String actionId;
    private final boolean enabled;
    private final int actionState;
    
    public UActionState(final String actionId, final boolean enabled) {
        this(actionId, enabled, 0);
    }
    
    public UActionState(final String actionId, final boolean enabled, int actionState) {
        this.actionId = actionId;
        this.enabled = enabled;
        this.actionState = actionState;
    }
    
    public String getActionId() {
        return actionId;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public int getActionState() {
        return actionState;
    }
}
