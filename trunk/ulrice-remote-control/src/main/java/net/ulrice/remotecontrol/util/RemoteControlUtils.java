package net.ulrice.remotecontrol.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import net.ulrice.remotecontrol.RemoteControlException;

/**
 * General utilities for the remote control feature
 * 
 * @author Manfred HANTSCHEL
 */
public class RemoteControlUtils {

    public static final String SPEED_FACTOR_PROPERTY = "RCSpeedFactor";
    public static final String DISABLE_TIMEOUTS_PROPERTY = "RCDisableTimeouts";
    public static final String DISABLE_CONTROLLER_PROPERTY = "RCDisableController";
    public static final String PAUSE_ON_ERROR_PROPERTY = "RCPauseOnError";

    public static final double ROBOT_DELAY = 0.001;

    public static final double PAUSE_DELAY = 0.1;

    public static final double WAIT_DELAY = 0.1;

    private static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    private static Double speedFactor;

    /**
     * Creates a pattern from the specified regular expression
     * 
     * @param regex the regular expression
     * @return the matcher
     */
    public static RegularMatcher toMatcher(String regex) {
        return new RegularMatcher(regex);
    }

    /**
     * Creates a collection from the specified values
     * 
     * @param values the values
     * @return the collection
     */
    public static <TYPE> Collection<TYPE> toCollection(TYPE... values) {
        if (values == null) {
            return null;
        }

        return Arrays.asList(values);
    }

    /**
     * Starts a new thread that executes the runnable. Currently uses the EXECUTOR_SERVICE, but this may change in
     * future.
     * 
     * @param runnable the runnable
     */
    public static void invokeAsync(Runnable runnable) {
        invokeInThread(runnable);
    }

    /**
     * Starts a new thread that executes the runnable
     * 
     * @param runnable the runnable
     */
    public static void invokeInThread(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

    /**
     * Constantly invokes the specified closure until the closure fires a result or the timeout is reached. Block the
     * current thread for a result.
     * 
     * @param timeoutInSeconds the timeout the timeout
     * @param closure the closure
     */
    public static <TYPE> TYPE repeatInThread(final double timeoutInSeconds, final ResultClosure<TYPE> closure)
        throws RemoteControlException {

        final Result<TYPE> result = new Result<TYPE>(timeoutInSeconds);

        invokeInThread(new Runnable() {

            @Override
            public void run() {
                long waitDelay = RemoteControlUtils.getWaitDelay();

                while (true) {
                    try {
                        closure.invoke(result);
                    }
                    catch (Exception e) {
                        result.fireException(e);
                        return;
                    }

                    if (result.isFinished()) {
                        return;
                    }

                    try {
                        Thread.sleep(waitDelay);
                    }
                    catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        });

        try {
            return result.aquireResult();
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException("Failed to repeat closure in thread", e);
        }
    }

    /**
     * Calls the invoke and wait method of the {@link SwingUtilities} and waits for it to finish.
     * 
     * @param runnable the runnable
     * @throws RemoteControlException on occasion
     */
    public static void invokeInSwing(final Runnable runnable) throws RemoteControlException {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            try {
                final Result<Boolean> result = new Result<Boolean>(10);

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            runnable.run();

                            result.fireResult(Boolean.TRUE);
                        }
                        catch (Exception e) {
                            result.fireException(e);
                        }
                    }

                });

                result.aquireResult();
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Failed to invoke in swing", e);
            }
        }
    }

    /**
     * Returns the default robot delay multiplied by the speed factor
     * 
     * @return the default robot delay
     */
    public static double getRobotDelay() {
        double delay = ROBOT_DELAY * speedFactor();

        if (delay > 0.25) {
            delay = 0.25;
        }

        return delay;
    }

    /**
     * Creates a robot
     * 
     * @return the robot
     * @throws RemoteControlException on occasion
     */
    public static Robot createRobot() throws RemoteControlException {
        Robot robot;

        try {
            robot = new Robot();

            robot.setAutoDelay((int) (1000 * getRobotDelay()));
            robot.setAutoWaitForIdle(true);
        }
        catch (AWTException e) {
            throw new RemoteControlException("Could not initialize robot", e);
        }

        return robot;
    }

    /**
     * Returns the speed multiplier for interactions. The default value is 1. The value can be modified by using a
     * system property:
     * <ul>
     * <li>-DRCSpeedFactor=0.1 for ten times the speed</li>
     * <li>-DRCSpeedFactor=10 for a tenth the speed</li>
     * </ul>
     * 
     * @return the default speed multiplier
     */
    public static double speedFactor() {
        if (speedFactor == null) {
            speedFactor = Double.valueOf(1);

            String s = System.getProperty(SPEED_FACTOR_PROPERTY);

            if (s != null) {
                try {
                    speedFactor = Double.valueOf(s);
                }
                catch (NumberFormatException e) {
                    System.err.println("Failed to parse the " + SPEED_FACTOR_PROPERTY + " property: "
                        + e.getMessage());
                }
            }
        }

        return speedFactor.doubleValue();
    }

    /**
     * Overrides the speed factor settings
     * 
     * @param speedFactor the speed factor
     */
    public static void overrideSpeedFactor(double speedFactor) {
        System.setProperty(SPEED_FACTOR_PROPERTY, String.valueOf(speedFactor));
        RemoteControlUtils.speedFactor = null;
    }

    /**
     * Pauses the current thread for the specified amount of seconds. The value is constant and not multiplied by the
     * speed factor.
     * 
     * @param seconds seconds to sleep
     */
    public static void constantPause(double seconds) {
        if (seconds <= 0) {
            return;
        }

        try {
            Thread.sleep((long) (seconds * 1000));
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Pauses the current thread for the specified amount of seconds. The value is multiplied by the speed settings.
     * 
     * @param seconds seconds to sleep
     */
    public static void pause(double seconds) {
        constantPause(seconds * speedFactor());
    }

    /**
     * Returns the default pause multiplied by the speed factor
     * 
     * @return the pause
     */
    public static double getPauseDelay() {
        return PAUSE_DELAY * speedFactor();
    }

    /**
     * Returns the wait delay for the wait-for operations
     * 
     * @return the delay
     */
    public static long getWaitDelay() {
        long result = (long) (WAIT_DELAY * speedFactor() * 1000);

        if (result < 10) {
            result = 10;
        }

        if (result > 1000) {
            result = 1000;
        }

        return result;
    }

    /**
     * Pauses the current thread for a short period.
     */
    public static void pause() {
        pause(PAUSE_DELAY);
    }

    /**
     * Returns true if timeouts are enabled
     * 
     * @return true if enabled
     */
    public static boolean isTimeoutEnabled() {
        return System.getProperty(DISABLE_TIMEOUTS_PROPERTY) == null;
    }

}
