package net.ulrice.ui.wizard;

import javax.swing.JComponent;

/**
 * Interface for a wizard step.
 * 
 * @author DL10KUH
 */
public interface Step {

	/**
	 * Returns the id of the step
	 */
    String getId();

    /**
     * Returns the title of the step
     */
    String getTitle();
    
    /**
     * Returns the display component of this step.
     */
    JComponent getView();
    
    /**
     * Called before the step is displayed by the wizard
     */
    void onShow(StepFlow flow, WizardData data);
    
    /**
     * Called before the step is hidden by the wizard
     */
    void onHide(StepFlow flow, WizardData data);
    
    /**
     * Return true, if the cancel action should be enabled.
     */
    boolean isCancelEnabled();
    
    /**
     * Return true, if the next action should be enabled.
     */
    boolean isNextEnabled();
    
    /**
     * Return true, if the prev action should be enabled.
     */
    boolean isPrevEnabled();
    
    /**
     * Return true, if the finish action should be enabled.
     */
    boolean isFinishEnabled();
}
