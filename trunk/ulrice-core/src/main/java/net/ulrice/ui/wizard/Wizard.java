package net.ulrice.ui.wizard;

import javax.swing.JComponent;

public class Wizard {

    private WizardView view;
    private StepFlow stepFlow;
    
    public Wizard() {
        this(new DefaultWizardView(), new DefaultStepFlow());
    }
    
    public Wizard(WizardView view, StepFlow stepFlow) {
        super();
        this.view = view;
        this.stepFlow = stepFlow;
        
        stepFlow.addStepFlowEventListener(view);        
        view.initialize(stepFlow.getPrevAction(), stepFlow.getNextAction());
    }
    
    public WizardView getView() {
        return view;
    }
    
    public JComponent getViewComponent() {
        return getView().getView();
    }
    
    public StepFlow getStepFlow() {
        return stepFlow;
    }
    
    public void addStep(Step step) {
        getStepFlow().addStep(step);
    }
    
    public void delStep(Step step) {
        getStepFlow().delStep(step);
    }
    
    public Step getStepById(String id) {
        return getStepFlow().getStepById(id);
    }
}
