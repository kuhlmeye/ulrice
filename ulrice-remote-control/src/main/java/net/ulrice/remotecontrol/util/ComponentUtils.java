package net.ulrice.remotecontrol.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.ui.accordionpanel.AccordionContentPanel;

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
    public static Collection<Component> find(ComponentMatcher matcher, Component... components) throws RemoteControlException {
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
            if ((component != null) && (component.isVisible())) {
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

            if (component instanceof Window) {
                // if ((component instanceof Window) && (!(component instanceof Dialog)) && (component.isVisible())) {
                final Window window = (Window) component;

                if (!window.isActive()) {
                    final Result<Boolean> result = new Result<Boolean>(4);

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
                    catch (RemoteControlException e) {
                        throw new RemoteControlException("Failed to bring component to front", e);
                    }
                    finally {
                        window.removeWindowFocusListener(focusListener);
                        window.removeWindowListener(windowListener);
                    }
                }
                else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            window.toFront();
                            window.requestFocus();
                        }
                    });
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

        final Result<Boolean> result = new Result<Boolean>(4);

        FocusAdapter focusListener = new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                result.fireResult(true);
            }

        };

        component.addFocusListener(focusListener);

        try {
            component.requestFocusInWindow();

            try {
                return result.aquireResult();
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Setting focus to component failed", e);
            }
        }
        finally {
            component.removeFocusListener(focusListener);
        }
    }

    /**
     * Tries to move the specified point of the specified component to the visible part of the viewport of the
     * parents.
     *
     * @param component the component
     * @param point the point
     * @throws RemoteControlException on occasion
     */
    public static void scrollPointToVisible(Component component, Point point) throws RemoteControlException {
        scrollPointToVisible(component, point, 2);
    }

    /**
     * Tries to move the specified point of the specified component to the visible part of the viewport of the
     * parents. If the size is < 2, it is interpreted as 2.
     *
     * @param component the component
     * @param point the point
     * @param size creates a rectangle out of the point with the specified size as width and height
     * @throws RemoteControlException on occasion
     */
    public static void scrollPointToVisible(Component component, Point point, int size) throws RemoteControlException {
        if (size < 2) {
            size = 2;
        }

        scrollRectToVisible(component, new Rectangle(point.x - (size / 2), point.y - (size / 2), size, size));
    }

    /**
     * Tries to move the specified rectangle of the specified component to the visible part of the viewport of the
     * parents.
     *
     * @param component the component
     * @param rectangle the rectangle
     * @throws RemoteControlException on occasion
     */
    public static void scrollRectToVisible(final Component component, final Rectangle rectangle) throws RemoteControlException {

        if (component instanceof JComponent) {
            try {
                RemoteControlUtils.invokeInSwing(new Runnable() {

                    @Override
                    public void run() {
                        ((JComponent) component).scrollRectToVisible(rectangle);

                        component.repaint(0);
                    }
                });
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Failed to scroll component", e);
            }
        }
    }

    public static String getTitle(JPanel panel) {
        if (panel == null) {
            return null;
        }

        if (panel instanceof AccordionContentPanel) {
            return ((AccordionContentPanel) panel).getSeparatorPanel().getTitle();
        }
        
        Border border = panel.getBorder();
        
        if (border == null) {
            return null;
        }
        
        if (border instanceof TitledBorder) {
            return ((TitledBorder) border).getTitle();
        }

        try {
            Method method = border.getClass().getMethod("getTitle");

            return (String) method.invoke(border);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // ignore
        }

        return null;
    }

}
