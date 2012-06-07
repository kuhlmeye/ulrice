package net.ulrice.ui.wizard;

import javax.swing.Action;

public interface StepFlow {
    
    void addStepFlowEventListener(StepFlowEventListener listener);
    
    void removeStepFlowEventListener(StepFlowEventListener listener);
    
    void addStep(Step step);
    
    void delStep(Step step);
    
    Step getStepById(String id);
    
    Step getCurrentStep();
    
    String getCurrentStepId();
    
    Step first();    
    
    Step next();
    
    Step prev();
    
    Action getNextAction();
    
    Action getPrevAction();
}
