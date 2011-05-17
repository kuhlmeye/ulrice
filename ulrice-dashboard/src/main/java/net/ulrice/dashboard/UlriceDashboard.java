/**
 * 
 */
package net.ulrice.dashboard;

import java.util.Properties;

import net.ulrice.configuration.ConfigurationException;
import net.ulrice.dashboard.configuration.IFUlriceDashboardFileConfiguration;

/**
 * @author ekaveto
 *
 */
public class UlriceDashboard {

	/** Provider of DashboardComponents */
	private static IFDashboardComponentProvider dashboardComponentProvider;
	
	/** Service to load/save/put/get properties */
	private static IFSettings settings;
	
	/** The configuration of ulrice-dashboard */
	private static Properties configuration;
	
	public static void initialize(IFUlriceDashboardFileConfiguration configuration) throws ConfigurationException {
		configuration.loadConfiguration();
		UlriceDashboard.configuration = configuration.getConfigurationProperties();
		UlriceDashboard.dashboardComponentProvider = configuration.getDashboardComponentProvider();
		UlriceDashboard.settings = configuration.getSettings();
	}

	/**
	 * @return the dashboardComponentProvider
	 */
	public static IFDashboardComponentProvider getDashboardComponentProvider() {
		return dashboardComponentProvider;
	}
	
	/**
	 * @return the settings
	 */
	public static IFSettings getSettings() {
		return settings;
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
		if(UlriceDashboard.configuration == null) {
			return defaultValue;
		}
		return UlriceDashboard.configuration.getProperty(builder.toString(), defaultValue);
	}

}
