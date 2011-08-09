/**
 * 
 */
package net.ulrice.dashboard;

import java.util.List;

import net.ulrice.dashboard.configuration.IFUlriceDashboardConfiguration;

/**
 * @author ekaveto
 *
 */
public class UlriceDashboard {
	
	/** Service to load/save/put/get properties */
	private static IFSettings settings;
	
    private static List<DashboardComponent> dashboardComponentList;
	
	public static void initialize(IFUlriceDashboardConfiguration configuration) {
		UlriceDashboard.dashboardComponentList = configuration.getDashboardComponentList();
		UlriceDashboard.settings = configuration.getSettings();
	}
	
	/**
	 * @return the settings
	 */
	public static IFSettings getSettings() {
		return settings;
	}
	
	public static List<DashboardComponent> getDashboardComponentList() {
        return dashboardComponentList;
    }
}
