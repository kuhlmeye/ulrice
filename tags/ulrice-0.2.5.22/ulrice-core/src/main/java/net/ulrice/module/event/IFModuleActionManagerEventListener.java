/**
 * 
 */
package net.ulrice.module.event;

import java.util.EventListener;

/**
 * Interface implemented by all listeners of the module action manager.
 * 
 * @author christof
 */
public interface IFModuleActionManagerEventListener extends EventListener {

	/**
	 * The set of application actions changed.
	 */
	void applicationActionsChanged();

	void moduleActionsChanged();
}
