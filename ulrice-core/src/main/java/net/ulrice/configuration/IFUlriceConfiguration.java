package net.ulrice.configuration;

import java.util.Properties;

import net.ulrice.frame.IFMainFrame;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.security.IFAuthCallback;

/**
 * The ulrice application configuration.
 * 
 * @author ckuhlmeyer
 */
public interface IFUlriceConfiguration {

	/**
	 * Loads the configuration.
	 * 
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	void loadConfiguration() throws ConfigurationException;

	/**
	 * Returns the module manager of ulrice.
	 * 
	 * @return The module manager.
	 */
	IFModuleManager getModuleManager();

	/**
	 * Returns the main frame of ulrice.
	 * 
	 * @return The main frame.
	 */
	IFMainFrame getMainFrame();
	
	/**
	 * Returns the module structure manager from the properties.
	 * 
	 * @return The configured module structure manager.
	 */
	IFModuleStructureManager getModuleStructureManager();

	/**
	 * Returns the configuration properties.
	 * 
	 * @return The configuration properties.
	 */
	Properties getConfigurationProperties();
	
	/**
	 * Returns the callback for authentication
	 * 
	 * @return The auth callback
	 */
	IFAuthCallback getAuthCallback();
}
