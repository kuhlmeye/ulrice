package net.ulrice.module.impl;

/**
 * This class is used to get information about the closing state of the module(s).
 * 
 * @author fox0lr2
 */
public interface IFCloseHandler {

    /**
     * Called if a module / all modules was closed successfully.
     */
    void closeSuccess();

    /**
     * Called if the closing of a module / all modules was canceled.
     */
    void closeFailure();
}
