/**
 * 
 */
package net.ulrice.dashboard.configuration;

import java.util.Properties;

import net.ulrice.configuration.ConfigurationException;
import net.ulrice.dashboard.IFDashboardComponentProvider;
import net.ulrice.dashboard.IFSettings;

/**
 * The ulrice dashboard configuration.
 * 
 * @author ekaveto
 *
 */
public interface IFUlriceDashboardFileConfiguration {
	
	/**
	 * Loads the configuration.
	 * 
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	void loadConfiguration() throws ConfigurationException;
	
	/**
	 * Returns the DashboardComponentProvider of ulrice-dashboard.
	 * 
	 * @return The DashboardComponentProvider.
	 */
	IFDashboardComponentProvider getDashboardComponentProvider();
	
	/**
	 * Returns the Settings-Service to put/get/save/load properties.
	 * 
	 * @return The Settings-Service.
	 */
	IFSettings getSettings();
	
	/**
	 * Returns the configuration properties.
	 * 
	 * @return The configuration properties.
	 */
	Properties getConfigurationProperties();

}
