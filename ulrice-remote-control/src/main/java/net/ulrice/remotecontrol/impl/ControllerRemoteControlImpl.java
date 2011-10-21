package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ComponentInteraction.*;
import static net.ulrice.remotecontrol.ComponentMatcher.*;
import static net.ulrice.remotecontrol.ControllerMatcher.*;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.JButton;
import javax.swing.JDialog;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.impl.IFCloseHandler;
import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.ComponentRemoteControl;
import net.ulrice.remotecontrol.ControllerMatcher;
import net.ulrice.remotecontrol.ControllerRemoteControl;
import net.ulrice.remotecontrol.ControllerState;
import net.ulrice.remotecontrol.RemoteControlCenter;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.Result;

public class ControllerRemoteControlImpl implements ControllerRemoteControl {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ControllerRemoteControl#ping()
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ControllerRemoteControl#statesOf(net.ulrice.remotecontrol.ControllerMatcher[])
     */
    @Override
    public Collection<ControllerState> statesOf(ControllerMatcher... matchers) throws RemoteControlException {
        LinkedHashSet<IFController> controllers =
                new LinkedHashSet<IFController>(Ulrice.getModuleManager().getActiveControllers());

        // TODO: WTF, why are null controllers in the list?
        controllers.remove(null);

        return ControllerState.inspect(and(matchers).match(controllers));
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ControllerRemoteControl#stateOf(net.ulrice.remotecontrol.ControllerMatcher[])
     */
    @Override
    public ControllerState stateOf(ControllerMatcher... matchers) throws RemoteControlException {
        Collection<ControllerState> states = statesOf(matchers);

        if (states.isEmpty()) {
            return null;
        }

        if (states.size() > 1) {
            throw new RemoteControlException("Multiple controlers match: " + and(matchers));
        }

        return states.iterator().next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ControllerRemoteControl#contains(net.ulrice.remotecontrol.ControllerMatcher[])
     */
    @Override
    public boolean contains(ControllerMatcher... matchers) throws RemoteControlException {
        return statesOf(matchers).size() > 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ControllerRemoteControl#close(net.ulrice.remotecontrol.ControllerMatcher[])
     */
    @Override
    public boolean close(ControllerMatcher... matchers) throws RemoteControlException {
        Collection<ControllerState> states = statesOf(matchers);

        if (states.isEmpty()) {
            return true;
        }

        boolean success = true;

        for (final ControllerState state : states) {
            final Result<Boolean> result = new Result<Boolean>(2);

            ComponentUtils.invokeInSwing(new Runnable() {

                @Override
                public void run() {
                    try {
                        Ulrice.getModuleManager().closeController(state.getController(), new IFCloseHandler() {

                            @Override
                            public void closeSuccess() {
                                result.fireResult(true);
                            }

                            @Override
                            public void closeFailure() {
                                result.fireResult(false);
                            }
                        });
                    }
                    catch (Exception e) {
                        e.printStackTrace(System.err);
                        result.fireException(e);
                    }
                }
            });

            ComponentRemoteControl componentRC = RemoteControlCenter.get(ComponentRemoteControl.class);

            while (!result.testResult(0.25)) {
                componentRC.interact(click(), ComponentMatcher.like(".*No"), ofType(JButton.class),
                    within(ofType(JDialog.class)));
                componentRC.interact(click(), ComponentMatcher.like(".*Close"), ofType(JButton.class),
                    within(ofType(JDialog.class)));
                componentRC.interact(click(), ComponentMatcher.like(".*Cancel"), ofType(JButton.class),
                    within(ofType(JDialog.class)));
            }

            success &= result.aquireResult();
        }

        return success;
    }

}
