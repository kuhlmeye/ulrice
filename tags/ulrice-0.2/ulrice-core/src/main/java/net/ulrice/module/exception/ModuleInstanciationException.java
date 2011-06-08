package net.ulrice.module.exception;

/**
 * Thrown if the instanciation of a module fails. 
 * 
 * @author ckuhlmeyer
 */
public class ModuleInstanciationException extends Exception {

	/** Default generated serial module id. */
	private static final long serialVersionUID = -4181629062909817115L;
	
	/**
	 * Creates a new module instanciation exception.
	 * 
	 * @param reason The error reason.
	 * @param cause The exception caused this exception or null.
	 */
	public ModuleInstanciationException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
