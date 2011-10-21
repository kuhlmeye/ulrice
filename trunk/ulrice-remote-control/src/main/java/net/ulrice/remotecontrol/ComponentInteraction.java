package net.ulrice.remotecontrol;

import java.awt.Component;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.List;

import net.ulrice.remotecontrol.impl.ComponentUtils;
import net.ulrice.remotecontrol.impl.helper.ComponentHelperRegistry;
import net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardDE;
import net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardInstruction;
import ognl.Ognl;
import ognl.OgnlException;

public abstract class ComponentInteraction implements Serializable {

    private static final long serialVersionUID = 6700185186190253028L;

    /**
     * Performs all interaction is sequential order.
     * 
     * @param interactions the interactions
     * @return the interaction
     */
    public static ComponentInteraction and(final ComponentInteraction... interactions) {
        if (interactions.length == 1) {
            return interactions[0];
        }

        double duration = 0;

        for (ComponentInteraction interaction : interactions) {
            duration += interaction.duration();
        }

        final double totalDuration = duration;

        return new ComponentInteraction() {

            private static final long serialVersionUID = 1497063868246399943L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                boolean result = true;

                for (ComponentInteraction interaction : interactions) {
                    result &= interaction.interact(component, robot);
                }

                return result;
            }

            @Override
            public double duration() {
                return totalDuration;
            }

        };
    }

    /**
     * Pauses for the specified amount of seconds.
     * 
     * @param seconds the seconds, > 0
     * @return the interaction
     */
    public static ComponentInteraction pause(final double seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Seconds <= 0");
        }

        return new ComponentInteraction() {

            private static final long serialVersionUID = -6409854587609629448L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                ComponentUtils.pause(seconds);

                return true;
            }

            @Override
            public double duration() {
                return seconds;
            }

        };
    }

    /**
     * Performs a click on all matched components.
     * 
     * @return the interaction
     */
    public static ComponentInteraction click() {
        return new ComponentInteraction() {

            private static final long serialVersionUID = -8388243900676643609L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).click(robot, component);
            }
        };
    }

    /**
     * Performs a click on the specified index of all matched components. Does nothing if the component does not
     * support an index clicks.
     * 
     * @param index the index, if negative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction click(final int index) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 2982903238859007582L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).click(robot, component, index);
            }
        };
    }

    /**
     * Performs a click on the specified text of all matched components. Does nothing if the component does not
     * support text clicks.
     * 
     * @param text the text
     * @return the interaction
     */
    public static ComponentInteraction click(final String text) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 8882630273706252509L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).click(robot, component, text);
            }
        };
    }

    /**
     * Performs a click on the specified row and column of all matched components. Does nothing if the component does
     * not support clicks on row and column.
     * 
     * @param row the row, if negative, counts from the last upwards
     * @param column the column, if negative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction click(final int row, final int column) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = -8336830884776404724L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).click(robot, component, row, column);
            }
        };
    }

    /**
     * Performs a double click on all matched components.
     * 
     * @return the interaction
     */
    public static ComponentInteraction doubleClick() {
        return and(click(), pause(0.05), click());
    }

    /**
     * Performs a double click on the specified index of all matched components. Does nothing if the component does
     * not support an index clicks.
     * 
     * @param index the index, if nagative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction doubleClick(final int index) {
        return and(click(index), pause(0.05), click(index));
    }

    /**
     * Performs a double click on the specified text of all matched components. Does nothing if the component does not
     * support text clicks.
     * 
     * @param text the text
     * @return the interaction
     */
    public static ComponentInteraction doubleClick(final String text) {
        return and(click(text), pause(0.05), click(text));
    }

    /**
     * Performs a double click on the specified row and column of all matched components. Does nothing if the
     * component does not support clicks on row and column.
     * 
     * @param row the row, if negative, counts from the last upwards
     * @param column the column, if negative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction doubleClick(final int row, final int column) {
        return and(click(row, column), pause(0.05), click(row, column));
    }

    /**
     * Grabs the focus in sequential order for all matched components.
     * 
     * @return the interaction
     */
    public static ComponentInteraction focus() {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 4476987761023418379L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).focus(robot, component);
            }

        };
    }

    /**
     * Performs a select all on all matched components. Does nothing if the component does not support select all.
     * 
     * @return the interaction
     */
    public static ComponentInteraction selectAll() {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 4476987761023418379L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).selectAll(robot, component);
            }

        };
    }

    /**
     * Enters the specified text into all matched components. Usually it tries to grab the focus of the component,
     * calls select all and types the text, but the interaction may differ per component.
     * 
     * @param text the text
     * @return the interaction
     */
    public static ComponentInteraction enter(final String text) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 8438141384776456113L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).enter(robot, component, text);
            }

        };
    }

    /**
     * Enters the specified text into all matched components. Usually it tries to click the component and types the
     * text, but the interaction may differ per component.
     * 
     * @param text the text
     * @param index the index, if negative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction enter(final String text, final int index) {
        return new ComponentInteraction() {

            /**
             * 
             */
            private static final long serialVersionUID = -8335783163131700780L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).enter(robot, component, text, index);
            }

        };
    }

    /**
     * Enters the specified text into all matched components. Usually it tries to double click the component and types
     * the text, but the interaction may differ per component.
     * 
     * @param text the text
     * @param row the row, if negative, counts from the last upwards
     * @param column the column, if negative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction enter(final String text, final int row, final int column) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 9092174601450290529L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                return ComponentHelperRegistry.get(component.getClass()).enter(robot, component, text, row, column);
            }

        };
    }

    /**
     * Types the specified text. Unlike the enter commands, the typing is not component related. The text supports the
     * following format:
     * 
     * <pre>
     * text       = { CHARACTER | "{" command "}" }.
     * command    = ( "pause " seconds ) | keyCommand.
     * keyCommand = { "shift " | "control " | "ctrl " | "alt graph " | "alt gr " | "altgr " | "alt " | "meta " | "windows " | "win " } 
     *              ( "key 0x" hexCode | CODE | CHARACTER ).
     * </pre>
     * 
     * @param text the text
     * @return the interaction
     */
    public static ComponentInteraction type(final String text) {
        final List<RemoteKeyboardInstruction> instructions = RemoteKeyboardDE.INSTANCE.parse(text);
        double duration = 0;

        for (RemoteKeyboardInstruction instruction : instructions) {
            duration += instruction.duration();
        }

        final double totalDuration = duration;

        return new ComponentInteraction() {

            private static final long serialVersionUID = 4476987761023418379L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                for (RemoteKeyboardInstruction instruction : instructions) {
                    instruction.execute(robot);
                }

                return true;
            }

            @Override
            public double duration() {
                return totalDuration;
            }

        };
    }

    /**
     * Holds the shift key while performing the specified interactions.
     * 
     * @param interactions the interactions
     * @return the interaction
     */
    public static ComponentInteraction holdShift(ComponentInteraction... interactions) {
        return holdKey(KeyEvent.VK_SHIFT, interactions);
    }

    /**
     * Holds the control key while performing the specified interactions.
     * 
     * @param interactions the interactions
     * @return the interaction
     */
    public static ComponentInteraction holdControl(ComponentInteraction... interactions) {
        return holdKey(KeyEvent.VK_CONTROL, interactions);
    }

    /**
     * Holds the alt key while performing the specified interactions.
     * 
     * @param interactions the interactions
     * @return the interaction
     */
    public static ComponentInteraction holdAlt(ComponentInteraction... interactions) {
        return holdKey(KeyEvent.VK_ALT, interactions);
    }

    private static ComponentInteraction holdKey(final int keyCode, ComponentInteraction... interactions) {
        final ComponentInteraction interaction = and(interactions);

        return new ComponentInteraction() {

            private static final long serialVersionUID = 5497131283292718042L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                robot.keyPress(keyCode);
                try {
                    return interaction.interact(component, robot);
                }
                finally {
                    robot.keyRelease(keyCode);
                }
            }
        };
    }

    /**
     * Uses OGNL to invoke a method on the component
     * 
     * @param expression the expression
     * @return true if invoked
     */
    public static ComponentInteraction invoke(final String expression) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 283760730002536778L;

            /**
             * {@inheritDoc}
             * 
             * @see net.ulrice.remotecontrol.ComponentInteraction#interact(java.awt.Component, java.awt.Robot)
             */
            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                try {
                    Ognl.getValue(expression, component);
                }
                catch (OgnlException e) {
                    throw new RemoteControlException("Invocation failed: " + expression, e);
                }

                return true;
            }
        };
    }

    protected ComponentInteraction() {
        super();
    }

    public abstract boolean interact(Component component, Robot robot) throws RemoteControlException;

    public double duration() {
        return 0.1;
    }

}
