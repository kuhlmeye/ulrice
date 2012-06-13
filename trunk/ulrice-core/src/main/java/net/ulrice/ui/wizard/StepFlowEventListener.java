package net.ulrice.ui.wizard;

import java.util.EventListener;

public interface StepFlowEventListener extends EventListener {

    void flowCancelled();
    
    void flowFinished();
    
    void stepChanged();        
}
