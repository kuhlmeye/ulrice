package net.ulrice;

import net.ulrice.appprefs.DefaultAppPrefs;
import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.IFUlriceConfiguration;
import net.ulrice.configuration.UlriceConfigurationCallback;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.message.TranslationProvider;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.ModuleManager;
import net.ulrice.module.impl.ReflectionModule;
import net.ulrice.module.impl.SimpleModuleTitleRenderer;
import net.ulrice.profile.persister.DefaultProfilePersister;
import net.ulrice.profile.persister.ProfilePersister;
import net.ulrice.security.GrantAllAuthCallback;
import net.ulrice.security.IFAuthCallback;

import org.junit.Before;
import org.junit.Ignore;

@Ignore("Base-Class")
public class AbstractUlriceTest implements IFUlriceConfiguration {

	protected static final String NORMAL_MODULE_ID = "normalModule";
	protected static final String SINGLE_MODULE_ID = "singleModule";
	
	protected ModuleManager moduleManager;

	public AbstractUlriceTest() {
		super();
	}

	@Before
	public void setup() throws ConfigurationException {
        moduleManager = new ModuleManager();
		Ulrice.initialize(this);
		
		// Define the modules.
		ReflectionModule singleModule = new ReflectionModule(SINGLE_MODULE_ID, ModuleType.SingleModule,
				"net.ulrice.TestModule", "net/ulrice/sample/module/sample1/moduleicon.png",
				new SimpleModuleTitleRenderer("Sample 1"));
		
		ReflectionModule normalModule = new ReflectionModule(NORMAL_MODULE_ID, ModuleType.NormalModule,
				"net.ulrice.TestModule", "net/ulrice/sample/module/sample2/moduleicon.png",
				new SimpleModuleTitleRenderer("Sample 2"));
	
	
		// Add the modules.	
		
		moduleManager.registerModule(singleModule);
		moduleManager.registerModule(normalModule);
	
		// Add the modules to the structure.
		moduleManager.addModule(singleModule);
		moduleManager.addModule(normalModule);
	}


	@Override
	public IFModuleManager getModuleManager() {
		return moduleManager;
	}

	@Override
	public IFMainFrame getMainFrame() {
		return null;
	}

	@Override
	public IFModuleStructureManager getModuleStructureManager() {
		return moduleManager;
	}

	@Override
	public IFAuthCallback getAuthCallback() {
		return new GrantAllAuthCallback();
	}

    @Override
    public TranslationProvider getTranslationProvider() {
        return null;
    }

	@Override
	public ProfilePersister getProfilePersister() {
		return new DefaultProfilePersister();
	}

	@Override
	public IFAppPrefs getAppPrefs() {
		return new DefaultAppPrefs();
	}

	@Override
	public UlriceConfigurationCallback getConfigurationCallback() {
		return null;
	}

}