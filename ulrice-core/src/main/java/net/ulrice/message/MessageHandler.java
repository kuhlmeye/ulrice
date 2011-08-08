package net.ulrice.message;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.event.IFModuleEventListener;

/**
 * This class cares about all kinds of module messages (informational messages,
 * exceptions, process messages,...) and of global messages (exception,..). This
 * class is also the default uncaught exception handler
 * 
 * @author christof
 */
public class MessageHandler implements UncaughtExceptionHandler, IFModuleEventListener {

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(MessageHandler.class.getName());

	/** The list of the event handlers. */
	private EventListenerList listenerList;

	/** List of all messsages not assigned to a module. */
	private List<Message> globalMessages;

	/** Map holding the lists of all module specific messages. */
	private Map<IFController, List<Message>> moduleMessages;

	/**
	 * Creates a new message handler instance.
	 */
	public MessageHandler() {
		Thread.setDefaultUncaughtExceptionHandler(this);
		
		Ulrice.getModuleManager().addModuleEventListener(this);

		this.listenerList = new EventListenerList();
		this.globalMessages = new LinkedList<Message>();
		this.moduleMessages = new HashMap<IFController, List<Message>>();
	}

	/**
	 * Returns the list of global messages.
	 * 
	 * @return The list of global messages;
	 */
	public List<Message> getGlobalMessages() {
		return getMessages(null);
	}

	/**
	 * Return the list of messages for a controller or the global message list.
	 * 
	 * @param controller
	 *            The controller or null if the global message list should be
	 *            returned.
	 * 
	 * @return The list of messages.
	 */
	public List<Message> getMessages(IFController controller) {
		if (controller == null) {
			return globalMessages;
		}

		if (!moduleMessages.containsKey(controller)) {
			return null;
		}
		return moduleMessages.get(controller);
	}

	/**
	 * Handle exception.
	 * 
	 * @param throwable
	 *            The exception.
	 */
	public void handleException(Throwable throwable) {
		handleException(null, throwable.getLocalizedMessage(), throwable);
	}

	/**
	 * Handle exception.
	 * 
	 * @param controller
	 *            The controller in which the exception is occurred
	 * @param throwable
	 *            The exception.
	 */
	public void handleException(IFController controller, Throwable throwable) {
		handleException(controller, throwable.getLocalizedMessage(), throwable);
	}

	/**
	 * Handle exception.
	 * 
	 * @param controller
	 *            The controller in which the exception is occurred
	 * @param message
	 *            An additional exception message.
	 * @param throwable
	 *            The exception.
	 */
	public void handleException(IFController controller, String message, Throwable throwable) {
		handleMessage(controller, new Message(controller, MessageSeverity.Exception, message, throwable));
	}

	/**
	 * Handle an informational message.
	 * 
	 * @param controller
	 *            The controller.
	 * @param message
	 *            The message.
	 */
	public void handleInformationMessage(IFController controller, String message) {
		handleMessage(controller, new Message(controller, MessageSeverity.Information, message, null));
	}

	/**
	 * Handle a message with a given severity.
	 * 
	 * @param controller
	 *            The controller.
	 * @param severity
	 *            The severity.
	 * @param message
	 *            The message.
	 */
	public void handleMessage(IFController controller, MessageSeverity severity, String message) {
		handleMessage(controller, new Message(severity, message));
	}

	/**
	 * Adds a global message to the handler.
	 * 
	 * @param message
	 *            The message.
	 */
	public void handleMessage(Message message) {
		handleMessage(null, message);
	}

	/**
	 * Adds a message to the message handler.
	 * 
	 * @param controller
	 *            The controller or null if this is a global message
	 * @param message
	 *            The message object itself.
	 */
	public void handleMessage(IFController controller, Message message) {
		if (message == null) {
			LOG.finer("Ignore null message.");
			return;
		}

		if (controller == null && globalMessages != null) {
			LOG.finer("Add message to global message list. Message: " + message.getMessage());
			globalMessages.add(message);
			fireMessageHandled(message);
		} else if (moduleMessages.containsKey(controller) && moduleMessages.get(controller) != null) {
			LOG.finer("Add message to module specific message list. Message: " + message.getMessage());
			moduleMessages.get(controller).add(message);
			fireMessageHandled(message);
		} else {
			LOG.warning("Message ignored. Message: " + message.getMessage());
		}
		
	}

	/**
	 * Add a message event listener to the list of event listeners.
	 * 
	 * @param listener
	 *            A message event listener.
	 */
	public void addMessageEventListener(IFMessageEventListener listener) {
		listenerList.add(IFMessageEventListener.class, listener);
	}

	/**
	 * Remove a message event listener from the list of event listeners.
	 * 
	 * @param listener
	 *            A message event listener.
	 */
	public void removeMessageEventListener(IFMessageEventListener listener) {
		listenerList.remove(IFMessageEventListener.class, listener);
	}

	/**
	 * Fire a newly handled message
	 */
	private void fireMessageHandled(Message message) {
		IFMessageEventListener[] listeners = listenerList.getListeners(IFMessageEventListener.class);
		if (listeners != null) {
			for (IFMessageEventListener listener : listeners) {
				listener.messageOccurred(message);
			}
		}
	}

	/**
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable th) {
		try {
			handleMessage(null, new Message(null, MessageSeverity.UncaughtException, th.getLocalizedMessage(), th));
		} catch (Throwable handlingTh) {
			LOG.log(Level.SEVERE, handlingTh.getLocalizedMessage(), handlingTh);
		} finally {
			LOG.log(Level.SEVERE, th.getLocalizedMessage(), th);
		}
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	@Override
	public void openModule(IFController controller) {
		// Prepare data structure for a new controller.
		moduleMessages.put(controller, new LinkedList<Message>());
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
	 */
	@Override
	public void closeController(IFController controller) {
		// Remove all messages from the module.
		moduleMessages.remove(controller);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void activateModule(IFController controller) {
		// Nothing to do in here.
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void deactivateModule(IFController controller) {
		// Nothing to do in here.
	}

	@Override
	public void moduleBlocked(IFController controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleUnblocked(IFController controller) {
		// TODO Auto-generated method stub
		
	}
}
