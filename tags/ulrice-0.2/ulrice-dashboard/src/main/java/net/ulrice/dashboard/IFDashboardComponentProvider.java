package net.ulrice.dashboard;

import java.util.List;

/**
 * 
 * This interface has to be implemented in order to provide a list of DashboardComponents to the Dashboard to be aware of.
 * 
 * @author ekaveto
 *
 */
public interface IFDashboardComponentProvider {
	
	List<DashboardComponent> getDashboardComponentList();

}
