package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ComponentMatcher.*;

import java.awt.Component;
import java.awt.Robot;
import java.awt.Window;
import java.util.Collection;

import net.ulrice.remotecontrol.ComponentInteraction;
import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.ComponentRemoteControl;
import net.ulrice.remotecontrol.ComponentState;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.Result;

public class ComponentRemoteControlImpl implements ComponentRemoteControl {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#ping()
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#stateOf(net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public ComponentState stateOf(ComponentMatcher... matchers) throws RemoteControlException {
        Collection<ComponentState> states = statesOf(matchers);

        if ((states == null) || (states.size() == 0)) {
            return null;
        }

        if (states.size() > 1) {
            throw new RemoteControlException("Multiple components match " + matchers);
        }

        return ComponentRegistry.register(states.iterator().next());
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#statesOf(net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public Collection<ComponentState> statesOf(ComponentMatcher... matchers) throws RemoteControlException {
        return ComponentRegistry.register(ComponentState.inspect(ComponentUtils.find(and(matchers),
            Window.getWindows())));
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#interact(net.ulrice.remotecontrol.ComponentInteraction,
     *      net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public boolean interact(final ComponentInteraction interaction, ComponentMatcher... matchers)
        throws RemoteControlException {
        final Collection<Component> components = ComponentUtils.find(and(matchers), Window.getWindows());

        if (components.size() < 1) {
            return false;
        }

        final Result<Boolean> result = new Result<Boolean>(interaction.duration() + 10);

        ComponentUtils.invokeInThread(new Runnable() {
            @Override
            public void run() {
                Robot robot;
                try {
                    robot = ComponentUtils.createRobot();
                }
                catch (RemoteControlException e) {
                    result.fireException(e);
                    return;
                }

                boolean success = true;

                for (Component component : components) {

                    try {
                        success &= interaction.interact(component, robot);
                    }
                    catch (RemoteControlException e) {
                        result.fireException(e);
                        return;
                    }
                }

                result.fireResult(success);
            }
        });

        return result.aquireResult();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#waitFor(double,
     *      net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public Collection<ComponentState> waitFor(final double seconds, final ComponentMatcher... matchers)
        throws RemoteControlException {
        final Result<Collection<ComponentState>> result = new Result<Collection<ComponentState>>(seconds);

        ComponentUtils.invokeInThread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                long timeToWait = (long) (seconds * 1000);
                long end = start + timeToWait;

                while (timeToWait > 0) {
                    try {
                        Thread.sleep((timeToWait > 250) ? 250 : timeToWait);
                    }
                    catch (InterruptedException e) {
                        // ignore
                    }

                    Collection<ComponentState> states;
                    try {
                        states = statesOf(matchers);
                    }
                    catch (RemoteControlException e) {
                        result.fireException(e);
                        return;
                    }

                    if ((states != null) && (states.size() > 0)) {
                        result.fireResult(states);
                        return;
                    }

                    timeToWait = end - System.currentTimeMillis();
                }
            }
        });

        return result.aquireResult();
    }

}
