package net.ulrice.module.impl.action;

public class UActionState {
    
    private final String actionId;
    private final boolean enabled;
    
    public UActionState(final String actionId, final boolean enabled) {
        this.actionId = actionId;
        this.enabled = enabled;
    }
    
    public String getActionId() {
        return actionId;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
