# Introduction #

The ulrice core project is the main framework for building ulrice based rich client applications.
It provides the application frame and manages the modules running in the application. The framework consists of several main
components:

  * [Module Manager](ModuleManager.md)
  * [Module Structure Manager](ModuleStructureManager.md)
  * [Message Handler](MessageHandler.md)
  * [Action Manager](ActionManager.md)
  * [Dialog Manager](DialogManager.md)
  * [Process Manager](ProcessManager.md)
  * [Authorization Callback](AuthCallback.md)

# How To #

## How to configure ulrice ##

Main ulrice configuration is done by providing an implementation of IFUlriceConfiguration interface to the Ulrice.initialize-Method. This interface provides the
implementations of all management classes and additional information needed by ulrice. You can use the provided class UlriceFileConfiguration for loading the
ulrice configuration from a property file.

The property file from the sample application looks like this:
```
# The sample application uses the default module manager.
net.ulrice.module.IFModuleManager=net.ulrice.module.impl.ModuleManager

# The sample application uses the default structure manager.
net.ulrice.module.IFModuleStructureManager=net.ulrice.module.impl.ModuleManager

# The sample application uses the default main frame of ulrice.
net.ulrice.frame.IFMainFrame=net.ulrice.frame.impl.MainFrame

# The title text for the ulrice sample application
net.ulrice.frame.impl.MainFrame.Title=Ulrice Sample Application

net.ulrice.frame.impl.Toolbar.ActionOrder=CLOSE, TEST3, TEST1, TEST2, Toolbar.Placeholder.Separator, Toolbar.Placeholder.ModuleActions
```

The initialization code from the sample application is as follows:
```
// Initialize ulrice.
try {
	InputStream configurationStream = UlriceSampleApplication.class.getResourceAsStream("ulrice.properties");
	UlriceFileConfiguration.initializeUlrice(configurationStream);
} catch (ConfigurationException e) {
	LOG.log(Level.SEVERE, "Configuration exception occurred.", e);
	System.exit(0);
}
```