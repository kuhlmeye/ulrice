package net.ulrice.dashboard.module;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import net.ulrice.dashboard.DashboardComponent;
import net.ulrice.dashboard.DashboardEventListener;
import net.ulrice.dashboard.IFDashboardComponentProvider;
import net.ulrice.dashboard.UlriceDashboard;
import net.ulrice.dashboard.module.VDashboard.CellComponent;
import net.ulrice.module.impl.AbstractController;

/**
 * Controller of the dashboard module. The dashboard module is used to start all
 * other modules.
 * 
 * @author christof
 */
public class CDashboard extends AbstractController {


	/** List for dashbard event listener */
	private List<DashboardEventListener> dashBoardEventListeners;
	
	/** Provider of DashboardComponents */
	private IFDashboardComponentProvider dashboardComponentProvider;

    private MDashboard model;

    private VDashboard view;

	/**
	 * Constructor of this class
	 */
	public CDashboard() {
		dashboardComponentProvider = UlriceDashboard.getDashboardComponentProvider();
        model = new MDashboard();
        model.initialize(this);
        view = new VDashboard();
        view.initialize(this);
	}


	protected void postEventInitialization() {
		view.restoreModules();
	}

	/**
	 * Adds a dashboard component to the model
	 * 
	 * @param key
	 *            The unique module id
	 * @param value
	 *            The <code>CellComponent</code>
	 */
	protected void addDashboardComponent(String key, CellComponent value) {
		model.addDashBoardComponent(key, value);
	}

	/**
	 * Contains an module already on the dashboard
	 * 
	 * @param key
	 *            The unique module id
	 * @return If exist then true, false otherwise
	 */
	protected boolean containsDashboardComponent(String key) {
		return model.containsDashboardComponent(key);
	}

	/**
	 * Removes an dashboard component from the model
	 * 
	 * @param moduleId
	 *            Remove dash board component by model
	 */
	protected void deleteDashBoardComponent(String moduleId) {
		model.removeDashboardComponent(moduleId);
	}

	/**
	 * 
	 * Gets for a given x and y-axis position the right
	 * <code>CellComponent</code>
	 * 
	 * @param cellX
	 *            Cell x position
	 * @param cellY
	 *            Cell y position
	 * @return If found then the component, otherwise null
	 */
	protected CellComponent getDashBoardComponent(int cellX, int cellY) {
		return model.getDashBoardComponent(cellX, cellY);
	}

	/**
	 * Check possible collision between <code>CellComponent</code>
	 * 
	 * @param moduleId
	 *            The module id of the cell component which is to test
	 * @param rectangle
	 *            The rectangle to check
	 * @return If a collision is detected then true, false otherwise
	 */
	protected boolean checkCollision(String moduleId, Rectangle rectangle) {
		for (Map.Entry<String, CellComponent> cellComponent : model
				.getDashBordSet()) {
			if (!cellComponent.getKey().equals(moduleId)) {
				if (cellComponent.getValue().getJComponent().getBounds()
						.intersects(rectangle)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Saves the position of the given <code>CellComponent</code>
	 * 
	 * @param cellComponent
	 *            The CellComponent that is to be saved
	 */
	protected void saveDashBoardComponentProperties(CellComponent cellComponent) {
	    UlriceDashboard.getSettings().saveProperties(cellComponent.getDashboardComponent()
				.getUniqueId(), cellComponent.getStartCell().getX() + ";"
				+ cellComponent.getStartCell().getY() + "-"
				+ cellComponent.getEndCell().getX() + ";"
				+ cellComponent.getEndCell().getY());
	}

	/**
	 * Deletes properties for a <code>CellComponent</code>
	 * 
	 * @param moduleId
	 *            The unique moduleId
	 */
	protected void deleteDashBoardComponentProperties(String moduleId) {
		UlriceDashboard.getSettings().removeKey(moduleId);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see vwg.omd.dealerrc.base.BaseController#isTabClosable()
	 */
	public boolean isTabClosable() {
		return false;
	}

	/**
	 * Inform all listeners about removing an module on the dashboard
	 * 
	 * @param controller
	 *            The controller that has been removed
	 */
	protected void removeDashbordModuleEvent(
			DashboardComponent dashboardComponent) {
		for (DashboardEventListener dev : dashBoardEventListeners) {
			dev.removeModule(dashboardComponent);
		}
	}

	/**
	 * Adds a new listener
	 * 
	 * @param dev
	 *            A listener
	 */
	public void addDashboardEventListener(DashboardEventListener dev) {
		dashBoardEventListeners.add(dev);
	}

	/**
	 * Removes an dashboard listener
	 * 
	 * @param dev
	 *            The listener that is to be deleted
	 */
	public void removeDashboardEventListener(DashboardEventListener dev) {
		dashBoardEventListeners.remove(dev);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see vwg.omd.dealerrc.base.BaseController#getMenuInformationCommandBar()
	 */
	public String getMenuInformationCommandBar() {
		return "> Available Modules";
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see vwg.omd.dealerrc.base.BaseController#getMenuInformationWorkarea()
	 */
	public String getMenuInformationWorkarea() {
		return "> Dashboard";
	}

	public IFDashboardComponentProvider getDashboardComponentProvider() {
		return dashboardComponentProvider;
	}

    @Override
    public JComponent getView() {
        return view.getView();
    }
}
