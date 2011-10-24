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

    /**
     * Creates a pattern from the specified regular expression
     * 
     * @param regex the regular expression
     * @return the pattern
     * @throws RemoteControlException if the regular expression is invalid
     */
    public static Pattern toPattern(String regex) throws RemoteControlException {
        try {
            return Pattern.compile(regex);
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

            robot.setAutoDelay(2);
            robot.setAutoWaitForIdle(true);
        }
        catch (AWTException e) {
            throw new RemoteControlException("Could not initialize robot", e);
        }

        return robot;
    }

    /**
     * Pauses the current thread for the specified amount of seconds
     * 
     * @param seconds seconds to sleep
     */
    public static void pause(double seconds) {
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

}
