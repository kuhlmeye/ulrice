package net.ulrice.remotecontrol.impl;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.Result;

public class ComponentUtils {

    public static Collection<Component> find(ComponentMatcher matcher, Component... components)
        throws RemoteControlException {
        Collection<Component> results = collect(components);

        return matcher.match(results);
    }

    public static Collection<Component> collect(Component... components) {
        return collect(new LinkedHashSet<Component>(), components);
    }

    public static Collection<Component> collect(Collection<Component> results, Component... components) {
        for (Component component : components) {
            if (component.isVisible()) {
                results.add(component);

                if (component instanceof Container) {
                    collect(results, ((Container) component).getComponents());
                }
            }
        }

        return results;
    }

    public static Collection<Component> parents(Component component) {
        Collection<Component> results = new LinkedHashSet<Component>();

        Container parent = component.getParent();

        while (parent != null) {
            results.add(parent);
            parent = parent.getParent();
        }

        return results;
    }

    public static Pattern toPattern(String regex) throws RemoteControlException {
        try {
            return Pattern.compile(regex);
        }
        catch (PatternSyntaxException e) {
            throw new RemoteControlException("Invalid pattern: " + regex, e);
        }
    }

    public static <TYPE> Collection<TYPE> toCollection(TYPE... values) {
        if (values == null) {
            return null;
        }

        return Arrays.asList(values);
    }

    public static boolean toFront(Component component) throws RemoteControlException {
        while (component != null) {
            if ((component instanceof Window) && (!(component instanceof Dialog)) && (component.isVisible())) {
                final Window window = (Window) component;

                if (!window.isActive()) {
                    System.out.println(window.getOwner());
                    final Result<Boolean> result = new Result<Boolean>(2);

                    WindowFocusListener focusListener = new WindowFocusListener() {
                        @Override
                        public void windowLostFocus(WindowEvent e) {
                            // intentionally left blank
                        }

                        @Override
                        public void windowGainedFocus(WindowEvent e) {
                            result.fireResult(true);
                        }
                    };

                    WindowListener windowListener = new WindowAdapter() {
                        @Override
                        public void windowActivated(WindowEvent e) {
                            result.fireResult(true);
                        }
                    };

                    window.addWindowFocusListener(focusListener);
                    window.addWindowListener(windowListener);

                    try {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                // can you imagine that it is hard to bring a window to the front?
                                window.setVisible(true);

                                if (window instanceof JFrame) {
                                    int state = ((JFrame) window).getExtendedState();
                                    state &= ~Frame.ICONIFIED;
                                    ((JFrame) window).setExtendedState(state);
                                }

                                window.setAlwaysOnTop(true);
                                window.toFront();
                                window.requestFocus();
                                window.setAlwaysOnTop(false);
                            }
                        });

                        return result.aquireResult();
                    }
                    finally {
                        window.removeWindowFocusListener(focusListener);
                        window.removeWindowListener(windowListener);
                    }
                }

                return false;
            }

            component = component.getParent();
        }

        return false;
    }

    public static boolean focus(Component component) throws RemoteControlException {
        toFront(component);

        if (component.hasFocus()) {
            return true;
        }

        final Result<Boolean> result = new Result<Boolean>(2);

        FocusAdapter focusListener = new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                result.fireResult(true);
            }

        };

        component.addFocusListener(focusListener);

        try {
            component.requestFocus();

            return result.aquireResult();
        }
        finally {
            component.removeFocusListener(focusListener);
        }
    }

    public static void scrollRectToVisible(Component component, Rectangle rectangle) {
        Container parent = component.getParent();

        if (parent != null) {
            scrollRectToVisible(parent, SwingUtilities.convertRectangle(component, new Rectangle(rectangle), parent));
        }

        if (component instanceof JComponent) {
            ((JComponent) component).scrollRectToVisible(rectangle);

            component.repaint(0);
        }
    }

    public static void invokeInThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "RemoteControl");

        thread.setDaemon(true);
        thread.start();
    }

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

    public static void pause(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

}
