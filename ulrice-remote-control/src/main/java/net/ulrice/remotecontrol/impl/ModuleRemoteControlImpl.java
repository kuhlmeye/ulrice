package net.ulrice.remotecontrol.impl;

import static net.ulrice.remotecontrol.ModuleMatcher.*;

import java.util.Collection;

import net.ulrice.Ulrice;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.exception.ModuleInstantiationException;
import net.ulrice.remotecontrol.ModuleMatcher;
import net.ulrice.remotecontrol.ModuleRemoteControl;
import net.ulrice.remotecontrol.ModuleState;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.Result;

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
        return ModuleState.inspect(and(matchers).match(Ulrice.getModuleManager().getAllModules()));
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

    @Override
    public boolean open(ModuleMatcher... matchers) throws RemoteControlException {
        Collection<ModuleState> states = statesOf(matchers);

        if (states.isEmpty()) {
            return false;
        }

        boolean success = true;

        for (final ModuleState state : states) {

            final Result<Boolean> result = new Result<Boolean>(5);

            ComponentUtils.invokeInSwing(new Runnable() {
                @Override
                public void run() {
                    try {
                        Ulrice.getModuleManager().openModule(state.getUniqueId(), new ControllerProviderCallback() {

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

        return success;
    }

}
