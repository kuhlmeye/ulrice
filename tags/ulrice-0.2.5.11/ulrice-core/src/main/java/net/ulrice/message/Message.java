/**
 * 
 */
package net.ulrice.message;

import net.ulrice.module.IFController;

/**
 * Represents a message handled by the message handler.
 * 
 * @author christof
 */
public class Message implements Comparable<Message> {

	/** The timestamp of the creation of this message. */
	private long creationTimestamp;
	
	/** The controller of this message or null, if this is a global message. */
	private IFController controller;

	/** The severity of this message. */
	private MessageSeverity severity;
	
	/** The message text. */
	private String message;
	
	/** The throwable contained in this message. */
	private Throwable throwable;
	
	/**
	 * Constructs a new message.
	 * 
	 * @param severity The severity of this message.
	 * @param message The message.
	 */
	public Message(MessageSeverity severity, String message) {
		this(null, severity, message, null);
	}
	
	/**
	 * Constructs a new message.
	 * 
	 * @param controller The controller to which this message belongs.
	 * @param severity The severity of this message.
	 * @param message The message.
	 * @param throwable The throwable of this message.
	 */
	public Message(IFController controller, MessageSeverity severity, String message, Throwable throwable) {
		this.controller = controller;
		this.severity = severity;
		this.message = message;
		this.throwable = throwable;
		this.creationTimestamp = System.currentTimeMillis();
	}

	/**
	 * @return the severity
	 */
	public MessageSeverity getSeverity() {
		return severity;
	}

	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(MessageSeverity severity) {
		this.severity = severity;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * @return the controller
	 */
	public IFController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(IFController controller) {
		this.controller = controller;
	}

	/**
	 * @return the creationTimestamp
	 */
	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Message o) {
		return Long.valueOf(o.getCreationTimestamp()).compareTo(getCreationTimestamp());
	}
}
