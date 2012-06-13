package net.ulrice.ui.wizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DefaultWizardView implements WizardView {

    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel stepViewPanel = new JPanel();
    private JPanel contentPanel = new JPanel(new BorderLayout());
    private boolean rebuildInProgress;
    
    @Override
    public void initialize(Action prevAction, Action nextAction, Action cancelAction, Action finishAction) {

        stepViewPanel.setLayout(new BoxLayout(stepViewPanel, BoxLayout.Y_AXIS));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        buttonPanel.add(new JButton(cancelAction));
        buttonPanel.add(new JButton(prevAction));
        buttonPanel.add(new JButton(nextAction));
        buttonPanel.add(new JButton(finishAction));
        
        panel.add(stepViewPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.SOUTH);  
        panel.add(contentPanel, BorderLayout.CENTER);
    }
        
    @Override
    public JComponent getView() {
        return panel;
    }
    
    @Override
    public void currentStepChanged(StepFlow stepFlow) {
        contentPanel.removeAll();
        contentPanel.add(stepFlow.getCurrentStep().getView(), BorderLayout.CENTER);
        rebuildStepPanel(stepFlow);
    }
    
    @Override
    public void stepFlowChanged(StepFlow stepFlow) {
        rebuildStepPanel(stepFlow);
    }

    private void rebuildStepPanel(StepFlow stepFlow) {
        if(!rebuildInProgress) {
            rebuildInProgress = true;
            try {
                stepViewPanel.removeAll();
                
                Iterator<Step> stepIterator = stepFlow.getStepIterator();
                while(stepIterator.hasNext()) {
                    addStepLabel(stepViewPanel, stepFlow, stepIterator.next());
                }
            } finally {
                rebuildInProgress = false;
            }        
        }
    }

    private boolean addStepLabel(JPanel panel, StepFlow stepFlow, Step step) {
        if(step != null) {
            JLabel label = new JLabel(step.getTitle());
            if(step.getId().equals(stepFlow.getCurrentStepId())) {
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } else {
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
            panel.add(label);
            return true;
        }
        return false;
    }
}
