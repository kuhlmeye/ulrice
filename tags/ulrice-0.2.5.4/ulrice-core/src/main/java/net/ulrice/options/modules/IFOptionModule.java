package net.ulrice.options.modules;

import javax.swing.JComponent;

/**
 * Interface for a module that will be plugged into the option dialog of the application.
 * 
 * @author DL10KUH
 */
public interface IFOptionModule {

    /** 
     * Name of the option module that will be displayed in the select list.
     */
    String getName();

    /**
     * Returns the component of the options module.
     */
    JComponent getView();

    /**
     * Called during the initialization of the options dialog. Use this to load data e.g.
     */
    void onInitialize();
    
    /**
     * Called, before the option module is shown in the options dialog.
     */
    void onShow();
    
    /**
     * Called, before the option module will be hidden. 
     */
    void onHide();
    
    /**
     * Called, if the data should be saved (closing of dialog,...)
     */
    void onSave();
}
