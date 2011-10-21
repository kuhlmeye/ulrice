package net.ulrice.remotecontrol;

import java.util.Collection;

public interface ControllerRemoteControl {

    boolean ping();

    Collection<ControllerState> statesOf(ControllerMatcher... matchers) throws RemoteControlException;
    
    ControllerState stateOf(ControllerMatcher... matchers) throws RemoteControlException;
    
    boolean contains(ControllerMatcher... matchers) throws RemoteControlException;

    boolean close(ControllerMatcher... matchers) throws RemoteControlException;
}
