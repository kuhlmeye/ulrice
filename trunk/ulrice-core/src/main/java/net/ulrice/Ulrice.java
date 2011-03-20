package net.ulrice;

import java.util.Properties;

import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.IFUlriceConfiguration;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.message.MessageHandler;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.impl.action.ModuleActionManager;
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

	/** Contains all other configuration values. */
	private static Properties configuration;
		

	/** 
	 * Initializes ulrice.
	 * 
	 * @param configuration The configuration used to initialize ulrice.
	 * @throws ConfigurationException If the configuration could not be loaded. 
	 */
	public static void initialize(IFUlriceConfiguration configuration) throws ConfigurationException {
		UI.applyDefaultUI();
		configuration.loadConfiguration();
		Ulrice.configuration = configuration.getConfigurationProperties();
				
		Ulrice.moduleManager = configuration.getModuleManager();
		Ulrice.moduleStructureManager = configuration.getModuleStructureManager();
		Ulrice.messageHandler = new MessageHandler();
		Ulrice.actionManager = new ModuleActionManager();

		Ulrice.mainFrame = configuration.getMainFrame();
		Ulrice.mainFrame.inializeLayout();		
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

	/**
	 * Returns the action manager of ulrice. 
	 * 
	 * @return the actionManager
	 */
	public static ModuleActionManager getActionManager() {
		return actionManager;
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

}
