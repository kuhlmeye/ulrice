package net.ulrice.remotecontrol;

import java.util.Collection;

public interface ModuleRemoteControl {

    boolean ping();

    Collection<ModuleState> statesOf(ModuleMatcher... matchers) throws RemoteControlException;

    ModuleState stateOf(ModuleMatcher... matchers) throws RemoteControlException;

    boolean contains(ModuleMatcher... matchers) throws RemoteControlException;

    boolean open(ModuleMatcher... matchers) throws RemoteControlException;

    // boolean close(String regex) throws RemoteControlException;
    //
    // boolean closeAll() throws RemoteControlException;

    // boolean isActive(String regex) throws RemoteControlException;

    // boolean closeAllModules() throws RemoteControlException;

}
