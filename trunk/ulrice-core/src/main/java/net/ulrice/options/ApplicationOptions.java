package net.ulrice.options;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.options.modules.IFOptionModule;

/**
 * Management class for the options of the application that could be configured
 * through a view.
 * 
 * @author DL10KUH
 */
public class ApplicationOptions {

    private static List<IFOptionModule> optionModules = new ArrayList<IFOptionModule>();
    
    public static OptionsDialog showOptionsDialog() {
        OptionsDialog optionsDialog = new OptionsDialog(optionModules);
        optionsDialog.pack();
        optionsDialog.setVisible(true);
        return optionsDialog;
    }
    
    public static void addOptionModule(IFOptionModule optionModule) {
        optionModules.add(optionModule);
    }
}
