package net.ulrice.module.impl.action;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.event.IFModuleActionManagerEventListener;
import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.impl.ModuleActionState;

/**
 * This class handles the actions of the application.
 * 
 * @author christof
 */
public class ModuleActionManager implements IFModuleEventListener, PropertyChangeListener, KeyEventDispatcher {

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(ModuleActionManager.class.getName());

	/** The controller that is currently active. */
	private IFController activeController;

	private Map<IFController, List<UlriceAction>> controllerActionOrderMap = new HashMap<IFController, List<UlriceAction>>();
	
	/** The map of the action-states of the modules. */
	private Map<IFController, Map<UlriceAction, ModuleActionState>> controllerActionStateMap = new HashMap<IFController, Map<UlriceAction, ModuleActionState>>();

	/** The standard actions of an application. */
	private Map<String, UlriceAction> applicationActions = new HashMap<String, UlriceAction>();

	/** The list of event listeners. */
	private EventListenerList listenerList = new EventListenerList();
	
    private Map<KeyStroke, UlriceAction> applicationActionHotkeyMap = new HashMap<KeyStroke, UlriceAction>();
    private Map<KeyStroke, UlriceAction> controllerActionHotkeyMap = new HashMap<KeyStroke, UlriceAction>();
        
	
	/**
	 * Creates a new module action manager.
	 */
	public ModuleActionManager() {
		Ulrice.getModuleManager().addModuleEventListener(this);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	
	public void dispose() {
	    Ulrice.getModuleManager().removeModuleEventListener(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
	}
	  
    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {    
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(keyEvent);

        UlriceAction action = null;
        if(applicationActionHotkeyMap.containsKey(ks)) {
            action = applicationActionHotkeyMap.get(ks);
        }
        if(controllerActionHotkeyMap.containsKey(ks)) {
            action = controllerActionHotkeyMap.get(ks);
        }
        
        if (action != null) {
            ActionEvent av =
                new ActionEvent(
                    keyEvent.getSource(),
                    keyEvent.getID(),
                    action.getHotkey().toString(),
                    (keyEvent).getModifiers());
            performAction(action, av);
            return true;
        }
        return false;
    }
        
    public void addHotkey(Map<KeyStroke, UlriceAction> hotkeyMap, UlriceAction action) {
        KeyStroke keyStroke = action.getHotkey();
        
        if (keyStroke != null) {
            hotkeyMap.put(keyStroke, action);
        }
    }

    public void removeHotkey(Map<KeyStroke, UlriceAction> hotkeyMap, UlriceAction action) {
        if (action == null) {
            return;
        }
        
        KeyStroke keyStroke = action.getHotkey();
        if (keyStroke != null) {
            hotkeyMap.remove(keyStroke);
        }
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
	 * Informs all listners that the list of application actions has changed.
	 */
	public void fireModuleActionsChanged() {
		IFModuleActionManagerEventListener[] listeners = listenerList.getListeners(IFModuleActionManagerEventListener.class);
		if (listeners != null) {
			for (IFModuleActionManagerEventListener listener : listeners) {
				listener.moduleActionsChanged();
			}
		}
	}

	/**
	 * Performs an action. In this method the action will be checked against the
	 * controller settings and the delegated to the currently active module.
	 * 
	 * @param action
	 *            The action that should be performed.
	 */
	public void performAction(UlriceAction action, ActionEvent e) {

		if (activeController != null) {

			if (!Ulrice.getSecurityManager().allowExecuteAction(activeController, action)) {
				LOG.info("Action [Id: " + action.getUniqueId() + ", Module: "
						+ Ulrice.getModuleManager().getModule(activeController).getModuleTitle(Usage.Default)
						+ "] will not be executed. Not authorized by ulrice security manager.");
				return;
			}

			Map<UlriceAction, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionStateMap != null) {
				ModuleActionState moduleActionState = actionStateMap.get(action);
				if (moduleActionState != null && moduleActionState.isEnabled()) {
					activeController.performModuleAction(action.getUniqueId());
					return;
				}  
			}
			
			if(action.isEnabled()) {
                action.actionPerformed(e);
                return;
            }
		}
	}

	/**
	 * Adds an action to the application actions.
	 * 
	 * @param moduleAction
	 *            The action.
	 */
	public void addApplicationAction(UlriceAction moduleAction) {		

		if (!Ulrice.getSecurityManager().allowRegisterApplicationAction(moduleAction)) {
			LOG.info("Application-Action [Id: " + moduleAction.getUniqueId() + "] will not be added. Not authorized by ulrice security manager.");
			return;
		}
				
		addHotkey(applicationActionHotkeyMap, moduleAction);

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
	public void removeApplicationAction(UlriceAction moduleAction) {
        removeHotkey(applicationActionHotkeyMap, moduleAction);
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
	public UlriceAction getApplicationAction(String uniqueId) {
		return applicationActions.get(uniqueId);
	}
	

	/**
	 * 
	 */
	public List<UlriceAction> adaptActionStates() {
		if (applicationActions != null) {
			for (UlriceAction moduleAction : applicationActions.values()) {
				if (!ActionType.SystemAction.equals(moduleAction.getType())) {
					moduleAction.setEnabled(moduleAction.isInitiallyEnabled());
				}
			}
		}
		if (activeController != null) {
			Map<UlriceAction, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionStateMap != null) {
				List<UlriceAction> moduleActionList = new ArrayList<UlriceAction>(actionStateMap.size());
				controllerActionHotkeyMap.clear();
				for (ModuleActionState moduleActionState : actionStateMap.values()) {
					UlriceAction action = moduleActionState.getAction();
					
                    addHotkey(controllerActionHotkeyMap, action);
					if (!ActionType.SystemAction.equals(action.getType())) {
					    
						action.setEnabled(moduleActionState.isEnabled());
						if (action instanceof MultiStateAction) {
						    ((MultiStateAction) action).setActionState(moduleActionState.getActionState());
						    ((MultiStateAction) action).updateAction(moduleActionState.getActionState());
						}
					}
				}
				return moduleActionList;
			}
		}
		return new ArrayList<UlriceAction>(0);
	}

	/**
	 * 
	 */
	public List<UlriceAction> getModuleActions() {
		if (activeController != null) {
			List<UlriceAction> actionList = controllerActionOrderMap.get(activeController);
			Map<UlriceAction, ModuleActionState> actionStateMap = controllerActionStateMap.get(activeController);
			if (actionList != null && actionStateMap != null) {
				List<UlriceAction> moduleActionList = new ArrayList<UlriceAction>(actionList.size());
				for (UlriceAction action : actionList) {
					ModuleActionState moduleActionState = actionStateMap.get(action);
					String uniqueId = moduleActionState.getAction().getUniqueId();
					if (!applicationActions.containsKey(uniqueId)) {
						moduleActionList.add(moduleActionState.getAction());
					}
				}
				return moduleActionList;
			}
		}
		return new ArrayList<UlriceAction>(0);
	}

	
    public boolean isActionUsedByModule(String uniqueId) {
        if (activeController != null) {
            List<UlriceAction> actionList = controllerActionOrderMap.get(activeController);
            if (actionList != null) {
                for(UlriceAction action : actionList) {
                    if(uniqueId.equals(action.getUniqueId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
	
	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	@Override
	public void openModule(IFController activeController) {
		this.activeController = activeController;
		
		List<ModuleActionState> moduleActionStates = activeController.getHandledActions();
		List<UlriceAction> actionOrder = new ArrayList<UlriceAction>(moduleActionStates.size());
		Map<UlriceAction, ModuleActionState> actionStateMap = new HashMap<UlriceAction, ModuleActionState>();
		
		for (ModuleActionState moduleActionState : moduleActionStates) {
			UlriceAction moduleAction = moduleActionState.getAction();
			
			if (Ulrice.getSecurityManager().allowRegisterAction(activeController, moduleAction)) {
                actionOrder.add(moduleAction);
                actionStateMap.put(moduleActionState.getAction(), moduleActionState);
			} else {
                LOG.info("Action [Id: " + moduleAction.getUniqueId() + ", Module: "
                        + Ulrice.getModuleManager().getModule(activeController).getModuleTitle(Usage.Default)
                        + "] will not be added. Not authorized by ulrice security manager.");
			}
		}
		controllerActionOrderMap.put(activeController, actionOrder);
		controllerActionStateMap.put(activeController, actionStateMap);

	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
	 */
	@Override
	public void closeController(IFController activeController) {

		this.activeController = null;
		controllerActionStateMap.remove(activeController);
		controllerActionOrderMap.remove(activeController);

		adaptActionStates();
		
		fireApplicationActionsChanged();
		fireModuleActionsChanged();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void activateModule(IFController activeController) {
		this.activeController = activeController;

		adaptActionStates();
		fireApplicationActionsChanged();
		fireModuleActionsChanged();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void deactivateModule(IFController activeController) {
		this.activeController = null;

		adaptActionStates();
		fireApplicationActionsChanged();
		fireModuleActionsChanged();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("enabled".equalsIgnoreCase(evt.getPropertyName())) {
			Object source = evt.getSource();

			if (source instanceof UlriceAction) {
				UlriceAction moduleAction = (UlriceAction) source;
				// TODO What happens, if the controller is null? (module closed in meantime..)
				if (!Ulrice.getSecurityManager().allowEnableAction(activeController, moduleAction)) {
					LOG.info("Action [Id: " + moduleAction.getUniqueId() + ", Module: "
							+ Ulrice.getModuleManager().getModuleTitle(activeController, Usage.Default)
							+ "] will not be enabled. Not authorized by ulrice security manager.");
					return;
				}
			}
		}
	}

	public void setActionState(IFController controller, String actionId, boolean enabled) {
	    
	    final Map<UlriceAction, ModuleActionState> map = controllerActionStateMap.get(controller);
	    
	    if (map == null) {
	        return;
	    }
	    
	    final Collection<ModuleActionState> values = map.values();
	    for(ModuleActionState state : values) {
	        if(state.getAction().getUniqueId().equals(actionId)) {
	            state.setEnabled(enabled);
	            break;
	        }
	    }
	    
	    adaptActionStates();
	    fireApplicationActionsChanged();
	}
	
	public void setActionStates(final IFController controller, final List<UActionState> actionStates) {
        final Map<UlriceAction, ModuleActionState> map = controllerActionStateMap.get(controller);
        if (map == null) {
            return;
        }

        final Set<UlriceAction> keys = map.keySet();
        final Set<String> actionSet = new HashSet<String>();
        
        for (UlriceAction moduleAction : keys) {
            for (UActionState actionState : actionStates) {
            	if(moduleAction.getUniqueId().equals(actionState.getActionId())) {
            		ModuleActionState state = map.get(moduleAction);
            		state.setEnabled(actionState.isEnabled());
            		state.setActionState(actionState.getActionState());
                	actionSet.add(state.getAction().getUniqueId());
            	}
            }
        }
        
        if(!actionSet.isEmpty()) {
        	adaptActionStates();
        	fireApplicationActionsChanged();
        }
	}
	
	@Override
	public void moduleBlocked(IFController controller, Object blocker) {	    
        Map<UlriceAction, ModuleActionState> actionMap = controllerActionStateMap.get(controller);        
        if(actionMap != null) {
	        Collection<ModuleActionState> actions = actionMap.values();
	        if(actions != null) {
	            for(ModuleActionState action : actions) {
	                action.addBlocker(blocker);
	            }
	        }
	
	        adaptActionStates();
	        fireApplicationActionsChanged();
        }
	}

	@Override
	public void moduleUnblocked(IFController controller, Object blocker) {
	    unblockInternal(controller, blocker);
	}
	
	@Override
    public void moduleBlockerRemoved(IFController controller, Object blocker) {
	    unblockInternal(controller, blocker);
    }
	
	private void unblockInternal(IFController controller, Object blocker) {
	    Map<UlriceAction, ModuleActionState> actionMap = controllerActionStateMap.get(controller);
        if(actionMap != null) {
            Collection<ModuleActionState> actions = actionMap.values();
            if(actions != null) {
                for(ModuleActionState action : actions) {
                    action.removeBlocker(blocker);
                }
            }
                
            adaptActionStates();
            fireApplicationActionsChanged();
        }
	}

    public void blockAction(IFController controller, UlriceAction action, Object blocker) {
        Map<UlriceAction, ModuleActionState> actionMap = controllerActionStateMap.get(controller);
        if(actionMap.containsKey(action)) {
            ModuleActionState actionState = actionMap.get(action);
            boolean wasEnabled = actionState.isEnabled();
            actionState.addBlocker(blocker);
            if(actionState.isEnabled() != wasEnabled) {
                adaptActionStates();
                fireApplicationActionsChanged();
            }
        }
    }

    public void unblockAction(IFController controller, UlriceAction action, Object blocker) {
        Map<UlriceAction, ModuleActionState> actionMap = controllerActionStateMap.get(controller);
        if(actionMap != null && actionMap.containsKey(action)) {
            ModuleActionState actionState = actionMap.get(action);
            boolean wasEnabled = actionState.isEnabled();
            actionState.removeBlocker(blocker);
            if(actionState.isEnabled() != wasEnabled) {
                adaptActionStates();
                fireApplicationActionsChanged();
            }
        }
    }
    
    @Override
    public void nameChanged(IFController controller) {
    }
    
    public void initializationFinished() {
    	
    }
}
