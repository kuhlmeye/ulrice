package net.ulrice.module;

import java.util.List;

import net.ulrice.module.impl.ModuleActionState;

/**
 * Interface of the module controller.
 * 
 * @author ckuhlmeyer
 */
public interface IFController {

	/**
	 * Returns the view of the controller.
	 * 
	 * @return The view.
	 */
	IFView getView();
	
	/**
	 * Returns the model of the controller.
	 * 
	 * @return The model.
	 */
	IFModel getModel();
	
	/**
	 * Returns the module of this controller.
	 * 
	 * @return The module.
	 */
	IFModule getModule();
	
	/**
	 * Returns the renderer for rendering the title of this module. 
	 * 
	 * @return The module title renderer.
	 */
	IFModuleTitleRenderer getModuleTitleRenderer();

	/**
	 * Method called by the module manager after instanciation and before creation event notification
	 * 
	 * @param module The module of this module.
	 */
	void preCreationEvent(IFModule module);

	/**
	 * Method called by the module manager after instanciation and after creation event notification
	 * 
	 * @param module The module of this module.
	 */
	void postCreationEvent(IFModule module);
	
	/**
	 * Called by the module action manager, if a module action was executed.
	 * 
	 * @param action The identifier of the action that should be executed.
	 * @return true, if the action was processed by this module. False otherwise.
	 */
	boolean performModuleAction(String actionId);
	
	/**
	 * Returns the array of module actions states that are handled by the module. 
	 * 
	 * @return An array of module action state objects.
	 */
	List<ModuleActionState> getModuleActionStates(); 
		
	/**
	 * Block the controller.
	 * 
	 * @param blocker Object that blocks the workarea
	 */
	void block(Object blocker);
	
	/**
	 * Unblock the controller.
	 * 
	 * @param blocker Object that unblocks the workarea
	 */
	void unblock(Object blocker);
	
	/**
	 * Returns, if the controller is currently blocked.
	 * 
	 * @return true, if blocked. False otherwise.
	 */
	boolean isBlocked();
}
