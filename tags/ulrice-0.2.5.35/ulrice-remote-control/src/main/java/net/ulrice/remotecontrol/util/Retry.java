package net.ulrice.remotecontrol.util;

import net.ulrice.remotecontrol.RemoteControlException;

public abstract class Retry<T> {

    public abstract T invoke() throws Exception;

    public boolean succeeded(T result) {
        return true;
    }

    public T retry(int numberOfTries) throws RemoteControlException {
        int count = 0;
        T result = null;
        Throwable exception = null;

        while (count < numberOfTries) {
            try {
                result = invoke();

                if (succeeded(result)) {
                    return result;
                }
            }
            catch (Throwable e) {
                exception = e;
            }

            count += 1;

            RemoteControlUtils.constantPause(0.5);
            RemoteControlUtils.pause(2);
        }

        throw new RemoteControlException("Failed after " + numberOfTries + " retries", exception);
    }
}
