package net.ulrice.remotecontrol.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.SwingUtilities;

import net.ulrice.remotecontrol.RemoteControlException;

/**
 * General utilities for the remote control feature
 * 
 * @author Manfred HANTSCHEL
 */
public class RemoteControlUtils {

    public static final String SPEED_FACTOR_PROPERTY = "RCSpeedFactor";

    public static final double ROBOT_DELAY = 0.01;

    public static final double PAUSE_DELAY = 0.1;

    public static final double WAIT_DELAY = 0.1;

    private static Double speedFactor;

    /**
     * Creates a pattern from the specified regular expression
     * 
     * @param regex the regular expression
     * @return the pattern
     * @throws RemoteControlException if the regular expression is invalid
     */
    public static Pattern toPattern(String regex) throws RemoteControlException {
        try {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        catch (PatternSyntaxException e) {
            throw new RemoteControlException("Invalid pattern: " + regex, e);
        }
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
     * Stars a new thread that executes the runnable
     * 
     * @param runnable the runnable
     */
    public static void invokeInThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "RemoteControl");

        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Calls the invoke and wait method of the {@link SwingUtilities}
     * 
     * @param runnable the runnable
     * @throws RemoteControlException on occasion
     */
    public static void invokeInSwing(Runnable runnable) throws RemoteControlException {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            }
            catch (InterruptedException e) {
                throw new RemoteControlException("Invocation interrupted", e);
            }
            catch (InvocationTargetException e) {
                throw new RemoteControlException("Failed to invoke", e);
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
}
