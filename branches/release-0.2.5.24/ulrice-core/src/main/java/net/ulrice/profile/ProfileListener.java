package net.ulrice.profile;

import java.util.EventListener;

/**
 * Listener interface for events of the profile manager.
 * 
 * @author christof
 */
public interface ProfileListener extends EventListener {

	/**
	 * The profile was created.
	 */
	void profileCreated(Profile profile);

	/**
	 * The profile was updated. 
	 */
	void profileUpdated(Profile profile);
	
	/**
	 * The profile was removed.
	 */
	void profileDeleted(Profile profile);
}
