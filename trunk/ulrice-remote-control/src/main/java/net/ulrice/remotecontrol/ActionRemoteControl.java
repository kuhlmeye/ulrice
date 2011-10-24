package net.ulrice.remotecontrol;

import java.util.Collection;

import net.ulrice.module.impl.action.ModuleActionManager;

/**
 * Remote control for Ulrices {@link ModuleActionManager}.
 * 
 * @author Manfred HANTSCHEL
 */
public interface ActionRemoteControl {

    boolean ping();

    /**
     * Lists all actions of the current controller
     * 
     * @param matchers one or more matchers, concatenated by and
     * @return a list of action strings, empty if none
     * @throws RemoteControlException on occasion
     */
    Collection<ActionState> statesOf(ActionMatcher... matchers) throws RemoteControlException;

    /**
     * Returns the state for the action
     * 
     * @param matchers matchers one or more matchers, concatenated by and
     * @return the state, null if none
     * @throws RemoteControlException on occasion
     */
    ActionState stateOf(ActionMatcher... matchers) throws RemoteControlException;

    /**
     * Returns true if there is at least one matching action
     * 
     * @param matchers matchers one or more matchers, concatenated by and
     * @return true if available
     * @throws RemoteControlException on occasion
     */
    boolean contains(ActionMatcher... matchers) throws RemoteControlException;

    /**
     * Executes the action
     * 
     * @param matchers matchers one or more matchers, concatenated by and
     * @return true if executed
     * @throws RemoteControlException on occasion
     */
    boolean action(ActionMatcher... matchers) throws RemoteControlException;

}
