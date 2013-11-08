package net.ulrice.configuration;

/**
 * Exception thrown, if the ulrice configuration fails.
 * 
 * @author ckuhlmeyer
 */
public class ConfigurationException extends Exception {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 5056816869509590055L;

	/**
	 * Creates a new ulrice configuration exception.
	 * 
	 * @param reason The reason why this exception is thrown.
	 * @param cause The cause of the exception or null.
	 */
	public ConfigurationException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
