package net.ulrice.remotecontrol.util;

import net.ulrice.remotecontrol.RemoteControlException;

public interface ResultClosure<T> {

    public void invoke(Result<T> result) throws RemoteControlException;
    
}
