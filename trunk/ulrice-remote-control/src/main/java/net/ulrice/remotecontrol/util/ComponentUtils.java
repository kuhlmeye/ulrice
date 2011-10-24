package net.ulrice.remotecontrol.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.RemoteControlException;

/**
 * Utility methods for components
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentUtils {

    /**
     * Collects all children of the specified components and matches them against the specified matcher
     * 
     * @param matcher the matcher
     * @param components the components
     * @return the result of the match operation
     * @throws RemoteControlException on occasion
     */
    public static Collection<Component> find(ComponentMatcher matcher, Component... components)
        throws RemoteControlException {
        Collection<Component> results = collect(components);

        return matcher.match(results);
    }

    /**
     * Collects all children of the specified components
     * 
     * @param components the components
     * @return a collection of all components and their children
     */
    public static Collection<Component> collect(Component... components) {
        return collect(new LinkedHashSet<Component>(), components);
    }

    /**
     * Collects all children of the specified components
     * 
     * @param results adds all children to these results
     * @param components the components
     * @return the results collection
     */
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

    /**
     * Collects all parents of the specified component.
     * 
     * @param component the component
     * @return a collection of all parents
     */
    public static Collection<Component> parents(Component component) {
        Collection<Component> results = new LinkedHashSet<Component>();

        Container parent = component.getParent();

        while (parent != null) {
            results.add(parent);
            parent = parent.getParent();
        }

        return results;
    }

    /**
     * Tries to bring the specified component to the front
     * 
     * @param component the component
     * @return true if successful
     * @throws RemoteControlException on occasion
     */
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

    /**
     * Tries to focus the component
     * 
     * @param component the component
     * @return true if successful
     * @throws RemoteControlException on occasion
     */
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

    /**
     * Tries to move the specified rectangle of the specified component to the visible part of the viewport of the
     * parents
     * 
     * @param component the component
     * @param rectangle the rectangle
     */
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

}
