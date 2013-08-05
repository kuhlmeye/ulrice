package net.ulrice.module.event;

import java.util.EventListener;

import net.ulrice.module.IFController;

/**
 * Interface for event listeners listening on module events.
 * 
 * @author christof
 */
public interface IFModuleEventListener extends EventListener {

	/**
	 * A new module was instanciated.
	 * 
	 * @param activeController The instanciated module.
	 */
	void openModule(IFController activeController);
	
	/**
	 * A controller was activated.
	 * 
	 * @param activeController The newly active controller.
	 */
	void activateModule(IFController activeController);
	
	/** 
	 * A controller was deactivated.
	 * 
	 * @param activeController The now deactivated controller.
	 */
	void deactivateModule(IFController activeController);
	
	/**
	 * A module is about to be closed.
	 * 
	 * @param activeController The module which is about to be closed.
	 */
	void closeController(IFController activeController);
	
	void moduleBlocked(IFController controller, Object blocker);
	
	void moduleUnblocked(IFController controller, Object blocker);
	
	void moduleBlockerRemoved(IFController controller, Object blocker);
	
	void nameChanged(IFController controller);
}
