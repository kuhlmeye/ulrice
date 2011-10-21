package net.ulrice.remotecontrol.util;

import net.ulrice.remotecontrol.RemoteControlException;

public class Result<RESULT_TYPE> {

    private final Object semaphore = new Object();

    private final long startMillis;
    private final long timeout;
    private final long endMillis;

    private boolean gotResult = false;
    private RESULT_TYPE result;
    private Exception exception;

    public Result(double timeoutInSeconds) {
        super();

        startMillis = System.currentTimeMillis();
        timeout = (long) (timeoutInSeconds * 1000);
        endMillis = startMillis + timeout;
    }

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

    public void fireException(Exception exception) {
        synchronized (semaphore) {
            this.exception = exception;

            gotResult = true;

            semaphore.notifyAll();
        }
    }

    public void fireResult(RESULT_TYPE result) {
        synchronized (semaphore) {
            this.result = result;

            gotResult = true;

            semaphore.notifyAll();
        }
    }

}
