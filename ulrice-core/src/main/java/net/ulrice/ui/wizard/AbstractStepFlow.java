package net.ulrice.ui.wizard;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.EventListenerList;

public abstract class AbstractStepFlow implements StepFlow {

    private EventListenerList listenerList = new EventListenerList();        
    private Map<String, Step> steps = new HashMap<String, Step>();
    private Action prevAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            prev();
        }        
    };
    private Action nextAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            next();
        }        
    };
    

    @Override
    public void addStepFlowEventListener(StepFlowEventListener listener) {
        listenerList.add(StepFlowEventListener.class, listener);
    }
    
    @Override
    public void removeStepFlowEventListener(StepFlowEventListener listener) {
        listenerList.remove(StepFlowEventListener.class, listener);
    }    
    
    @Override
    public void addStep(Step step) {
        steps.put(step.getId(), step);
        stepAdded(step);
        
        fireStepFlowChanged();
    }

    protected abstract void stepAdded(Step step);

    @Override
    public void delStep(Step step) {
        steps.remove(step.getId());
        stepRemoved(step);
    }

    protected abstract void stepRemoved(Step step);

    @Override
    public Step getStepById(String id) {
        return steps.get(id);
    }
    
    private void fireStepFlowChanged() {
        StepFlowEventListener[] listeners = listenerList.getListeners(StepFlowEventListener.class);
        for(StepFlowEventListener listener : listeners) {
            listener.stepFlowChanged(this);
        }
    }
    
    protected void fireCurrentStepChanged() {
        StepFlowEventListener[] listeners = listenerList.getListeners(StepFlowEventListener.class);
        for(StepFlowEventListener listener : listeners) {
            listener.currentStepChanged(this);
        }
    }
    
    @Override
    public Action getPrevAction() {
        return prevAction;
    }
    
    @Override
    public Action getNextAction() {
        return nextAction;
    }
}
