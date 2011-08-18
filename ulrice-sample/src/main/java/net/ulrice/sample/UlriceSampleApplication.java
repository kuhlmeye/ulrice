package net.ulrice.sample;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.ulrice.Ulrice;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.UlriceFileConfiguration;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.module.ModuleType;
import net.ulrice.module.exception.ModuleInstantiationException;
import net.ulrice.module.impl.AuthReflectionModule;
import net.ulrice.module.impl.SimpleModuleTitleRenderer;
import net.ulrice.module.impl.action.CloseAllModulesAction;
import net.ulrice.module.impl.action.CloseModuleAction;
import net.ulrice.module.impl.action.ExitApplicationAction;
import net.ulrice.module.impl.action.ModuleAction;
import net.ulrice.module.impl.action.ModuleActionManager;
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
			InputStream configurationStream = UlriceSampleApplication.class.getResourceAsStream("ulrice.properties");
			UlriceFileConfiguration.initializeUlrice(configurationStream);			
		} catch (ConfigurationException e) {
			LOG.log(Level.SEVERE, "Configuration exception occurred.", e);
			System.exit(0);
		}

		// Define the modules.
		AuthReflectionModule movieDBModule = new AuthReflectionModule("movieDB", ModuleType.NormalModule,
				"net.ulrice.sample.module.moviedb.CMovieDB", "net/ulrice/sample/module/sample1/moduleicon.png",
				new SimpleModuleTitleRenderer("Movie DB"));
		movieDBModule.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "MOVIEDB"));

		AuthReflectionModule sampleModule1 = new AuthReflectionModule("sampleModule1", ModuleType.NormalModule,
				"net.ulrice.sample.module.sample1.CSample1", "net/ulrice/sample/module/sample1/moduleicon.png",
				new SimpleModuleTitleRenderer("Sample 1"));
		sampleModule1.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "SAMPLE1"));
		
		AuthReflectionModule sampleModule2 = new AuthReflectionModule("sampleModule2", ModuleType.NormalModule,
				"net.ulrice.sample.module.sample2.CSample2", "net/ulrice/sample/module/sample2/moduleicon.png",
				new SimpleModuleTitleRenderer("Sample 2"));
		sampleModule2.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "SAMPLE2"));
		
		AuthReflectionModule lafListModule = new AuthReflectionModule("lafList", ModuleType.NormalModule,
				"net.ulrice.sample.module.laflist.CLafList", "net/ulrice/sample/module/laflist/moduleicon.png",
				new SimpleModuleTitleRenderer("Look And Feel Constants"));
		lafListModule.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "LAFLIST"));
		
		AuthReflectionModule dataBindingSample = new AuthReflectionModule("databinding", ModuleType.NormalModule,
				"net.ulrice.sample.module.databinding.CDataBinding", "net/ulrice/sample/module/databinding/moduleicon.png",
				new SimpleModuleTitleRenderer("Data Binding Sample"));
		dataBindingSample.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "DATABINDING"));
		
		IFModule translator = new IFModule() {
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
            public void instantiateModule(ControllerProviderCallback callback) {
                callback.onControllerReady (new CTranslator (translatorService));
            }
		    
		};

		// Add the modules.
		IFModuleManager moduleManager = Ulrice.getModuleManager();
		moduleManager.registerModule(movieDBModule);
		moduleManager.registerModule(sampleModule1);
		moduleManager.registerModule(sampleModule2);
		moduleManager.registerModule(lafListModule);
		moduleManager.registerModule(dataBindingSample);
		moduleManager.registerModule(translator);

		// Add the modules to the structure.
		IFModuleStructureManager moduleStructureManager = Ulrice.getModuleStructureManager();
		moduleStructureManager.addModule(movieDBModule);
		moduleStructureManager.addModule(sampleModule1);
		moduleStructureManager.addModule(sampleModule2);
		moduleStructureManager.addModule(lafListModule);
		moduleStructureManager.addModule(dataBindingSample);
		moduleStructureManager.addModule(translator);

		

		// Add the application actions.
		
		
		ModuleActionManager actionManager = Ulrice.getActionManager();
		actionManager.addApplicationAction(new ExitApplicationAction("Exit", loadImage("exit.png")));
		actionManager.addApplicationAction(new CloseAllModulesAction("Close All", null));
		actionManager.addApplicationAction(new CloseModuleAction("Close", loadImage("close.gif")));
		Ulrice.getActionManager().addApplicationAction(
				new ModuleAction("TEST1", "TEST1", false, null));
		Ulrice.getActionManager().addApplicationAction(
				new ModuleAction("TEST2", "TEST2", false, null));
		Ulrice.getActionManager().addApplicationAction(
				new ModuleAction("TEST3", "TEST3", false, null));

		// Show main frame.
		JFrame mainFrame = Ulrice.getMainFrame().getFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(640, 468);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	private static ImageIcon loadImage(String image) {
		return new ImageIcon(UlriceSampleApplication.class.getResource(image));
	}

}
