package net.ulrice.dashboard;

/**
 * 
 * The interface Dashboard contains required methods for the implementation
 *
 * @author dv20jac
 *
 */
public interface Dashboard {
    
    /**
     * 
     * Restores the modules that are placed on the dashbord before. At first the
     * information are received from the properties file later by a database.
     */
    void restoreModules();

}
