package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardDE;
import net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardInstruction;
import net.ulrice.remotecontrol.util.ComponentUtils;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

public abstract class AbstractComponentHelper<TYPE extends Component> implements ComponentHelper<TYPE> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(TYPE component) {
        try {
            return (String) component.getClass().getMethod("getText").invoke(component);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle(TYPE component) {
        try {
            return (String) component.getClass().getMethod("getTitle").invoke(component);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText(TYPE component) {
        if (component instanceof JComponent) {
            return ((JComponent) component).getToolTipText();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getLabelFor(TYPE component) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelected(TYPE component) {
        try {
            return (Boolean) component.getClass().getMethod("isSelected").invoke(component);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive(TYPE component) {
        return component.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getData(TYPE component) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, TYPE component) throws RemoteControlException {
        return click(robot, component, new Rectangle(0, 0, component.getWidth(), component.getHeight()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, TYPE component, int index) throws RemoteControlException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, TYPE component, String text) throws RemoteControlException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, TYPE component, int row, int column) throws RemoteControlException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, TYPE component, final Rectangle rectangle) throws RemoteControlException {
        ComponentUtils.scrollRectToVisible(component, rectangle);

        Point point = new Point(rectangle.x + (rectangle.width / 2), rectangle.y + (rectangle.height / 2));

        return click(robot, component, point);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, TYPE component, final Point location) throws RemoteControlException {
        ComponentUtils.scrollPointToVisible(component, location);

        ComponentUtils.toFront(component);
        SwingUtilities.convertPointToScreen(location, component);

        // Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(location.x, location.y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        // robot.mouseMove(mousePosition.x, mousePosition.y);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, TYPE component, String text) throws RemoteControlException {
        focus(robot, component);
        selectAll(robot, component);
        return type(robot, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, TYPE component, String text, int index) throws RemoteControlException {
        if (click(robot, component, index)) {
            return false;
        }

        return type(robot, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, TYPE component, String text, int row, int column) throws RemoteControlException {
        if (click(robot, component, row, column)) {
            return false;
        }

        RemoteControlUtils.constantPause(0.05);

        if (click(robot, component, row, column)) {
            return false;
        }

        return type(robot, text);
    }

    private boolean type(Robot robot, String text) {
        final List<RemoteKeyboardInstruction> instructions = RemoteKeyboardDE.INSTANCE.parse(text);

        for (RemoteKeyboardInstruction instruction : instructions) {
            instruction.execute(robot);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean focus(Robot robot, TYPE component) throws RemoteControlException {
        return ComponentUtils.focus(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selectAll(Robot robot, TYPE component) {
        try {
            component.getClass().getMethod("selectAll").invoke(component);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    protected int invertValue(int value, int maximum) {
        if (value >= 0) {
            return value;
        }

        return maximum + value;
    }
}
