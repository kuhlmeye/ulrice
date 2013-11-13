package net.ulrice.configuration;

import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.frame.impl.MainFrameConfig;
import net.ulrice.frame.impl.Menubar;
import net.ulrice.module.ModuleRegistrationHelper;
import net.ulrice.module.impl.action.ModuleActionManager;

public class UlriceConfigurationCallbackAdapter implements UlriceConfigurationCallback {

	@Override
	public void configureUlrice(IFAppPrefs appPrefs, MainFrameConfig mainFrameConfig) {
	}

	@Override
	public void addApplicationActions(ModuleActionManager actionManager) {
	}

	@Override
	public void configureMenu(Menubar menubar, ModuleActionManager actionManager) {
	}
	
	@Override
	public void registerModules(ModuleRegistrationHelper regHelper) {
	}
}
