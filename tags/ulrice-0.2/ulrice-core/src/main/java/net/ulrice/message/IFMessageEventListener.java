package net.ulrice.message;

import java.util.EventListener;

/**
 * Interface for all message event listeners.
 *  
 * @author christof
 */
public interface IFMessageEventListener extends EventListener {

	/**
	 * Called by the message handler after receiving a new message.
	 * 
	 * @param message The message object.
	 */
	void messageOccurred(Message message);	
}
