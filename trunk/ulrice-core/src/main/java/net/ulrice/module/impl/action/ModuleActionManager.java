package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleRenderer.Usage;
import net.ulrice.module.event.IFModuleActionManagerEventListener;
import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.impl.ModuleActionState;

/**
 * This class handles the actions of the application.
 * 
 * @author christof
 */
public class ModuleActionManager implements IFModuleEventListener, PropertyChangeListener {

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(ModuleActionManager.class.getName());

	/** The controller that is currently active. */
	private IFController activeController;

	private Map<IFController, List<Action>> controllerActionOrderMap = new HashMap<IFController, List<Action>>();
	
	/** The map of the action-states of the modules. */
	private Map<IFController, Map<Action, ModuleActionState>> controllerActionStateMap = new HashMap<IFController, Map<Action, ModuleActionState>>();

	/** The standard actions of an application. */
	private Map<String, Action> applicationActions = new HashMap<String, Action>();

	/** The list of event listeners. */
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * Creates a new module action manager.
	 */
	public ModuleActionManager() {
		Ulrice.getModuleManager().addModuleEventListener(this);
	}

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addModuleActionManagerEventListener(IFModuleActionManagerEventListener listener) {
		listenerList.add(IFModuleActionManagerEventListener.class, listener);
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void removeModuleActionManagerEventListener(IFModuleActionManagerEventListener listener) {
		listenerList.remove(IFModuleActionManagerEventListener.class, listener);
	}

	/**
	 * Informs all listners that the list of application actions has changed.
	 */
	public void fireApplicationActionsChanged() {
		IFModuleActionManagerEventListener[] listeners = listenerList.getListeners(IFModuleActionManagerEventListener.class);
		if (listeners != null) {
			for (IFModuleActionManagerEventListener listener : listeners) {
				listener.applicationActionsChanged();
			}
		}
	}

	/**
	 * Performs an action. In this method the action will be checked against the
	 * controller settings and the delegated to the currently active module.
	 * 
	 * @param moduleAction
	 *            The action that should be performed.
	 * @param e
	 *            The action event.
	 */
	public void performAction(Action moduleAction, ActionEvent e) {

		if (activeController != null) {

			if (!Ulrice.getSecurityManager().allowExecuteAction(activeController, moduleAction)) {
				LOG.info("Action [Id: " + moduleAction.getUniqueId() + ", Module: "
						+ activeController.getModule().getModuleTitle(Usage.Default)
						+ "] will not be executed. Not authorized by ulrice security manager.");
				return;
			}

			Map<Action, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionStateMap != null) {
				ModuleActionState moduleActionState = actionStateMap.get(moduleAction);
				if (moduleActionState != null && moduleActionState.isEnabled()) {
					activeController.performModuleAction(moduleAction.getUniqueId());
				}
			}
		}
	}

	/**
	 * Adds an action to the application actions.
	 * 
	 * @param moduleAction
	 *            The action.
	 */
	public void addApplicationAction(Action moduleAction) {		

		if (!Ulrice.getSecurityManager().allowRegisterAction(activeController, moduleAction)) {
			LOG.info("Application-Action [Id: " + moduleAction.getUniqueId() + "] will not be added. Not authorized by ulrice security manager.");
			return;
		}
				

		moduleAction.addPropertyChangeListener(this);
		applicationActions.put(moduleAction.getUniqueId(), moduleAction);
		fireApplicationActionsChanged();
	}

	/**
	 * Removed an action from the list of application actions.
	 * 
	 * @param moduleAction
	 *            The action.
	 */
	public void removeApplicationAction(Action moduleAction) {
		moduleAction.removePropertyChangeListener(this);
		applicationActions.remove(moduleAction.getUniqueId());
		fireApplicationActionsChanged();
	}

	/**
	 * Returns an application action by the action id.
	 * 
	 * @param uniqueId
	 *            The application action id.
	 * @return The application action.
	 */
	public Action getApplicationAction(String uniqueId) {
		return applicationActions.get(uniqueId);
	}

