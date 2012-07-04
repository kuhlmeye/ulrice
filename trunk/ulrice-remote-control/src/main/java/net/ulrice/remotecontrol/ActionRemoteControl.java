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
     * Waits for the specified amount of seconds for at least one action to correlate the matcher
     * 
     * @param seconds the seconds
     * @param matchers one or more matchers, concatenated by and
     * @return a collection of states, never null
     * @throws RemoteControlException if no action was found or on other errors
     */
    Collection<ActionState> waitForAll(double seconds, ActionMatcher... matchers)
        throws RemoteControlException;

    /**
     * Waits for the specified amount of seconds for one action to correlate the matcher
     * 
     * @param seconds the seconds
     * @param matchers one or more matchers, concatenated by and
     * @return the action, never null
     * @throws RemoteControlException if no action was found or on other errors
     */
    ActionState waitFor(double seconds, ActionMatcher... matchers) throws RemoteControlException;

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

    /**
     * Executes the action asynchronously (does not wait for result). This is necessary, e.g, 
     * if the action opens a modal dialog.
     * 
     * @param matchers matchers one or more matchers, concatenated by and
     * @return true if executed
     * @throws RemoteControlException on occasion
     */
    boolean asyncAction(ActionMatcher... matchers) throws RemoteControlException;

}
