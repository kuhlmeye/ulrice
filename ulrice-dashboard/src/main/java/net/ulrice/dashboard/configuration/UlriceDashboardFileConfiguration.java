/**
 * 
 */
package net.ulrice.dashboard.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.ulrice.configuration.AbstractUlriceConfiguration;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.dashboard.IFDashboardComponentProvider;
import net.ulrice.dashboard.IFSettings;
import net.ulrice.dashboard.UlriceDashboard;

/**
 * @author ekaveto
 *
 */
public class UlriceDashboardFileConfiguration extends
		AbstractUlriceConfiguration implements IFUlriceDashboardFileConfiguration {
	
	/** The properties. */
	private Properties properties;
	
	private IFDashboardComponentProvider dashboardComponentProvider;
	
	private IFSettings settings;
	
	/**
	 * Initialize ulrice-dashboard by a file configuration.
	 * 
	 * @param configurationStream
	 *            The configuration file.
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	public static void initializeUlriceDashboard(InputStream configurationStream) throws ConfigurationException {
		new UlriceDashboardFileConfiguration(configurationStream);
	}
	
	/**
	 * Initialize ulrice-dashboard by a file configuration.
	 * 
	 * @param configurationStream
	 *            The configuration file.
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	public UlriceDashboardFileConfiguration(InputStream configurationStream) throws ConfigurationException {
		super();

		try {
			properties = new Properties();
			properties.load(configurationStream);
		} catch (IOException e) {
			throw new ConfigurationException("Ulrice configuration file could not be loaded.", e);
		}
		
		// initialize Ulrice-Dashboard
		UlriceDashboard.initialize(this);
	}

	/**
	 * @see net.ulrice.dashboard.configuration.IFUlriceDashboardFileConfiguration#loadConfiguration()
	 */
	@Override
	public void loadConfiguration() throws ConfigurationException {
		// load the needed configuration parameters
		dashboardComponentProvider = (IFDashboardComponentProvider) loadClass(properties.getProperty(IFDashboardComponentProvider.class.getName()));
		settings = (IFSettings) loadClass(properties.getProperty(IFSettings.class.getName()));
	}

	/**
	 * @see net.ulrice.dashboard.configuration.IFUlriceDashboardFileConfiguration#getDashboardComponentProvider()
	 */
	@Override
	public IFDashboardComponentProvider getDashboardComponentProvider() {
		return dashboardComponentProvider;
	}

	/**
	 * @see net.ulrice.dashboard.configuration.IFUlriceDashboardFileConfiguration#getConfigurationProperties()
	 */
	@Override
	public Properties getConfigurationProperties() {
		return properties;
	}

	@Override
	public IFSettings getSettings() {
		return settings;
	}

}
