package net.ulrice.message;

/**
 * Defines the severity of the message.
 * 
 * @author christof
 */
public enum MessageSeverity {

	/** For all uncaught exceptions. */
	UncaughtException,
	
	/** For all caught exceptions. */
	Exception,
	
	/** For Application or module errors. */
	Error,
	
	/** For warning messages. */
	Warning,
	
	/** For informational messages. */
	Information,
	
	/** For status informations. (e.g. process finished,...) */
	Status,
	
	/** For debug messages. */
	Debug;	
}
