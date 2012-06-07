package net.ulrice.ui.wizard;

import javax.swing.Action;
import javax.swing.JComponent;

public interface WizardView extends StepFlowEventListener {

    void initialize(Action prevAction, Action nextAction);
    
    JComponent getView();            
}
