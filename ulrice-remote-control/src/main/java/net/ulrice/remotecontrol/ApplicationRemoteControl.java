package net.ulrice.remotecontrol;

public interface ApplicationRemoteControl {

    boolean ping();

    /**
     * Shuts down the application
     */
    public void shutdown();
    
}
