
package net.ulrice.dashboard.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ulrice.dashboard.module.VDashboard.CellComponent;

import net.ulrice.module.IFModel;

/**
 * Datamodel of the dashboard module.
 * 
 * @author christof
 */
public class MDashboard implements IFModel {

    /** Default generated serial version uid. */
    private static final long serialVersionUID = 1731283174024412217L;
    
    /** */
    private Map<String, CellComponent> dashboardComponentMap;
    

    /**
     * Initialize the table.
     * 
     * @see net.ulrice.module.IFModel#initialize()
     */
    @Override
    public void initialize() {
        dashboardComponentMap = new HashMap<String, CellComponent>();
    }

    /**
     * Adds a new <code>CellComponent</code> to the map
     * 
     * @param key The key
     * @param value The value
     */
    protected void addDashBoardComponent(String key, CellComponent value) {
        dashboardComponentMap.put(key, value);
    }

    /**
     * Returns a <code>CellComponent</code> for a given key  
     * @param key The key
     * @return If found the cell component, null otherwise
     */
    protected CellComponent getDashboardComponent(String key) {
        return dashboardComponentMap.get(key);
    }

    /**
     * Check if a <code>CellComponent</code> already exists
     *
     * @param key The key
     * @return If found then true, false otherwise
     */
    protected boolean containsDashboardComponent(String key) {
        return dashboardComponentMap.containsKey(key);
    }
    
    /**
     * Remove an dashboard component
     *
     * @param key The key to identify the component
     */
    protected void removeDashboardComponent(String key) {
       dashboardComponentMap.remove(key);
    }
    
    /**
     * Find by a given x and y cell position the <code>CellComponent</code>
     * 
     * @param cellX X position
     * @param cellY Y position
     * @return If found the component, null otherwise
     */
    protected CellComponent getDashBoardComponent(int cellX, int cellY) {
        for (Map.Entry<String, CellComponent> set : dashboardComponentMap.entrySet()) {
            if (cellX >= set.getValue().getStartCell().getX() && cellX <= set.getValue().getEndCell().getX()) {
                if (cellY >= set.getValue().getStartCell().getY() && cellY <= set.getValue().getEndCell().getY()) {
                    return set.getValue();
                }
            }
        }
        return null;
    }
    
    /**
     * Returns a <code>Set</code> of cell component inside an <code>Map.Entry</code> .
     * @return Set of <code>CellComponent</code>
     */
    protected Set<Map.Entry<String, CellComponent>> getDashBordSet() {
        return dashboardComponentMap.entrySet();
    }
}
