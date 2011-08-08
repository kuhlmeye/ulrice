package net.ulrice;

import java.util.Properties;

import org.junit.Before;

import net.ulrice.Ulrice;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.IFUlriceConfiguration;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.ModuleManager;
import net.ulrice.module.impl.ReflectionModule;
import net.ulrice.module.impl.SimpleModuleTitleRenderer;
import net.ulrice.security.GrantAllAuthCallback;
import net.ulrice.security.IFAuthCallback;

public class AbstractUlriceTest implements IFUlriceConfiguration {

	protected static final String NORMAL_MODULE_ID = "normalModule";
	protected static final String SINGLE_MODULE_ID = "singleModule";
	
	protected ModuleManager moduleManager;

	public AbstractUlriceTest() {
		super();
	}

	@Before
	public void setup() throws ConfigurationException {
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
	public void loadConfiguration() throws ConfigurationException {
		moduleManager = new ModuleManager();
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
	public Properties getConfigurationProperties() {
		return null;
	}

	@Override
	public IFAuthCallback getAuthCallback() {
		return new GrantAllAuthCallback();
	}

}