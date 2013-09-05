package net.ulrice;

import java.util.EventListener;


/**
 * Interface for listeners that will be informed after ulrice is initialized.
 * 
 * @author christof
 */
public interface ConfigurationListener extends EventListener {

	/**
	 * Ulrice is fully initialized.
	 */
	void initializationFinished();
}
