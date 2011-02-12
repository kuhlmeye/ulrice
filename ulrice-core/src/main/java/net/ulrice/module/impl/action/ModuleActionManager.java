package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.event.IFModuleActionManagerEventListener;
import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.impl.ModuleActionState;

/**
 * This class handles the actions of the application.
 * 
 * @author christof
 */
public class ModuleActionManager implements IFModuleEventListener {

	/** The controller that is currently active. */
	private IFController activeController;

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
		IFModuleActionManagerEventListener[] listeners = listenerList
				.getListeners(IFModuleActionManagerEventListener.class);
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
			Map<Action, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionStateMap != null) {
				List<Action> moduleActionList = new ArrayList<Action>(actionStateMap.size());
				for (ModuleActionState moduleActionState : actionStateMap.values()) {
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
		Set<ModuleActionState> moduleActionStates = activeController.getModuleActionStates();
		Map<Action, ModuleActionState> actionStateMap = new HashMap<Action, ModuleActionState>();
		if (moduleActionStates != null) {
			for (ModuleActionState moduleActionState : moduleActionStates) {
				actionStateMap.put(moduleActionState.getAction(), moduleActionState);
			}
		}
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
}
