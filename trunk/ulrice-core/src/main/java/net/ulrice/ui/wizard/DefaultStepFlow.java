package net.ulrice.ui.wizard;

import java.util.ArrayList;
import java.util.List;

public class DefaultStepFlow extends AbstractStepFlow {

    private List<String> stepOrder = new ArrayList<String>();
    private int currentIdx = 0; 
    
    @Override
    protected void stepAdded(Step step) {
        stepOrder.add(step.getId());
    }
    
    @Override
    protected void stepRemoved(Step step) {
        stepOrder.remove(step.getId());
    }
    
    @Override
    public Step next() {
        if (currentIdx != -1 && currentIdx < stepOrder.size() - 1) {
            currentIdx++;
            fireCurrentStepChanged();
            return getCurrentStep();
        }
        return null;
    }
    
    @Override
    public Step prev() {        
        if (currentIdx != -1 && currentIdx > 0) {
            currentIdx--;
            fireCurrentStepChanged();
            return getCurrentStep();
        }
        return null;
    }
    
    @Override
    public Step first() {
        boolean notify = currentIdx != 0;
        currentIdx = 0;
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
}
