package net.ulrice.ui.wizard;


public abstract class AbstractStepFlow implements StepFlow {

    private WizardData data = new WizardData();
    private Wizard wizard;

    @Override
    public void initialize(Wizard wizard) {
        this.wizard = wizard;
    }
    
    protected void fireStepFlowChanged() {
        wizard.stepFlowChanged(this);
    }
    
    protected void fireCurrentStepChanged() {
        wizard.currentStepChanged(this);
    }
    
    @Override
    public WizardData getWizardData() {
        return data;
    }
}
