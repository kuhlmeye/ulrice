package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ActionMatcher.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.remotecontrol.ActionMatcher;
import net.ulrice.remotecontrol.ActionRemoteControl;
import net.ulrice.remotecontrol.ActionState;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;

/**
 * Implementation of the {@link ActionRemoteControl}
 * 
 * @author Manfred HANTSCHEL
 */
public class ActionRemoteControlImpl implements ActionRemoteControl {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ActionRemoteControl#ping()
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ActionRemoteControl#statesOf(net.ulrice.remotecontrol.ActionMatcher[])
     */
    @Override
    public Collection<ActionState> statesOf(ActionMatcher... matchers) throws RemoteControlException {
        IFController controller = Ulrice.getModuleManager().getCurrentController();

        if (controller == null) {
            return Collections.emptyList();
        }

        return ActionState.inspect(and(matchers).match(
            new LinkedHashSet<ModuleActionState>(controller.getHandledActions())));
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ActionRemoteControl#stateOf(net.ulrice.remotecontrol.ActionMatcher[])
     */
    @Override
    public ActionState stateOf(ActionMatcher... matchers) throws RemoteControlException {
        Collection<ActionState> list = statesOf(matchers);

        if (list.isEmpty()) {
            return null;
        }

        if (list.size() > 1) {
            throw new RemoteControlException("Multiple actions match: " + and(matchers));
        }

        return list.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ActionState> waitForAll(final double seconds, final ActionMatcher... matchers)
        throws RemoteControlException {
        final Result<Collection<ActionState>> result = new Result<Collection<ActionState>>(seconds);
        
        RemoteControlUtils.invokeInThread(new Runnable() {
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

                    Collection<ActionState> states;
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionState waitFor(double seconds, ActionMatcher... matchers) throws RemoteControlException {
        Collection<ActionState> results = waitForAll(seconds, matchers);

        return results.iterator().next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ActionRemoteControl#contains(net.ulrice.remotecontrol.ActionMatcher[])
     */
    @Override
    public boolean contains(ActionMatcher... matchers) throws RemoteControlException {
        return statesOf(matchers).size() > 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ActionRemoteControl#action(net.ulrice.remotecontrol.ActionMatcher[])
     */
    @Override
    public boolean action(ActionMatcher... matchers) throws RemoteControlException {
        IFController controller = Ulrice.getModuleManager().getCurrentController();

        if (controller == null) {
            return false;
        }

        Collection<ModuleActionState> list =
                and(matchers).match(new LinkedHashSet<ModuleActionState>(controller.getHandledActions()));

        if (list.isEmpty()) {
            return false;
        }

        boolean result = true;

        for (final ModuleActionState actionState : list) {
            if (actionState.getAction().isEnabled()) {
                RemoteControlUtils.invokeInSwing(new Runnable() {
                    @Override
                    public void run() {
                        Ulrice.getActionManager().performAction(actionState.getAction(), null);
                        RemoteControlUtils.pause();
                    }
                });

                result &= true;
            }
            else {
                result = false;
            }
        }

        return result;
    }

}
