package net.ulrice.sample;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.ulrice.Ulrice;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.UlriceFileConfiguration;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFModule;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.AuthReflectionModule;
import net.ulrice.module.impl.SimpleModuleTitleRenderer;
import net.ulrice.module.impl.action.CloseAllModulesAction;
import net.ulrice.module.impl.action.CloseModuleAction;
import net.ulrice.module.impl.action.ExitApplicationAction;
import net.ulrice.module.impl.action.ModuleActionManager;
import net.ulrice.module.impl.action.ModuleDelegationAction;
import net.ulrice.sample.module.masktextfield.MaskTextFieldModule;
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
			InputStream configurationStream = UlriceSampleApplication.class.getResourceAsStream("ulrice.properties");
			UlriceFileConfiguration.initializeUlrice(configurationStream);
			UlriceSampleDatabindingConfiguration.initialize();
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
				"net.ulrice.sample.module.laflist.LafListController", "net/ulrice/sample/module/laflist/moduleicon.png",
				new SimpleModuleTitleRenderer("Look And Feel Constants"));
		lafListModule.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "LAFLIST"));
		
		AuthReflectionModule dataBindingSample = new AuthReflectionModule("databinding", ModuleType.NormalModule,
				"net.ulrice.sample.module.databinding.CDataBinding", "net/ulrice/sample/module/databinding/moduleicon.png",
				new SimpleModuleTitleRenderer("Data Binding Sample"));
		dataBindingSample.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "DATABINDING"));
		
		AuthReflectionModule radioModule = new AuthReflectionModule("radio", ModuleType.NormalModule,
				"net.ulrice.sample.module.databinding.radiobutton.CRadioButtonSample", "net/ulrice/sample/module/sample1/moduleicon.png",
				new SimpleModuleTitleRenderer("Radio Button Sample"));
		radioModule.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_MODULE_REGISTER, "RADIOBUTTON"));
		
		AuthReflectionModule behaviorModule = new AuthReflectionModule("behavior", ModuleType.SingleModule, "net.ulrice.sample.module.behavior.CBehavior", "net/ulrice/sample/module/sample1/moduleicon.png",
			new SimpleModuleTitleRenderer("Behavior Driven Development"));
		
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
		
		registerModule(new ProfiledModuleSampleModule());
		registerModule(new ProcessSampleModule());
		registerModule(new MaskTextFieldModule());		
		
		// Add the modules.
		registerModule(movieDBModule);
		registerModule(lafListModule);
		registerModule(dataBindingSample);
		registerModule(translator);
		registerModule(radioModule);
		registerModule(behaviorModule);

		Ulrice.getModuleStructureManager().fireModuleStructureChanged();
		

		// Add the application actions.
		
		
		ModuleActionManager actionManager = Ulrice.getActionManager();
		actionManager.addApplicationAction(new ExitApplicationAction("Exit", loadImage("exit.png")));
		actionManager.addApplicationAction(new CloseAllModulesAction("Close All", null));
		actionManager.addApplicationAction(new CloseModuleAction("Close", loadImage("close.gif")));
		Ulrice.getActionManager().addApplicationAction(
				new ModuleDelegationAction("TEST1", "TEST1", false, null));
		Ulrice.getActionManager().addApplicationAction(
				new ModuleDelegationAction("TEST2", "TEST2", false, null));
		Ulrice.getActionManager().addApplicationAction(
				new ModuleDelegationAction("TEST3", "TEST3", false, null));		

		// Show main frame.
		JFrame mainFrame = Ulrice.getMainFrame().getFrame();
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.setSize(1024, 768);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
        mainFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                
                new ExitApplicationAction(null, null).actionPerformed(new ActionEvent(this, e.getID(), null));
            }
        });
	}

	private static void registerModule(IFModule module) {
		Ulrice.getModuleManager().registerModule(module);
		Ulrice.getModuleStructureManager().addModule(module);
	}

	private static ImageIcon loadImage(String image) {
		return new ImageIcon(UlriceSampleApplication.class.getResource(image));
	}

}
