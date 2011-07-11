package net.ulrice.databinding.modelaccess.impl;

/**
 * Exception thrown, if an error occurs during accessing data via reflection.
 * 
 * @author christof
 */
public class ReflectionMVAException extends RuntimeException {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -3211120627063188216L;

	/**
	 * Creates a new reflection data accessor exception.
	 * 
	 * @param message The message.
	 * @param cause The cause.
	 */
	public ReflectionMVAException(String message, Throwable cause) {
		super(message, cause);
	}	
}
