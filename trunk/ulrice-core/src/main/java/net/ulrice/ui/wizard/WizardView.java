package net.ulrice.ui.wizard;

import javax.swing.Action;
import javax.swing.JComponent;

public interface WizardView {

    void initialize(Action prevAction, Action nextAction, Action cancelAction, Action finishAction);
    
    JComponent getView();

    void stepFlowChanged(StepFlow abstractStepFlow);

    void currentStepChanged(StepFlow abstractStepFlow);            
}
