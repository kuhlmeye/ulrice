package net.ulrice.dashboard;

import java.util.EventListener;
/**
 * The interface inform about activities on the dashboard
 * 
 * @author dv20jac
 *
 */
public interface DashboardEventListener extends EventListener {
    
    /**
     * Add a module to the dashboard
     * 
     * @param controller The controller from the module
     */
    void addModule(DashboardComponent dashboardComponent);
   
    /**
     * Removes an module from the dashboard
     * 
     * @param controller The controller from the module
     */
    void removeModule(DashboardComponent dashboardComponent);

}
