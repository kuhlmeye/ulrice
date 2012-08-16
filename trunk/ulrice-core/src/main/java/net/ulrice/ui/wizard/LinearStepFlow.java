package net.ulrice.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Normal linear step flow.
 * 
 * @author DL10KUH
 */
public class LinearStepFlow extends AbstractStepFlow {
    
    private Map<String, Step> steps = new HashMap<String, Step>();
    private List<String> stepOrder = new ArrayList<String>();
    private int currentIdx = 0; 
    
    public void addStep(Step step) {
        steps.put(step.getId(), step);
        stepOrder.add(step.getId());
        fireStepFlowChanged();
    }

    public void delStep(Step step) {
        steps.remove(step.getId());
        stepOrder.remove(step.getId());
    }

    @Override
    public Step getStepById(String id) {
        return steps.get(id);
    }
    
    @Override
    public Step next() {
        if (currentIdx != -1 && currentIdx < stepOrder.size() - 1) {
            if(getCurrentStep() != null) {
                getCurrentStep().onHide(this, getWizardData());
            }
            currentIdx++;
            if(getCurrentStep() != null) {
                getCurrentStep().onShow(this, getWizardData());
            }
            fireCurrentStepChanged();
            return getCurrentStep();
        }
        return null;
    }
    
    @Override
    public Step prev() {        
        if (currentIdx != -1 && currentIdx > 0) {
            if(getCurrentStep() != null) {
                getCurrentStep().onHide(this, getWizardData());
            }
            currentIdx--;
            if(getCurrentStep() != null) {
                getCurrentStep().onShow(this, getWizardData());
            }
            fireCurrentStepChanged();
            return getCurrentStep();
        }
        return null;
    }
    
    @Override
    public Step first() {
        boolean notify = currentIdx != 0;
        if(getCurrentStep() != null) {
            getCurrentStep().onHide(this, getWizardData());
        }
        currentIdx = 0;
        if(getCurrentStep() != null) {
            getCurrentStep().onShow(this, getWizardData());
        }
        if(notify) {
            fireCurrentStepChanged();
        }
        return stepOrder.size() > 0 ? getCurrentStep() : null;
    }

    @Override
    public Step getCurrentStep() {
        return getStepById(getCurrentStepId());
    }

    @Override
    public String getCurrentStepId() {
        return stepOrder.get(currentIdx);
    }
    
    @Override
    public boolean isFirst() {
        return currentIdx == 0;
    }
    
    @Override
    public boolean isLast() {
        return stepOrder.size() - 1 == currentIdx;
    }
    
    
    @Override
    public Iterator<Step> getStepIterator() {       
        return new Iterator<Step>() {

            private int currentStepIdx = 0;
            
            @Override
            public boolean hasNext() {
                return currentStepIdx < stepOrder.size();
            }

            @Override
            public Step next() {
                return getStepById(stepOrder.get(currentStepIdx++));
            }

            @Override
            public void remove() {
                throw new IllegalAccessError("Method is not supported.");
            }
            
        };
    }
}