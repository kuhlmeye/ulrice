package net.ulrice;

import java.util.Properties;

import javax.swing.event.EventListenerList;

import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.IFUlriceConfiguration;
import net.ulrice.dialog.DialogManager;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.message.MessageHandler;
import net.ulrice.message.TranslationProvider;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.impl.action.ModuleActionManager;
import net.ulrice.process.ProcessManager;
import net.ulrice.security.GrantAllAuthCallback;
import net.ulrice.security.IFAuthCallback;
import net.ulrice.ui.UI;

/**
 * Class holding all context object of ulrice.
 * 
 * @author ckuhlmeyer
 */
public class Ulrice {

	/** The module manager used by this application. */
	private static IFModuleManager moduleManager;
	
	/** The manager used to structure the modules. */
	private static IFModuleStructureManager moduleStructureManager;
	
	/** The main frame used by this application. */
	private static IFMainFrame mainFrame;
	
	/** The message handler of ulrice. */
	private static MessageHandler messageHandler;
	
	/** The action manager of ulrice. */
	private static ModuleActionManager actionManager;
	
	private static ProcessManager processManager;

	/** Contains all other configuration values. */
	private static Properties configuration;
	
	/** Contains the I18N-Support */
	private static TranslationProvider translationProvider;
		
	/** The callback used to handle authorization requests. */
	private static IFAuthCallback securityManager;	
	
	private static DialogManager dialogManager;
	
	private static EventListenerList listenerList = new EventListenerList();


	/** 
	 * Initializes ulrice.
	 * 
	 * @param configuration The configuration used to initialize ulrice.
	 * @throws ConfigurationException If the configuration could not be loaded. 
	 */
	public static void initialize(IFUlriceConfiguration configuration) throws ConfigurationException {
		UI.applyDefaultUI();
		Ulrice.configuration = configuration.getConfigurationProperties();				
		Ulrice.moduleManager = configuration.getModuleManager();
		Ulrice.moduleStructureManager = configuration.getModuleStructureManager();
		Ulrice.messageHandler = new MessageHandler();
		Ulrice.actionManager = new ModuleActionManager();
		Ulrice.processManager = new ProcessManager();
		Ulrice.dialogManager = new DialogManager();
		Ulrice.translationProvider = configuration.getTranslationProvider();
		
		if(configuration.getAuthCallback() != null) {
			Ulrice.securityManager = configuration.getAuthCallback();
		} else {
			Ulrice.securityManager = new GrantAllAuthCallback();
		}
		
		Ulrice.mainFrame = configuration.getMainFrame();
		if(Ulrice.mainFrame != null) {
			Ulrice.mainFrame.inializeLayout();
		}
		
		ConfigurationListener[] listeners = listenerList.getListeners(ConfigurationListener.class);
		if(listeners != null) {
			for(ConfigurationListener listener : listeners) {
				listener.initializationFinished();
			}
		}
	}


	/** 
	 * Return the module manager of ulrice. 
	 * 
	 * @return The module manager.
	 */
	public static IFModuleManager getModuleManager() {
		return moduleManager;
	}
	
	/**
	 * Return the main frame of ulrice.
	 * 
	 * @return The main frame.
	 */
	public static IFMainFrame getMainFrame() {
		return mainFrame;
	}
	
	/**
	 * Return the module structure manager.
	 * 
	 * @return The module structure manager.
	 */
	public static IFModuleStructureManager getModuleStructureManager() {
		return moduleStructureManager;
	}
	
	/**
	 * Returns the message handler of ulrice.
	 * 
	 * @return The message handler.
	 */
	public static MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public static TranslationProvider getTranslationProvider() {
        return translationProvider;
    }

    /**
	 * Returns the action manager of ulrice. 
	 * 
	 * @return the actionManager
	 */
	public static ModuleActionManager getActionManager() {
		return actionManager;
	}
	
	public static ProcessManager getProcessManager() {
		return processManager;
	}
	

	public static DialogManager getDialogManager() {
		return dialogManager;
	}
	
	/**
	 * Returns a configuration value.
	 * 
	 * @param requestingObject The class name of the requestingObject is used as key prefix.
	 * @param key The parameter key.
	 * @param defaultValue The default value returned, if the value was not found.
	 * @return The configuration parameter value.
	 */
	public static String getConfiguration(Object requestingObject, String key, String defaultValue) {
		StringBuilder builder = new StringBuilder();
		builder.append(requestingObject.getClass().getName());
		builder.append('.');
		builder.append(key);
		if(Ulrice.configuration == null) {
			return defaultValue;
		}
		return Ulrice.configuration.getProperty(builder.toString(), defaultValue);
	}

	/**
	 * Returns the authorization callback.
	 * 
	 * @return The class handling the authorization callback
	 */
	public static IFAuthCallback getSecurityManager() {
		return securityManager;
	}
	
	public static void addConfigurationListener(ConfigurationListener listener) {
		listenerList.add(ConfigurationListener.class, listener);
	}
	
	public static void removeConfigurationListener(ConfigurationListener listener) {
		listenerList.remove(ConfigurationListener.class, listener);
	}
}
