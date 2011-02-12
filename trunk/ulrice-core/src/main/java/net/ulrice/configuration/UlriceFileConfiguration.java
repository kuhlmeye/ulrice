package net.ulrice.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.frame.impl.MainFrame;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.impl.ModuleManager;

/**
 * Loads the ulrice configuration from a java properties file.
 * 
 * @author ckuhlmeyer
 */
public class UlriceFileConfiguration extends AbstractUlriceConfiguration implements IFUlriceConfiguration {

	/** The properties. */
	private Properties properties;

	/** The module manager. */
	private IFModuleManager moduleManager;

	/** The main frame of the ulrice application. */
	private IFMainFrame mainFrame;

	private IFModuleStructureManager moduleStructureManager;

	/**
	 * Initialize ulrice by a file configuration.
	 * 
	 * @param configurationStream
	 *            The configuration file.
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	public static void initializeUlrice(InputStream configurationStream) throws ConfigurationException {
		new UlriceFileConfiguration(configurationStream);
	}

	/**
	 * Initialize ulrice by a file configuration.
	 * 
	 * @param configurationStream
	 *            The configuration file.
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	public UlriceFileConfiguration(InputStream configurationStream) throws ConfigurationException {
		super();

		try {
			properties = new Properties();
			properties.load(configurationStream);
		} catch (IOException e) {
			throw new ConfigurationException("Ulrice configuration file could not be loaded.", e);
		}

		// Initialize ulrice.
		Ulrice.initialize(this);
	}

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getConfigurationProperties()
	 */
	public Properties getConfigurationProperties() {
		return properties;
	}

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getModuleManager()
	 */
	public IFModuleManager getModuleManager() {
		return moduleManager;
	}

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getModuleStructureManager()
	 */
	public IFModuleStructureManager getModuleStructureManager() {
		return moduleStructureManager;
	}

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getMainFrame()
	 */
	public IFMainFrame getMainFrame() {
		return mainFrame;
	}

	public void loadConfiguration() throws ConfigurationException {

		// Load the needed configuration parameters.
		moduleManager = (IFModuleManager) loadClass(properties.getProperty(IFModuleManager.class.getName(),
				ModuleManager.class.getName()));
		moduleStructureManager = (IFModuleStructureManager) loadClass(properties.getProperty(
				IFModuleStructureManager.class.getName(), ModuleManager.class.getName()));
		mainFrame = (IFMainFrame) loadClass(properties.getProperty(IFMainFrame.class.getName(), MainFrame.class
				.getName()));

	}
}
