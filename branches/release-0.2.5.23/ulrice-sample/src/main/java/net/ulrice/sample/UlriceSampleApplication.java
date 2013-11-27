package net.ulrice.sample;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.ulrice.Ulrice;
import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.DefaultUlriceConfiguration;
import net.ulrice.configuration.UlriceConfigurationCallback;
import net.ulrice.frame.impl.MainFrameConfig;
import net.ulrice.frame.impl.Menubar;
import net.ulrice.frame.impl.Toolbar;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.module.ModuleRegistrationHelper;
import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.AuthReflectionModule;
import net.ulrice.module.impl.SimpleModuleTitleRenderer;
import net.ulrice.module.impl.action.CloseAllModulesAction;
import net.ulrice.module.impl.action.CloseModuleAction;
import net.ulrice.module.impl.action.ExitApplicationAction;
import net.ulrice.module.impl.action.ModuleActionManager;
import net.ulrice.module.impl.action.ModuleDelegationAction;
import net.ulrice.sample.module.behavior.CBehavior;
import net.ulrice.sample.module.databinding.CDataBinding;
import net.ulrice.sample.module.databinding.radiobutton.CRadioButtonSample;
import net.ulrice.sample.module.laflist.LafListController;
import net.ulrice.sample.module.masktextfield.MaskTextFieldModule;
import net.ulrice.sample.module.moviedb.CMovieDB;
import net.ulrice.sample.module.processsample.ProcessSampleModule;
import net.ulrice.sample.module.profiledmodulesample.ProfiledModuleSampleModule;
import net.ulrice.security.Authorization;
import net.ulrice.translator.CTranslator;
import net.ulrice.translator.service.IFTranslationService;
import net.ulrice.translator.service.xml.XMLInMemoryTranslationService;

public class UlriceSampleApplication {

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(UlriceSampleApplication.class.getName());

	/**
	 * Jump-In point. Starts the sample application.
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String[] args) {

		// Set the default log level
		LOG.setLevel(Level.FINE);

		// Initialize ulrice.
		try {
			Ulrice.start(new DefaultUlriceConfiguration(new UlriceConfigurationCallback() {
				
				@Override
				public void configureUlrice(IFAppPrefs appPrefs, MainFrameConfig mainFrameConfig) {
					mainFrameConfig.setToolbarActionOrder(ExitApplicationAction.ACTION_ID, Toolbar.MODULE_ACTIONS);
					mainFrameConfig.setTitle("Ulrice Sample Application");
				}

				@Override
				public void addApplicationActions(ModuleActionManager actionManager) {
					actionManager.addApplicationAction(new ExitApplicationAction("Exit", loadImage("exit.png")));
					actionManager.addApplicationAction(new CloseAllModulesAction("Close All", null));
					actionManager.addApplicationAction(new CloseModuleAction("Close", loadImage("close.gif")));
					actionManager.addApplicationAction(new ModuleDelegationAction("TEST1", "TEST1", false, null));
					actionManager.addApplicationAction(new ModuleDelegationAction("TEST2", "TEST2", false, null));
					actionManager.addApplicationAction(new ModuleDelegationAction("TEST3", "TEST3", false, null));		
				}

				@Override
				public void configureMenu(Menubar menubar, ModuleActionManager actionManager) {
					
					JMenu file = new JMenu("File");
					file.add(new JMenuItem(actionManager.getApplicationAction(CloseModuleAction.ACTION_ID)));
					file.add(new JMenuItem(actionManager.getApplicationAction(CloseAllModulesAction.ACTION_ID)));		
					file.add(new JMenuItem(actionManager.getApplicationAction(ExitApplicationAction.ACTION_ID)));				

					menubar.add(file);
				}
				
				@Override
				public void registerModules(ModuleRegistrationHelper regHelper) {
					regHelper.addTopModule(new ProfiledModuleSampleModule());
					regHelper.addTopModule(new ProcessSampleModule());
					regHelper.addTopModule(new MaskTextFieldModule());	
					regHelper.addTopModule(createTranslatorModule());	

					regHelper.addTopModule(createAuthModule(ModuleType.NormalModule, CMovieDB.class, "Movie DB", "MOVIEDB"));
					regHelper.addTopModule(createAuthModule(ModuleType.NormalModule, LafListController.class, "Look And Feel Constants", "LAFLIST"));
					regHelper.addTopModule(createAuthModule(ModuleType.NormalModule, CDataBinding.class, "Databinding", "DATABINDING"));
					regHelper.addTopModule(createAuthModule(ModuleType.NormalModule, CRadioButtonSample.class, "Radio Button", "RADIOBUTTON"));
					regHelper.addTopModule(createAuthModule(ModuleType.NormalModule, CBehavior.class, "BDD", "BDD"));
				}
			}));
			UlriceSampleDatabindingConfiguration.initialize();
		} catch (ConfigurationException e) {
			LOG.log(Level.SEVERE, "Configuration exception occurred.", e);
			System.exit(0);
		}		

	}

	private static AuthReflectionModule createAuthModule(ModuleType type, Class<? extends IFController> moduleClass, String title, String authString) {
		AuthReflectionModule module = new AuthReflectionModule(moduleClass.getName(), type, moduleClass.getName(), moduleClass.getPackage().getName().replace('.', '/') + "/moduleicon.png", new SimpleModuleTitleRenderer(title));
		module.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "MOVIEDB"));
		return module;
	}

	private static IFModule<CTranslator> createTranslatorModule() {
		return new IFModule<CTranslator>() {
		    final IFTranslationService translatorService = new XMLInMemoryTranslationService();
		    
            public String getModuleTitle(Usage usage) {
                return "Translator";
            }

            @Override
            public String getUniqueId() {
                return "translator";
            }

            @Override
            public ImageIcon getIcon(ModuleIconSize preferredSize) {
                return null; //TODO
            }

            @Override
            public ModuleType getModuleInstanceType() {
                return ModuleType.NormalModule;
            }

            @Override
            public void instantiateModule(ControllerProviderCallback<CTranslator> callback, IFController parent) {
                callback.onControllerReady (new CTranslator (translatorService));
            }
		    
		};
	}

	private static ImageIcon loadImage(String image) {
		return new ImageIcon(UlriceSampleApplication.class.getResource(image));
	}
}
