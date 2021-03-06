package net.ulrice.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
import net.ulrice.security.IFAuthCallback;

/**
 * Loads the ulrice configuration from a java properties file.
 * 
 * @author ckuhlmeyer
 */
public class UlriceFileConfiguration extends ClassLoadingHelper implements IFUlriceConfiguration {

	/** The properties. */
	private Properties properties;

	/** The module manager. */
	private IFModuleManager moduleManager;

	/** The main frame of the ulrice application. */
	private IFMainFrame mainFrame;

	private IFModuleStructureManager moduleStructureManager;
	
	/** The authorization callback. */
	private IFAuthCallback authCallback;

    private TranslationProvider translationProvider;

	private ProfilePersister profilePersister;

	private IFAppPrefs appPrefs;

	private UlriceConfigurationCallback configurationCallback;

	/**
	 * Initialize ulrice by a file configuration.
	 * 
	 * @param configurationStream
	 *            The configuration file.
	 * @throws ConfigurationException
	 *             If the configuration could not be loaded.
	 */
	public UlriceFileConfiguration(InputStream configurationStream) throws ConfigurationException {
        if(configurationStream == null) {
            throw new IllegalArgumentException("Configuration stream must not be null.");
        }
	      
        try {
            properties = new Properties();
            properties.load(configurationStream);
        } catch (IOException e) {
            throw new ConfigurationException("Ulrice configuration file could not be loaded.", e);
        }
        
		// Load the needed configuration parameters.
		appPrefs = (IFAppPrefs) loadClass(properties.getProperty(IFAppPrefs.class.getName(), DefaultAppPrefs.class.getName()));
		moduleManager = (IFModuleManager) loadClass(properties.getProperty(IFModuleManager.class.getName(),  ModuleManager.class.getName()));
		moduleStructureManager = (IFModuleStructureManager) loadClass(properties.getProperty(IFModuleStructureManager.class.getName(), ModuleManager.class.getName()));
		mainFrame = (IFMainFrame) loadClass(properties.getProperty(IFMainFrame.class.getName(), MainFrame.class.getName()));
		authCallback = (IFAuthCallback) loadClass(properties.getProperty(IFAuthCallback.class.getName(), null));		
		translationProvider = (TranslationProvider) loadClass(properties.getProperty(TranslationProvider.class.getName(), EmptyTranslationProvider.class.getName()));
		profilePersister = (ProfilePersister) loadClass(properties.getProperty(ProfilePersister.class.getName(), DefaultProfilePersister.class.getName()));
		configurationCallback = (UlriceConfigurationCallback) loadClass(properties.getProperty(UlriceConfigurationCallback.class.getName(), UlriceConfigurationCallbackAdapter.class.getName()));
	}

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getModuleManager()
	 */
    @Override
	public IFModuleManager getModuleManager() {
		return moduleManager;
	}

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getModuleStructureManager()
	 */
    @Override
	public IFModuleStructureManager getModuleStructureManager() {
		return moduleStructureManager;
	}

    @Override
    public TranslationProvider getTranslationProvider() {
        return translationProvider;
    }

	/**
	 * @see net.ulrice.configuration.IFUlriceConfiguration#getMainFrame()
	 */
    @Override
	public IFMainFrame getMainFrame() {
		return mainFrame;
	}


	@Override
	public IFAuthCallback getAuthCallback() {		
		return authCallback;
	}

	@Override
	public ProfilePersister getProfilePersister() {
		return profilePersister;
	}

	@Override
	public IFAppPrefs getAppPrefs() {
		return appPrefs;
	}
	@Override
	public UlriceConfigurationCallback getConfigurationCallback() {
		return configurationCallback;
	}
}
