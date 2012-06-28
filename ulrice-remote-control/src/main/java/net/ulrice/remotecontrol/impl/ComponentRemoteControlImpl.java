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
import net.ulrice.remotecontrol.util.ComponentUtils;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import net.ulrice.remotecontrol.util.ResultClosure;

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
            throw new RemoteControlException("Multiple components match " + and(matchers));
        }

        return states.iterator().next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#statesOf(net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public Collection<ComponentState> statesOf(ComponentMatcher... matchers) throws RemoteControlException {
        return ComponentState.inspect(ComponentUtils.find(and(matchers), Window.getWindows()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(ComponentMatcher... matchers) throws RemoteControlException {
        return statesOf(matchers).size() > 0;
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

        RemoteControlUtils.invokeInThread(new Runnable() {
            @Override
            public void run() {
                Robot robot;
                try {
                    robot = RemoteControlUtils.createRobot();
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

        try {
            return result.aquireResult();
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException("Interaction " + interaction + " failed on "
                + ComponentMatcher.and(matchers), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#waitForAll(double,
     *      net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public Collection<ComponentState> waitForAll(final double seconds, final ComponentMatcher... matchers)
        throws RemoteControlException {

        try {
            return RemoteControlUtils.repeatInThread(seconds, new ResultClosure<Collection<ComponentState>>() {

                @Override
                public void invoke(Result<Collection<ComponentState>> result) throws RemoteControlException {
                    Collection<ComponentState> states = statesOf(matchers);

                    if ((states != null) && (states.size() > 0)) {
                        result.fireResult(states);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to wait %,.1f s for all components: %s", seconds,
                ComponentMatcher.and(matchers)), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#waitFor(double,
     *      net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public ComponentState waitFor(double seconds, ComponentMatcher... matchers) throws RemoteControlException {
        Collection<ComponentState> results = waitForAll(seconds, matchers);

        return results.iterator().next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ComponentRemoteControl#waitForNone(double,
     *      net.ulrice.remotecontrol.ComponentMatcher[])
     */
    @Override
    public void waitForNone(double seconds, final ComponentMatcher... matchers) throws RemoteControlException {
        try {
            RemoteControlUtils.repeatInThread(seconds, new ResultClosure<Collection<ComponentState>>() {

                @Override
                public void invoke(Result<Collection<ComponentState>> result) throws RemoteControlException {
                    Collection<ComponentState> states = statesOf(matchers);

                    if ((states == null) || (states.size() == 0)) {
                        result.fireResult(null);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to wait %,.1f s for no component to match: %s",
                seconds, ComponentMatcher.and(matchers)), e);
        }
    }

}
