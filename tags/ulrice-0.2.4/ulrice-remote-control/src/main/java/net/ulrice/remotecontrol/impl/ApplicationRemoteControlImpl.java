package net.ulrice.remotecontrol.impl;

import net.ulrice.remotecontrol.ApplicationRemoteControl;

/**
 * Implementation of the {@link ApplicationRemoteControl}
 * 
 * @author Manfred HANTSCHEL
 */
public class ApplicationRemoteControlImpl implements ApplicationRemoteControl {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#ping()
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#shutdown()
     */
    @Override
    public void shutdown() {
        System.exit(0);
    }

}