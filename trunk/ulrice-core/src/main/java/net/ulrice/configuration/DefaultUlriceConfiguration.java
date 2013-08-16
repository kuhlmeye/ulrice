package net.ulrice.configuration;

import net.ulrice.appprefs.DefaultAppPrefs;
import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.frame.impl.MainFrame;
import net.ulrice.message.EmptyTranslationProvider;
import net.ulrice.message.TranslationProvider;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.impl.ModuleManager;
import net.ulrice.profile.persister.DefaultProfilePersister;
import net.ulrice.profile.persister.ProfilePersister;
import net.ulrice.security.GrantAllAuthCallback;
import net.ulrice.security.IFAuthCallback;

public class DefaultUlriceConfiguration implements IFUlriceConfiguration {

	private MainFrame mainFrame = new MainFrame();
	private ModuleManager moduleManager = new ModuleManager();
	private GrantAllAuthCallback grantAll = new GrantAllAuthCallback();
	private EmptyTranslationProvider translationProvider = new EmptyTranslationProvider();
	private DefaultProfilePersister profilePersister = new DefaultProfilePersister();
	private DefaultAppPrefs appPrefs = new DefaultAppPrefs();
	
	public IFModuleManager getModuleManager() {
		return moduleManager;
	}

	public IFMainFrame getMainFrame() {
		return mainFrame;
	}

	public IFModuleStructureManager getModuleStructureManager() {
		return moduleManager;
	}

	public IFAuthCallback getAuthCallback() {
		return grantAll;
	}

	public TranslationProvider getTranslationProvider() {
		return translationProvider;
	}

	public ProfilePersister getProfilePersister() {
		return profilePersister;
	}

	public IFAppPrefs getAppPrefs() {
		return appPrefs;
	}
}
