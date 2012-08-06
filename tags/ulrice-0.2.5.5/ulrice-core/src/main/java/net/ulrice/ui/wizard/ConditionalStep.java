package net.ulrice.ui.wizard;

public interface ConditionalStep extends Step {

    String[] getAvailableReturnCodes();
    
    String getReturnCode();
}
