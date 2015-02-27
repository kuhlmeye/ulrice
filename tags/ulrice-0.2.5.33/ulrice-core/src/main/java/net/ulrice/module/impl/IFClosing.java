package net.ulrice.module.impl;

/**
 * This interface is used to confirm or cancel the closing of a controller.
 * 
 * @author fox0lr2
 */
public interface IFClosing {

    /**
     * Call this method to confirm the closing of the controller.
     */
    void doClose();

    /**
     * Call this method to cancel the closing of the controller.
     */
    void doCancelClose();
}
