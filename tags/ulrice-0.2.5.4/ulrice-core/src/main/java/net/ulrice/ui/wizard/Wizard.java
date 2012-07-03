package net.ulrice.ui.wizard;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

public class Wizard {

    private EventListenerList listenerList = new EventListenerList();
    private boolean enableFinishOnlyOnLastStep = true;
    private WizardView view;
    private StepFlow stepFlow;
    private Action prevAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            getStepFlow().prev();
        }        
    };
    private Action nextAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            getStepFlow().next();            
        }        
    };
    private Action cancelAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            fireFlowCancelled();
        }
    };
    private Action finishAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            fireFlowFinished();
        }
    };
    
    public Wizard() {
        this(new DefaultWizardView(), new LinearStepFlow());
    }
    
    public Wizard(WizardView view, StepFlow stepFlow) {
        super();
        this.view = view;
        this.stepFlow = stepFlow;
        
        stepFlow.initialize(this);
        handleActionStates(stepFlow);
        view.initialize(prevAction, nextAction, cancelAction, finishAction);
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
    
    public WizardData getWizardData() {
        return getStepFlow().getWizardData();
    }
    
    public void addStepFlowEventListener(StepFlowEventListener listener) {
        listenerList.add(StepFlowEventListener.class, listener);
    }
    
    public void removeStepFlowEventListener(StepFlowEventListener listener) {
        listenerList.remove(StepFlowEventListener.class, listener);
    }
    
    private void fireStepChanged() {
        StepFlowEventListener[] listeners = listenerList.getListeners(StepFlowEventListener.class);
        if(listeners != null) {
            for(StepFlowEventListener listener : listeners) {
                listener.stepChanged();
            }
        }
    }
    
    private void fireFlowCancelled() {
        StepFlowEventListener[] listeners = listenerList.getListeners(StepFlowEventListener.class);
        if(listeners != null) {
            for(StepFlowEventListener listener : listeners) {
                listener.flowCancelled();
            }
        }
    }
    
    private void fireFlowFinished() {
        StepFlowEventListener[] listeners = listenerList.getListeners(StepFlowEventListener.class);
        if(listeners != null) {
            for(StepFlowEventListener listener : listeners) {
                listener.flowFinished();
            }
        }
    }

    public void stepFlowChanged(StepFlow stepFlow) {
        handleActionStates(stepFlow);
        getView().stepFlowChanged(stepFlow);
    }

    public void currentStepChanged(StepFlow stepFlow) {
        handleActionStates(stepFlow);
        getView().currentStepChanged(stepFlow);
        fireStepChanged();
    }

    private void handleActionStates(StepFlow stepFlow) {
        prevAction.setEnabled(!stepFlow.isFirst());
        nextAction.setEnabled(!stepFlow.isLast());
        if(isEnableFinishOnlyOnLastStep()) {
            finishAction.setEnabled(stepFlow.isLast());
        }
    }
    
    public Action getCancelAction() {
        return cancelAction;
    }
    
    public Action getFinishAction() {
        return finishAction;
    }
    
    public boolean isEnableFinishOnlyOnLastStep() {
        return enableFinishOnlyOnLastStep;
    }
    
    public void setEnableFinishOnlyOnLastStep(boolean enableFinishOnlyOnLastStep) {
        this.enableFinishOnlyOnLastStep = enableFinishOnlyOnLastStep;
    }
}
