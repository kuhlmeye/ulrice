package net.ulrice.ui.wizard;

import java.util.Iterator;

public interface StepFlow {
    
    Step getStepById(String id);
    
    Step getCurrentStep();
    
    String getCurrentStepId();
    
    Step first();    
    
    Step next();
    
    Step prev();
    
    boolean isFirst();
    
    boolean isLast();
    
    Iterator<Step> getStepIterator();

    WizardData getWizardData();

    void initialize(Wizard wizard);
}
