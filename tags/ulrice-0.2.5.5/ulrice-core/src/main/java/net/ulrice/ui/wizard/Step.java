package net.ulrice.ui.wizard;

import javax.swing.JComponent;

public interface Step {

    String getId();
    
    String getTitle();
    
    JComponent getView();
    
    void onShow(StepFlow flow, WizardData data);
    
    void onHide(StepFlow flow, WizardData data);
}
