/**
 * 
 */
package net.ulrice.dashboard.configuration;

import java.util.List;

import net.ulrice.dashboard.DashboardComponent;
import net.ulrice.dashboard.IFSettings;

/**
 * The ulrice dashboard configuration.
 * 
 * @author ekaveto
 *
 */
public interface IFUlriceDashboardConfiguration {
	
	
	/**
	 * Returns the Settings-Service to put/get/save/load properties.
	 * 
	 * @return The Settings-Service.
	 */
	IFSettings getSettings();

    List<DashboardComponent> getDashboardComponentList();

}
