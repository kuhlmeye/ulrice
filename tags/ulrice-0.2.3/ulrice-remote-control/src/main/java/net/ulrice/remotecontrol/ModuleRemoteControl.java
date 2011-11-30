package net.ulrice.remotecontrol;

import java.util.Collection;

import net.ulrice.module.impl.ModuleManager;

/**
 * Remote control for Ulrices {@link ModuleManager}.
 * 
 * @author Manfred HANTSCHEL
 */
public interface ModuleRemoteControl {

    boolean ping();

    /**
     * Returns the states of all modules, that adhere the specified matchers.
     * 
     * @param matchers the matchers, concatenated by and
     * @return a collection of module states, empty if none
     * @throws RemoteControlException on occasion
     */
    Collection<ModuleState> statesOf(ModuleMatcher... matchers) throws RemoteControlException;

    /**
     * Returns the state of the module, that adheres the specified matchers. Null if none.
     * 
     * @param matchers the matchers, concatenated by and
     * @return the state or null
     * @throws RemoteControlException if more than one modules fit the matchers
     */
    ModuleState stateOf(ModuleMatcher... matchers) throws RemoteControlException;

    /**
     * Returns true if there is at least one module that fits the matchers
     * 
     * @param matchers the matchers, concatenated by and
     * @return true if at least one module matches
     * @throws RemoteControlException on occasion
     */
    boolean contains(ModuleMatcher... matchers) throws RemoteControlException;

    /**
     * Opens all modules that fit the matchers
     * 
     * @param matchers the matchers, concatenated by and
     * @return true if at least one module was opened
     * @throws RemoteControlException on occasion
     */
    boolean open(ModuleMatcher... matchers) throws RemoteControlException;

}
