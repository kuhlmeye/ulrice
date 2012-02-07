package net.ulrice.remotecontrol.util;

import net.ulrice.remotecontrol.RemoteControlException;

/**
 * General result class for parallel processes.
 * 
 * @author Manfred HANTSCHEL
 * @param <RESULT_TYPE> the type of result
 */
public class Result<RESULT_TYPE> {

    private final Object semaphore = new Object();

    private final long startMillis;
    private final long timeout;
    private final long endMillis;

    private boolean gotResult = false;
    private RESULT_TYPE result;
    private Exception exception;

    /**
     * Creates a new result class with the specified timeout in seconds
     * 
     * @param timeoutInSeconds the timeout in seconds
     */
    public Result(double timeoutInSeconds) {
        super();

        startMillis = System.currentTimeMillis();
        timeout = (long) (timeoutInSeconds * 1000);
        endMillis = startMillis + timeout;
    }

    /**
     * Returns true if there is a result. Waits for the specified amount of seconds to get a result. If a timeout
     * occurs during the wait operation a remote control exception is thrown.
     * 
     * @param waitForSeconds seconds to wait for a result.
     * @return true if there is a result, false otherwise
     * @throws RemoteControlException if a timeout occurs during the wait operation
     */
    public boolean testResult(double waitForSeconds) throws RemoteControlException {
        synchronized (semaphore) {
            if (gotResult) {
                return true;
            }

            long maximumWaitFor = (long) (waitForSeconds * 1000);
            long waitFor = endMillis - System.currentTimeMillis();

            if (waitFor < 1) {
                throw new RemoteControlException(String.format("Action timed out: %,.3f seconds",
                    (double) timeout / 1000));
            }

            if (waitFor > maximumWaitFor) {
                waitFor = maximumWaitFor;
            }

            if (!gotResult) {
                try {
                    semaphore.wait(waitFor);
                }
                catch (InterruptedException e) {
                    throw new RemoteControlException("Action interrupted", e);
                }
            }

            return gotResult;
        }
    }

    /**
     * Blocks and waits for a result. Throws an {@link RemoteControlException} if a timeout occurs during the wait
     * operation.
     * 
     * @return the result
     * @throws RemoteControlException if a timeout occurs
     */
    public RESULT_TYPE aquireResult() throws RemoteControlException {
        synchronized (semaphore) {
            long waitFor = endMillis - System.currentTimeMillis();

            if (waitFor < 1) {
                throw new RemoteControlException(String.format("Action timed out: %,.3f seconds",
                    (double) timeout / 1000));
            }

            if (!gotResult) {
                try {
                    semaphore.wait(waitFor);
                }
                catch (InterruptedException e) {
                    throw new RemoteControlException("Action interrupted", e);
                }
            }

            if (!gotResult) {
                throw new RemoteControlException(String.format("Action timed out: %,.3f seconds",
                    (double) timeout / 1000));
            }

            if (exception != null) {
                throw new RemoteControlException("Unhandled exception", exception);
            }

            return result;
        }
    }

    /**
     * Fires an exception. AquireResult will result in an exception
     * 
     * @param exception the exception
     */
    public void fireException(Exception exception) {
        synchronized (semaphore) {
            this.exception = exception;

            gotResult = true;

            semaphore.notifyAll();
        }
    }

    /**
     * Fires a result. AquireResult will return the result
     * 
     * @param result the result
     */
    public void fireResult(RESULT_TYPE result) {
        synchronized (semaphore) {
            this.result = result;

            gotResult = true;

            semaphore.notifyAll();
        }
    }

}
