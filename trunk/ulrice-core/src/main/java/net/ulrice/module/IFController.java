package net.ulrice.module;

import java.util.List;

import javax.swing.JComponent;

import net.ulrice.module.impl.ModuleActionState;

/**
 * Interface of the module controller, i.e. the handle for one 'instance' of a module.
 * 
 * @author ckuhlmeyer
 */
public interface IFController {

	/**
	 * Returns the view of the controller.
	 */
	JComponent getView();
	
	/**
	 * Returns the renderer for rendering the title of this module. The reason the controller is even
	 *  asked for the title provider - instead of always asking the module itself - is to allow current
	 *  data from the module to be part of the title.<br>
	 *  
	 * A controller may return <code>null</code>, in which case the default implementation provided
	 *  by the module is used.
	 */
	IFModuleTitleProvider getTitleProvider();

	/**
	 * Method called by the module manager after instantiation and before creation event notification
	 */
	void preCreate(); //TODO ehaasec turn this into regular events that are fired via listeners

	/**
	 * Method called by the module manager after instantiation and after creation event notification
	 */
	void postCreate();
	
	/**
	 * Called by the module action manager, if a module action was executed.
	 * 
	 * @return true, if the action was processed by this module. False otherwise.
	 */
	boolean performModuleAction(String actionId);
	
	/**
	 * Returns the array of module actions states that are handled by the module. 
	 */
	List<ModuleActionState> getHandledActions(); 
		
//	/**
//	 * Block the controller.
//	 */
//	void block(Object blocker);
//	
//	/**
//	 * Unblock the controller.
//	 */
//	void unblock(Object blocker);
//	
//	/**
//	 * Returns, if the controller is currently blocked.
//	 */
//	boolean isBlocked();
}
