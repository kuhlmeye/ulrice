package net.ulrice.remotecontrol;

import java.awt.Component;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import net.ulrice.remotecontrol.impl.helper.ComponentHelperRegistry;
import net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardDE;
import net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardInstruction;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import ognl.Ognl;

/**
 * Interactions class for the {@link ComponentRemoteControl}. Specified user interactions with the application.
 *
 * @author Manfred HANTSCHEL
 */
public abstract class ComponentInteraction implements Serializable {

    private static final long serialVersionUID = 6700185186190253028L;

    /**
     * Performs all interaction in sequential order.
     *
     * @param interactions the interactions
     * @return the interaction
     */
    public static ComponentInteraction sequence(final ComponentInteraction... interactions) {
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

            @Override
            public String toString() {
                return "sequence" + Arrays.toString(interactions);
            }

        };
    }

    /**
     * Performs the interaction in sequential order for the number of times.
     *
     * @param times the number of times to repeat the interactions
     * @param interaction the interaction
     * @return the interaction
     */
    public static ComponentInteraction repeat(final int times, final ComponentInteraction interaction) {

        return new ComponentInteraction() {

            private static final long serialVersionUID = 1129901624193430542L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                for (int i = 0; i < times; i += 1) {
                    if (!interaction.interact(component, robot)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public double duration() {
                return interaction.duration() * times;
            }

            @Override
            public String toString() {
                return String.format("repeat[%d times %s]", times, interaction.toString());
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
                RemoteControlUtils.pause(seconds);

                return true;
            }

            @Override
            public double duration() {
                return seconds * RemoteControlUtils.speedFactor();
            }

            @Override
            public String toString() {
                return String.format("pause[%,.1fs]", seconds);
            }

        };
    }

    /**
     * Pauses for the specified amount of seconds (not influenced by the speed factor).
     *
     * @param seconds the seconds, > 0
     * @return the interaction
     */
    public static ComponentInteraction constantPause(final double seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Seconds <= 0");
        }

        return new ComponentInteraction() {

            private static final long serialVersionUID = -6409854587609629448L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                RemoteControlUtils.constantPause(seconds);

                return true;
            }

            @Override
            public double duration() {
                return seconds;
            }

            @Override
            public String toString() {
                return String.format("constantPause[%,.1fs]", seconds);
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).click(robot, component);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return (RemoteControlUtils.getRobotDelay() * 3) + RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return "click";
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).click(robot, component, index);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return (RemoteControlUtils.getRobotDelay() * 3) + RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return String.format("click[%d]", index);
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).click(robot, component, text);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return (RemoteControlUtils.getRobotDelay() * 3) + RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return String.format("click[%s]", text);
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).click(robot, component, row, column);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return (RemoteControlUtils.getRobotDelay() * 3) + RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return String.format("click[%d, %d]", row, column);
            }
        };
    }

    /**
     * Performs a double click on all matched components.
     *
     * @return the interaction
     */
    public static ComponentInteraction doubleClick() {
        return sequence(click(), constantPause(0.05), click());
    }

    /**
     * Performs a double click on the specified index of all matched components. Does nothing if the component does
     * not support an index clicks.
     *
     * @param index the index, if nagative, counts from the last backwards
     * @return the interaction
     */
    public static ComponentInteraction doubleClick(final int index) {
        return sequence(click(index), constantPause(0.05), click(index));
    }

    /**
     * Performs a double click on the specified text of all matched components. Does nothing if the component does not
     * support text clicks.
     *
     * @param text the text
     * @return the interaction
     */
    public static ComponentInteraction doubleClick(final String text) {
        return sequence(click(text), constantPause(0.05), click(text));
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
        return sequence(click(row, column), constantPause(0.05), click(row, column));
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).focus(robot, component);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return "focus";
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).selectAll(robot, component);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return "selectAll";
            }

        };
    }

    public static ComponentInteraction selectNone() {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 4476987761023418379L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                try {
                    return ComponentHelperRegistry.get(component.getClass()).selectNone(robot, component);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return "selectNone";
            }

        };
    }

    /**
     * Performs a select from start to end (incl.).
     *
     * @return the interaction
     */
    public static ComponentInteraction select(final int start, final int end) {
        return new ComponentInteraction() {

            private static final long serialVersionUID = 4476987761023418379L;

            @Override
            public boolean interact(Component component, Robot robot) throws RemoteControlException {
                try {
                    return ComponentHelperRegistry.get(component.getClass()).select(robot, component, start, end);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return "select(" + start + ", " + end + ")";
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).enter(robot, component, text);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay() * 4 /* wild guess */;
            }

            @Override
            public String toString() {
                return String.format("enter[%s]", text);
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).enter(robot, component, text, index);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay() * 4 /* wild guess */;
            }

            @Override
            public String toString() {
                return String.format("enter[%s, %d]", text, index);
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
                try {
                    return ComponentHelperRegistry.get(component.getClass()).enter(robot, component, text, row, column);
                }
                finally {
                    RemoteControlUtils.pause();
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay() * 4 /* wild guess */;
            }

            @Override
            public String toString() {
                return String.format("enter[%s, %d, %d]", text, row, column);
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

        final double totalDuration = duration + RemoteControlUtils.getPauseDelay();

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

            @Override
            public String toString() {
                return String.format("type[%s]", text);
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
        final ComponentInteraction interaction = sequence(interactions);

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

            @Override
            public double duration() {
                return RemoteControlUtils.getRobotDelay() * 2;
            }

            @Override
            public String toString() {
                return String.format("holdKey[%s for %s]", RemoteKeyboardInstruction.getKeyCodeConstant(keyCode), interaction);
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
            public boolean interact(final Component component, Robot robot) throws RemoteControlException {
                final Result<Boolean> result = new Result<Boolean>(10);

                RemoteControlUtils.invokeInSwing(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Ognl.getValue(expression, component);
                        }
                        catch (Exception e) {
                            result.fireException(e);
                        }
                        finally {
                            RemoteControlUtils.pause();
                        }

                        result.fireResult(Boolean.TRUE);
                    }
                });

                try {
                    return result.aquireResult();
                }
                catch (RemoteControlException e) {
                    throw new RemoteControlException("Invocation failed: " + expression, e);
                }
            }

            @Override
            public double duration() {
                return RemoteControlUtils.getPauseDelay();
            }

            @Override
            public String toString() {
                return String.format("invoke[%s]", expression);
            }

        };
    }

    protected ComponentInteraction() {
        super();
    }

    /**
     * Executes the interaction on the specified component using the specified robot
     *
     * @param component the component
     * @param robot the robot
     * @return true for success
     * @throws RemoteControlException on occasion
     */
    public abstract boolean interact(Component component, Robot robot) throws RemoteControlException;

    /**
     * An estimation of the duration
     *
     * @return an estimation of the duration
     */
    public abstract double duration();

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();

}
