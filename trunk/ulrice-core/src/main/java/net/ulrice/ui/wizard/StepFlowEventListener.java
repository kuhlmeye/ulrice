package net.ulrice.ui.wizard;

import java.util.EventListener;

public interface StepFlowEventListener extends EventListener {

    void stepFlowChanged(StepFlow stepFlow);
    
    void currentStepChanged(StepFlow stepFlow);        
}
