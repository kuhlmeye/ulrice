package net.ulrice.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

public class ConditionalStepFlow extends AbstractStepFlow {

    private Map<String, ConditionalStep> idStepMap = new HashMap<String, ConditionalStep>();
    private Map<String, Map<String, String>> conditionMap = new HashMap<String, Map<String,String>>();
    private Stack<String> idStack = new Stack<String>();
    private String currentStepId;
    private String firstStepId;
    
    @Override
    public Step getStepById(String id) {
        return (id != null) ? idStepMap.get(id) : null;
    }

    @Override
    public Step getCurrentStep() {
        return getStepById(currentStepId);
    }

    @Override
    public String getCurrentStepId() {
        return currentStepId;
    }
    
    public void setFirstStep(ConditionalStep firstStep) {
        this.firstStepId = firstStep.getId();
        if(currentStepId == null) {
            currentStepId = firstStepId;
        }
        
        addStep(null, null, firstStep);
    }

    public void addStep(String fromStepId, String returnCode, ConditionalStep toStep) {
        idStepMap.put(toStep.getId(), toStep);
        
        Map<String, String> returnCodeMap = conditionMap.get(fromStepId);
        if(returnCodeMap == null) {
            returnCodeMap = new HashMap<String, String>();
            conditionMap.put(fromStepId, returnCodeMap);
        }
        
        returnCodeMap.put(returnCode, toStep.getId());
        
        fireStepFlowChanged();
    }
    
    public void clearSteps() {
        idStepMap.clear();
        conditionMap.clear();
        firstStepId = null;
        currentStepId = null;
    }

    @Override
    public Step first() {
        if(getCurrentStep() != null) {
            getCurrentStep().onHide(this, getWizardData());
        }
        currentStepId = firstStepId;
        if(getCurrentStep() != null) {
            getCurrentStep().onShow(this, getWizardData());
        }
        Step currentStep = getCurrentStep();
        fireCurrentStepChanged();
        return currentStep;
    }

    @Override
    public Step next() {
        idStack.push(currentStepId);
        
        ConditionalStep step = idStepMap.get(getCurrentStepId());
        String nextId = getStepIdByRC(step.getId(), step.getReturnCode());

        if(getCurrentStep() != null) {
            getCurrentStep().onHide(this, getWizardData());
        }
        currentStepId = nextId;
        if(getCurrentStep() != null) {
            getCurrentStep().onShow(this, getWizardData());
        }

        Step currentStep = getCurrentStep();
        fireCurrentStepChanged();
        return currentStep;
    }

    private String getStepIdByRC(String stepId, String returnCode) {
        Map<String, String> returnCodeMap = conditionMap.get(stepId);
        if(returnCodeMap == null) {
            return null;
        }
        return returnCodeMap.get(returnCode);
    }

    @Override
    public Step prev() {
        if(getCurrentStep() != null) {
            getCurrentStep().onHide(this, getWizardData());
        }
        currentStepId = idStack.pop();        
        if(getCurrentStep() != null) {
            getCurrentStep().onShow(this, getWizardData());
        }
        Step currentStep = getCurrentStep();
        fireCurrentStepChanged();
        return currentStep;
    }
    
    public boolean isFirst() {
        return firstStepId == null || firstStepId.equals(getCurrentStepId());
    }
    
    public boolean isLast() {
        Map<String, String> map = conditionMap.get(currentStepId);        
        return map == null || map.isEmpty();
    }        

    @Override
    public Iterator<Step> getStepIterator() {
        ListIterator<String> listIterator = idStack.listIterator();
        List<Step> result = new ArrayList<Step>();
        while(listIterator.hasNext()) {
            result.add(getStepById(listIterator.next()));
        }
        
        if(getCurrentStepId() != null) {
            ConditionalStep cStep = idStepMap.get(getCurrentStepId());
            result.add(cStep);
            
            while(cStep.getAvailableReturnCodes().length == 1) {
                String nextId = getStepIdByRC(cStep.getId(), cStep.getAvailableReturnCodes()[0]);
                if(nextId == null) {
                    break;
                }
                cStep = idStepMap.get(nextId);
                result.add(cStep);
            }
        }
        
        return result.iterator();
    }
}
