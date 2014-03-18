package net.ulrice.configuration;

import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.frame.impl.MainFrameConfig;
import net.ulrice.frame.impl.Menubar;
import net.ulrice.module.ModuleRegistrationHelper;
import net.ulrice.module.impl.action.ModuleActionManager;

public interface UlriceConfigurationCallback {

	void configureUlrice(IFAppPrefs appPrefs, MainFrameConfig mainFrameConfig);

	void addApplicationActions(ModuleActionManager actionManager);
	
	void configureMenu(Menubar menubar, ModuleActionManager actionManager);
	
	void registerModules(ModuleRegistrationHelper regHelper);
}
