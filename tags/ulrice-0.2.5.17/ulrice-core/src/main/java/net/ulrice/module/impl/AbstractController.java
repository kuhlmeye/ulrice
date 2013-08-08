package net.ulrice.module.impl;

import java.util.Collections;
import java.util.List;

import net.ulrice.Ulrice;
import net.ulrice.message.MessageSeverity;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleProvider;

/**
 * Creates an abstract controller.
 * 
 * @author ckuhlmeyer
 */
public abstract class AbstractController implements IFController {

	public IFModuleTitleProvider getTitleProvider() {
	    return null;
	}

	/**
	 * Initializes the sub-components.
	 * <ol>
	 * <li>Initialization of model.</li>
	 * <li>Initialization of view.</li>
	 * <li>Initialization of controller.</li>
	 * </ol>
	 * 
	 * @see net.ulrice.module.IFController#postCreation(net.ulrice.module.IFModule)
	 */	
	public void preCreate() {
	}

	/**
	 * @see net.ulrice.module.IFController#postCreationEvent(net.ulrice.module.IFModule)
	 */
	public void postCreate() {
	}

	/**
	 * Post an exception to the exception and message handler.
	 *  
	 * @param th The exception.
	 */
	public void postException(Throwable th) {
		Ulrice.getMessageHandler().handleException(this, th);
	}

	/**
	 * Post an exception to the exception and message handler with additional information
	 * 
	 * @param message The additional message.
	 * @param th The exception.
	 */
	public void postException(String message, Throwable th) {
		Ulrice.getMessageHandler().handleException(this, message, th);
	}

	/**
	 * Post an informational message to the message handler.
	 * 
	 * @param message the message.
	 */
	public void postInfoMessage(String message) {
		Ulrice.getMessageHandler().handleInformationMessage(this, message);		
	}
	
	/**
	 * Post a  message to the message handler.
	 * 
	 * @param severity The severity of the message.
	 * @param message the message.
	 */
	public void postMessage(MessageSeverity severity, String message) {
		Ulrice.getMessageHandler().handleMessage(this, severity, message);
	}

	@SuppressWarnings("unchecked")
    @Override
	public List<ModuleActionState> getHandledActions() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean performModuleAction(String actionId) {
		return false;
	}
}
