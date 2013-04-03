package net.ulrice.ui.wizard;

/**
 * Enhances the step interface to fit the requirements of conditional workflows
 * 
 * @author DL10KUH
 */
public interface ConditionalStep extends Step {

	/**
	 * Return the available return codes of this step
	 */
    String[] getAvailableReturnCodes();
    
    /**
     * Return the return code of this step
     */
    String getReturnCode();
}
