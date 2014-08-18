package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ComponentInteraction.click;
import static net.ulrice.remotecontrol.ComponentMatcher.ofType;
import static net.ulrice.remotecontrol.ComponentMatcher.texted;
import static net.ulrice.remotecontrol.ComponentMatcher.within;
import static net.ulrice.remotecontrol.ControllerMatcher.and;

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
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import net.ulrice.remotecontrol.util.ResultClosure;

/**
 * Implementation of the {@link ControllerRemoteControl}.
 * 
 * @author Manfred HANTSCHEL
 */
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

        return ControllerState.inspectControllers(and(matchers).match(controllers));
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
     */
    @Override
    public Collection<ControllerState> waitForAll(final double seconds, final ControllerMatcher... matchers)
        throws RemoteControlException {

        try {
            return RemoteControlUtils.repeatInThread(seconds, new ResultClosure<Collection<ControllerState>>() {

                @Override
                public void invoke(Result<Collection<ControllerState>> result) throws RemoteControlException {
                    Collection<ControllerState> states = statesOf(matchers);

                    if ((states != null) && (states.size() > 0)) {
                        result.fireResult(states);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to wait %,.1f s for all controllers: %s", seconds,
                ControllerMatcher.and(matchers)), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerState waitFor(double seconds, ControllerMatcher... matchers) throws RemoteControlException {
        Collection<ControllerState> results = waitForAll(seconds, matchers);

        return results.iterator().next();
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
     * @see net.ulrice.remotecontrol.ControllerRemoteControl#focus(net.ulrice.remotecontrol.ControllerMatcher[])
     */
    @Override
    public boolean focus(ControllerMatcher... matchers) throws RemoteControlException {
        Collection<ControllerState> states = statesOf(matchers);

        if (states.isEmpty()) {
            return false;
        }

        boolean success = true;

        for (final ControllerState state : states) {
            final Result<Boolean> result = new Result<Boolean>(2);

            try {
                RemoteControlUtils.invokeInSwing(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Ulrice.getModuleManager().activateModule(state.getController());
                            result.fireResult(true);
                        }
                        catch (Exception e) {
                            e.printStackTrace(System.err);
                            result.fireException(e);
                        }
                    }
                });

                success &= result.aquireResult();
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Focussing conroller failed: " + ControllerMatcher.and(matchers), e);
            }

            RemoteControlUtils.pause();
        }

        return success;
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

            try {
                RemoteControlUtils.invokeInSwing(new Runnable() {

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

                while (!result.testResult(0.25)) {
                    closeDialogs();
                }
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Failed to close the dialogs when closing the controller", e);
            }

            try {
                success &= result.aquireResult();
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Closing of controller failed", e);
            }

            RemoteControlUtils.pause();
        }

        try {
            // handle non modal dialogs
            RemoteControlUtils.repeatInThread(5, new ResultClosure<Boolean>() {

                @Override
                public void invoke(Result<Boolean> result) throws RemoteControlException {
                    if (closeDialogs()) {
                        result.fireResult(Boolean.TRUE);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException("Failed to close all remaining dialogs", e);
        }

        return success;
    }

    private boolean closeDialogs() throws RemoteControlException {
        ComponentRemoteControl componentRC = RemoteControlCenter.get(ComponentRemoteControl.class);

        componentRC.interact(click(), ComponentMatcher.like(".*Yes.*"), ofType(JButton.class),
            within(ofType(JDialog.class), ComponentMatcher.contains(ComponentMatcher.or(texted(".*discard.*"),texted(".*unsaved.*")))));
        componentRC.interact(click(), ComponentMatcher.like(".*No.*"), ofType(JButton.class),
            within(ofType(JDialog.class)));
        componentRC.interact(click(), ComponentMatcher.like(".*Close.*"), ofType(JButton.class),
            within(ofType(JDialog.class)));
        componentRC.interact(click(), ComponentMatcher.like(".*Cancel.*"), ofType(JButton.class),
            within(ofType(JDialog.class)));
        componentRC.interact(click(), ComponentMatcher.like(".*OK.*"), ofType(JButton.class),
            within(ofType(JDialog.class)));

        return !componentRC.contains(ofType(JDialog.class));
    }

}
