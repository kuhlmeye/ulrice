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

    /**
     * Captures a screen shot of all visible frames and creates a byte array containing the PNG image
     * 
     * @return a byte array containing a PNG image
     * @throws RemoteControlException on occasion
     */
    public byte[] screenshot() throws RemoteControlException;
    
    public void overrideSpeedFactor(double speedFactor);

}