	/**
	 * 
	 */
	public List<Action> adaptActionStates() {
		if (applicationActions != null) {
			for (Action moduleAction : applicationActions.values()) {
				if (!ActionType.SystemAction.equals(moduleAction.getType())) {
					moduleAction.setEnabled(moduleAction.isInitiallyEnabled());
				}
			}
		}

		if (activeController != null) {
			Map<Action, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionStateMap != null) {
				List<Action> moduleActionList = new ArrayList<Action>(actionStateMap.size());
				for (ModuleActionState moduleActionState : actionStateMap.values()) {
					Action action = moduleActionState.getAction();
					if (!ActionType.SystemAction.equals(action.getType())) {
						action.setEnabled(moduleActionState.isEnabled());
					}
				}
				return moduleActionList;
			}
		}
		return new ArrayList<Action>(0);
	}

	/**
	 * 
	 */
	public List<Action> getModuleActions() {
		if (activeController != null) {
			List<Action> actionList = controllerActionOrderMap.get(activeController);
			Map<Action, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionList != null && actionStateMap != null) {
				List<Action> moduleActionList = new ArrayList<Action>(actionList.size());
				for (Action action : actionList) {
					ModuleActionState moduleActionState = actionStateMap.get(action);
					String uniqueId = moduleActionState.getAction().getUniqueId();
					if (!applicationActions.containsKey(uniqueId)) {
						moduleActionList.add(moduleActionState.getAction());
					}
				}
				return moduleActionList;
			}
		}
		return new ArrayList<Action>(0);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	@Override
	public void openModule(IFController activeController) {
		this.activeController = activeController;
		List<ModuleActionState> moduleActionStates = activeController.getModuleActionStates();

		List<Action> actionOrder = new ArrayList<Action>(moduleActionStates.size());
		Map<Action, ModuleActionState> actionStateMap = new HashMap<Action, ModuleActionState>();
		if (moduleActionStates != null) {
			for (ModuleActionState moduleActionState : moduleActionStates) {
				Action moduleAction = moduleActionState.getAction();
				if (!Ulrice.getSecurityManager().allowRegisterAction(activeController, moduleAction)) {
					LOG.info("Action [Id: " + moduleAction.getUniqueId() + ", Module: "
							+ activeController.getModule().getModuleTitle(Usage.Default)
							+ "] will not be added. Not authorized by ulrice security manager.");
					return;
				}

				actionOrder.add(moduleAction);
				actionStateMap.put(moduleActionState.getAction(), moduleActionState);
			}
		}
		controllerActionOrderMap.put(activeController, actionOrder);
		controllerActionStateMap.put(activeController, actionStateMap);

		adaptActionStates();
		fireApplicationActionsChanged();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeModule(net.ulrice.module.IFController)
	 */
	@Override
	public void closeModule(IFController activeController) {

		this.activeController = null;
		controllerActionStateMap.remove(activeController);

		adaptActionStates();
		fireApplicationActionsChanged();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void activateModule(IFController activeController) {
		this.activeController = activeController;

		adaptActionStates();
		fireApplicationActionsChanged();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void deactivateModule(IFController activeController) {
		this.activeController = null;

		adaptActionStates();
		fireApplicationActionsChanged();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("enabled".equalsIgnoreCase(evt.getPropertyName())) {
			Object source = evt.getSource();

			if (source instanceof Action) {
				Action moduleAction = (Action) source;
				if (!Ulrice.getSecurityManager().allowEnableAction(activeController, moduleAction)) {
					LOG.info("Action [Id: " + moduleAction.getUniqueId() + ", Module: "
							+ activeController.getModule().getModuleTitle(Usage.Default)
							+ "] will not be enabled. Not authorized by ulrice security manager.");
					return;
				}
			}
		}
	}

	@Override
	public void moduleBlocked(IFController controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleUnblocked(IFController controller) {
		// TODO Auto-generated method stub
		
	}
}
