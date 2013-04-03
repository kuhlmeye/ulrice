package net.ulrice.security;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.impl.action.UlriceAction;

/**
 * Voting auth callback. Delegates the callback to registered callback.
 */
public class VotingAuthCallback implements IFAuthCallback {

	private List<IFAuthCallback> securityCallbackList =  new ArrayList<IFAuthCallback>();
	
	public void addSecurityCallback(IFAuthCallback securityCallback) {
		securityCallbackList.add(securityCallback);
	}
	
	public void removeSecurityCallback(IFAuthCallback securityCallback) {
		securityCallbackList.remove(securityCallback);
	}
	
	@Override
	public boolean allowRegisterModule(IFModule module) {
		boolean result = true;
		for(IFAuthCallback securityCallback : securityCallbackList) {
			result &= securityCallback.allowRegisterModule(module);
		}
		return result;
	}

	@Override
	public boolean allowOpenModule(IFModule module, IFController ctrl) {
		boolean result = true;
		for(IFAuthCallback securityCallback : securityCallbackList) {
			result &= securityCallback.allowOpenModule(module, ctrl);
		}
		return result;
	}

	@Override
	public boolean allowRegisterAction(IFController ctrl, UlriceAction moduleAction) {
		boolean result = true;
		for(IFAuthCallback securityCallback : securityCallbackList) {
			result &= securityCallback.allowRegisterAction(ctrl, moduleAction);
		}
		return result;
	}

	@Override
	public boolean allowEnableAction(IFController ctrl, UlriceAction moduleAction) {
		boolean result = true;
		for(IFAuthCallback securityCallback : securityCallbackList) {
			result &= securityCallback.allowExecuteAction(ctrl, moduleAction);
		}
		return result;
	}

	@Override
	public boolean allowExecuteAction(IFController ctrl, UlriceAction moduleAction) {
		boolean result = true;
		for(IFAuthCallback securityCallback : securityCallbackList) {
			result &= securityCallback.allowExecuteAction(ctrl, moduleAction);
		}
		return result;
	}

    @Override
    public boolean allowRegisterApplicationAction(UlriceAction moduleAction) {
        boolean result = true;
        for(IFAuthCallback securityCallback : securityCallbackList) {
            result &= securityCallback.allowRegisterApplicationAction(moduleAction);
        }
        return result;
    }

}
