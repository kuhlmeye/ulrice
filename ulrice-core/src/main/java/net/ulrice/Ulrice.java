package net.ulrice;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.configuration.ConfigurationException;
import net.ulrice.configuration.IFUlriceConfiguration;
import net.ulrice.dialog.DialogManager;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.message.MessageHandler;
import net.ulrice.message.TranslationProvider;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.impl.action.ModuleActionManager;
import net.ulrice.options.ApplicationOptions;
import net.ulrice.process.ProcessManager;
import net.ulrice.profile.ProfileManager;
import net.ulrice.security.GrantAllAuthCallback;
import net.ulrice.security.IFAuthCallback;
import net.ulrice.ui.UI;

/**
 * Class holding all context object of ulrice.
 * 
 * @author ckuhlmeyer
 */
public class Ulrice {

    public static final int DEFAULT_REMOTE_CONTROL_PORT = 62103;

    /** The module manager used by this application. */
    private static IFModuleManager moduleManager;

    /** The manager used to structure the modules. */
    private static IFModuleStructureManager moduleStructureManager;

    /** The main frame used by this application. */
    private static IFMainFrame mainFrame;

    /** The message handler of ulrice. */
    private static MessageHandler messageHandler;

    /** The action manager of ulrice. */
    private static ModuleActionManager actionManager;

    private static ProcessManager processManager;

    private static ProfileManager profileManager;

    /** Contains the I18N-Support */
    private static TranslationProvider translationProvider;

    /** The callback used to handle authorization requests. */
    private static IFAuthCallback securityManager;

    private static DialogManager dialogManager;

    private static EventListenerList listenerList = new EventListenerList();

    private static IFAppPrefs appPrefs;

    /**
     * Initializes ulrice.
     * 
     * @param configuration The configuration used to initialize ulrice.
     * @throws ConfigurationException If the configuration could not be loaded.
     */
    public static void initialize(IFUlriceConfiguration configuration) throws ConfigurationException {
        UI.applyDefaultUI();
        Ulrice.appPrefs = configuration.getAppPrefs();
        Ulrice.moduleManager = configuration.getModuleManager();
        Ulrice.moduleStructureManager = configuration.getModuleStructureManager();
        Ulrice.messageHandler = new MessageHandler();
        Ulrice.actionManager = new ModuleActionManager();
        Ulrice.processManager = new ProcessManager();
        Ulrice.dialogManager = new DialogManager();
        Ulrice.translationProvider = configuration.getTranslationProvider();
        Ulrice.profileManager = new ProfileManager(configuration.getProfilePersister());

        if (configuration.getAuthCallback() != null) {
            Ulrice.securityManager = configuration.getAuthCallback();
        }
        else {
            Ulrice.securityManager = new GrantAllAuthCallback();
        }

        Ulrice.mainFrame = configuration.getMainFrame();
        if (Ulrice.mainFrame != null) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        Ulrice.mainFrame.inializeLayout();
                    }
                });
            }
            catch (InterruptedException e) {
                Ulrice.getMessageHandler().handleException(e);
            }
            catch (InvocationTargetException e) {
                Ulrice.getMessageHandler().handleException(e);
            }
        }

        ConfigurationListener[] listeners = listenerList.getListeners(ConfigurationListener.class);
        if (listeners != null) {
            for (ConfigurationListener listener : listeners) {
                listener.initializationFinished();
            }
        }

        try {
            initializeRemoteControl();
        }
        catch (Exception e) {
            System.err.println("Failed to initialize remote control: " + e.getMessage());
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				Ulrice.shutdown();
			}
		}));
    }

    public static void shutdown() {
        ApplicationOptions.clearOptionsModules();
        Ulrice.actionManager.dispose();
        Ulrice.getModuleStructureManager().shutdown();
        Ulrice.getAppPrefs().shutdown();
    }

    /**
     * Initializes the remote control, if the class is present
     * 
     * @throws ConfigurationException on occasion
     */
    private static void initializeRemoteControl() throws ConfigurationException {
        try {
            Class< ?> remoteControlCenter = Class.forName("net.ulrice.remotecontrol.RemoteControlCenter");

            try {
                remoteControlCenter.getMethod("startServer", int.class).invoke(null, DEFAULT_REMOTE_CONTROL_PORT);
            }
            catch (IllegalArgumentException e) {
                throw new ConfigurationException("Failed to start remote control", e);
            }
            catch (SecurityException e) {
                throw new ConfigurationException("Failed to start remote control", e);
            }
            catch (IllegalAccessException e) {
                throw new ConfigurationException("Failed to start remote control", e);
            }
            catch (InvocationTargetException e) {
                throw new ConfigurationException("Failed to start remote control", e);
            }
            catch (NoSuchMethodException e) {
                throw new ConfigurationException("Failed to start remote control", e);
            }
        }
        catch (ClassNotFoundException e) {
            // ignore, remote control not present
        }
    }

    /**
     * Registers a remote control service, if the remote control class is present
     * 
     * @param type the type of service
     * @param instance the instance
     * @throws ConfigurationException on occasion
     */
    public static <TYPE> void optionalRegisterRemoteControl(TYPE type, Object instance) throws ConfigurationException {
        try {
            Class< ?> remoteControlCenter = Class.forName("net.ulrice.remotecontrol.RemoteControlCenter");

            try {
                remoteControlCenter.getMethod("registerNative", Class.class, Object.class).invoke(null, type, instance);
            }
            catch (IllegalArgumentException e) {
                throw new ConfigurationException("Failed to register remote control", e);
            }
            catch (SecurityException e) {
                throw new ConfigurationException("Failed to register remote control", e);
            }
            catch (IllegalAccessException e) {
                throw new ConfigurationException("Failed to register remote control", e);
            }
            catch (InvocationTargetException e) {
                throw new ConfigurationException("Failed to register remote control", e);
            }
            catch (NoSuchMethodException e) {
                throw new ConfigurationException("Failed to register remote control", e);
            }
        }
        catch (ClassNotFoundException e) {
            // ignore, remote control not present
        }
    }

    /**
     * Return the module manager of ulrice.
     * 
     * @return The module manager.
     */
    public static IFModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Return the main frame of ulrice.
     * 
     * @return The main frame.
     */
    public static IFMainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Return the module structure manager.
     * 
     * @return The module structure manager.
     */
    public static IFModuleStructureManager getModuleStructureManager() {
        return moduleStructureManager;
    }

    /**
     * Returns the message handler of ulrice.
     * 
     * @return The message handler.
     */
    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public static TranslationProvider getTranslationProvider() {
        return translationProvider;
    }

    /**
     * Returns the action manager of ulrice.
     * 
     * @return the actionManager
     */
    public static ModuleActionManager getActionManager() {
        return actionManager;
    }

    public static ProcessManager getProcessManager() {
        return processManager;
    }

    public static DialogManager getDialogManager() {
        return dialogManager;
    }

    public static IFAppPrefs getAppPrefs() {
        return appPrefs;
    }

    /**
     * Returns the authorization callback.
     * 
     * @return The class handling the authorization callback
     */
    public static IFAuthCallback getSecurityManager() {
        return securityManager;
    }

    public static void addConfigurationListener(ConfigurationListener listener) {
        listenerList.add(ConfigurationListener.class, listener);
    }

    public static void removeConfigurationListener(ConfigurationListener listener) {
        listenerList.remove(ConfigurationListener.class, listener);
    }

    public static ProfileManager getProfileManager() {
        return profileManager;
    }
}
