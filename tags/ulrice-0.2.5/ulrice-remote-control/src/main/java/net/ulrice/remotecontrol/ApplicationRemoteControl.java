package net.ulrice.remotecontrol;

/**
 * Remote control for universal application stuff
 * 
 * @author Manfred HANTSCHEL
 */
public interface ApplicationRemoteControl {

    boolean ping();

    /**
     * Shuts down the application
     */
    public void shutdown();

}
