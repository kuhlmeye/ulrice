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
            if (actionState.isEnabled()) {
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
