package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ModuleMatcher.and;

import java.util.Collection;

import net.ulrice.Ulrice;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.exception.ModuleInstantiationException;
import net.ulrice.remotecontrol.ModuleMatcher;
import net.ulrice.remotecontrol.ModuleRemoteControl;
import net.ulrice.remotecontrol.ModuleState;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;

/**
 * Implementation of the {@link ModuleRemoteControl}
 * 
 * @author Manfred HANTSCHEL
 */
public class ModuleRemoteControlImpl implements ModuleRemoteControl {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ModuleRemoteControl#ping()
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ModuleRemoteControl#statesOf(net.ulrice.remotecontrol.ModuleMatcher[])
     */
    @Override
    public Collection<ModuleState> statesOf(ModuleMatcher... matchers) throws RemoteControlException {
        return ModuleState.inspectModules(and(matchers).match(Ulrice.getModuleManager().getAllModules()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ModuleRemoteControl#stateOf(net.ulrice.remotecontrol.ModuleMatcher[])
     */
    @Override
    public ModuleState stateOf(ModuleMatcher... matchers) throws RemoteControlException {
        Collection<ModuleState> states = statesOf(matchers);

        if (states.isEmpty()) {
            return null;
        }

        if (states.size() > 1) {
            throw new RemoteControlException("Multiple modules match: " + and(matchers));
        }

        return states.iterator().next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ModuleRemoteControl#contains(net.ulrice.remotecontrol.ModuleMatcher[])
     */
    @Override
    public boolean contains(ModuleMatcher... matchers) throws RemoteControlException {
        return statesOf(matchers).size() > 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ModuleRemoteControl#open(net.ulrice.remotecontrol.ModuleMatcher[])
     */
    @Override
    public boolean open(ModuleMatcher... matchers) throws RemoteControlException {
        Collection<ModuleState> states = statesOf(matchers);

        if (states.isEmpty()) {
            return false;
        }

        boolean success = true;

        for (final ModuleState state : states) {

            final Result<Boolean> result = new Result<Boolean>(20);

            try {
                RemoteControlUtils.invokeInSwing(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Ulrice.getModuleManager().openModule(state.getUniqueId(),
                                new ControllerProviderCallback<IFController>() {

                                    @Override
                                    public void onFailure(ModuleInstantiationException exc) {
                                        result.fireResult(false);
                                    }

                                    @Override
                                    public void onControllerReady(IFController controller) {
                                        result.fireResult(true);
                                    }
                                });
                        }
                        catch (Exception e) {
                            result.fireException(e);
                        }
                    }
                });

                success &= result.aquireResult();
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Opening module failed: " + ModuleMatcher.and(matchers), e);
            }

            RemoteControlUtils.pause();
        }

        return success;
    }

}
