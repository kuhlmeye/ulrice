package net.ulrice.remotecontrol;

import java.util.Collection;

/**
 * Remote control for all AWT and Swing components
 * 
 * @author Manfred HANTSCHEL
 */
public interface ComponentRemoteControl {

    boolean ping();

    /**
     * Returns the state of the component specified by the matcher
     * 
     * @param matchers one or more matchers, concatenated by and
     * @return the state of the component
     * @throws RemoteControlException on occasion
     */
    ComponentState stateOf(ComponentMatcher... matchers) throws RemoteControlException;

    /**
     * Returns a collection of the states of all components that correlate the matcher
     * 
     * @param matchers one or more matchers, concatenated by and
     * @return a collection of states, never null
     * @throws RemoteControlException on occasion
     */
    Collection<ComponentState> statesOf(ComponentMatcher... matchers) throws RemoteControlException;

    /**
     * Returns true if there exists at least one component that correlates the matcher
     * 
     * @param matchers one or more matchers, concatenated by and
     * @return true, if at least one component matches
     * @throws RemoteControlException
     */
    boolean contains(ComponentMatcher... matchers) throws RemoteControlException;

    /**
     * Performs the specified interaction on all components that correlate the matcher
     * 
     * @param interaction the interaction
     * @param matchers one or more matchers, concatenated by and
     * @return the result of the interaction, false if no interaction did occur
     * @throws RemoteControlException on occasion
     */
    boolean interact(ComponentInteraction interaction, ComponentMatcher... matchers) throws RemoteControlException;

    /**
     * Waits for the specified amount of seconds for at least one component to correlate the matcher
     * 
     * @param seconds the seconds
     * @param matchers one or more matchers, concatenated by and
     * @return a collection of states, never null
     * @throws RemoteControlException if no component was found or on other errors
     */
    Collection<ComponentState> waitForAll(double seconds, ComponentMatcher... matchers) throws RemoteControlException;

    /**
     * Waits for the specified amount of seconds for one component to correlate the matcher
     * 
     * @param seconds the seconds
     * @param matchers one or more matchers, concatenated by and
     * @return the component, never null
     * @throws RemoteControlException if no component was found or on other errors
     */
    ComponentState waitFor(double seconds, ComponentMatcher... matchers) throws RemoteControlException;

    /**
     * Waits for the specified amount of seconds for no component to correlate the matcher
     * 
     * @param seconds the seconds
     * @param matchers one or more matchers, concatenated by and
     * @throws RemoteControlException on a timeout
     */
    void waitForNone(double seconds, ComponentMatcher... matchers) throws RemoteControlException;

}
