package net.ulrice.remotecontrol;

import java.util.Collection;

import net.ulrice.module.impl.ModuleManager;

/**
 * Remote control for Ulrices {@link ModuleManager}
 * 
 * @author Manfred HANTSCHEL
 */
public interface ControllerRemoteControl {

    boolean ping();

    /**
     * Returns the states of all matching controllers.
     * 
     * @param matchers the matchers, concatenated by and
     * @return a collection of controller states, empty if none found
     * @throws RemoteControlException on occasion
     */
    Collection<ControllerState> statesOf(ControllerMatcher... matchers) throws RemoteControlException;

    /**
     * Returns the state of the matching controllers. Null if no controller matches.
     * 
     * @param matchers the matchers, concatenated by and
     * @return the matching controller
     * @throws RemoteControlException if more than one controller matches
     */
    ControllerState stateOf(ControllerMatcher... matchers) throws RemoteControlException;

    /**
     * Returns true if there is a controller that matches all the matchers
     * 
     * @param matchers the matchers, concatenated by and
     * @return true if available
     * @throws RemoteControlException on occasion
     */
    boolean contains(ControllerMatcher... matchers) throws RemoteControlException;

    /**
     * Closes the specified controller. Tries to close all opened dialogs as well.
     * 
     * @param matchers the matchers, concatenated by and
     * @return true if successful
     * @throws RemoteControlException on occasion
     */
    boolean close(ControllerMatcher... matchers) throws RemoteControlException;
}
