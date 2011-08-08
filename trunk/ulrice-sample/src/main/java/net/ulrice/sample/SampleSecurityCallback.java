package net.ulrice.sample;

import java.util.logging.Logger;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.module.impl.action.Action;
import net.ulrice.security.IFAuthCallback;

public class SampleSecurityCallback implements IFAuthCallback {

	private static final Logger LOG = Logger.getLogger(SampleSecurityCallback.class.getName());

	public static final String TYPE_MODULE_REGISTER = "REGISTER_MODULE";

	public static final String TYPE_EXECUTE_ACTION = "EXEC_ACTION";

	@Override
	public boolean allowOpenModule(IFModule module, IFController ctrl) {
		LOG.info("Checking authorization to open module " + Ulrice.getModuleManager().getModule(ctrl).getModuleTitle(IFModuleTitleProvider.Usage.Default));
		return true;
	}

	@Override
	public boolean allowRegisterAction(IFController ctrl, Action moduleAction) {
		LOG.info("Checking authorization to register action " + moduleAction.getUniqueId());
		return true;
	}

	@Override
	public boolean allowEnableAction(IFController ctrl, Action moduleAction) {
		LOG.info("Checking authorization to enable action " + moduleAction.getUniqueId());
		return false;

	}

	@Override
	public boolean allowExecuteAction(IFController ctrl, Action moduleAction) {
		LOG.info("Checking authorization to execute action " + moduleAction.getUniqueId());
		return true;

	}

	@Override
	public boolean allowRegisterModule(IFModule module) {
		LOG.info("Checking authorization to register module " + module.getModuleTitle(IFModuleTitleProvider.Usage.Default));
		return true;
	}

}
