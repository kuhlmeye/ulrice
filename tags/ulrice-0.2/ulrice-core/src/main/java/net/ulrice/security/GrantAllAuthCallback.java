package net.ulrice.security;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.impl.action.Action;

public class GrantAllAuthCallback implements IFAuthCallback {

	@Override
	public boolean allowOpenModule(IFModule module, IFController ctrl) {
		return true;
	}

	@Override
	public boolean allowRegisterAction(IFController ctrl, Action moduleAction) {
		return true;
	}

	@Override
	public boolean allowEnableAction(IFController ctrl, Action moduleAction) {
		return true;
	}

	@Override
	public boolean allowExecuteAction(IFController ctrl, Action moduleAction) {
		return true;
	}

	@Override
	public boolean allowRegisterModule(IFModule module) {
		return true;
	}

}
