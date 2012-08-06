package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ActionMatcher.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import javax.swing.SwingUtilities;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.remotecontrol.ActionMatcher;
import net.ulrice.remotecontrol.ActionRemoteControl;
import net.ulrice.remotecontrol.ActionState;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import net.ulrice.remotecontrol.util.ResultClosure;

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

        try {
            return RemoteControlUtils.repeatInThread(seconds, new ResultClosure<Collection<ActionState>>() {

                @Override
                public void invoke(Result<Collection<ActionState>> result) throws RemoteControlException {
                    Collection<ActionState> states = statesOf(matchers);

                    if ((states != null) && (states.size() > 0)) {
                        result.fireResult(states);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to wait %,.1f s for all actions: %s", seconds,
                ActionMatcher.and(matchers)), e);
        }
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
        return action(false, matchers);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ActionRemoteControl#asyncAction(net.ulrice.remotecontrol.ActionMatcher[])
     */
    @Override
    public boolean asyncAction(ActionMatcher... matchers) throws RemoteControlException {
        return action(true, matchers);
    }

    private boolean action(boolean async, final ActionMatcher... matchers) throws RemoteControlException {
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
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Ulrice.getActionManager().performAction(actionState.getAction(), null);
                    }
                };

                if (async) {
                    RemoteControlUtils.invokeAsync(new Runnable() {

                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(runnable);
                        }

                    });
                }
                else {
                    try {
                        RemoteControlUtils.invokeInSwing(runnable);
                    }
                    catch (RemoteControlException e) {
                        throw new RemoteControlException("Failed to invoke action: " + and(matchers), e);
                    }
                }

                result &= true;
            }
            else {
                result = false;
            }

            RemoteControlUtils.pause();
        }

        return result;
    }

}
