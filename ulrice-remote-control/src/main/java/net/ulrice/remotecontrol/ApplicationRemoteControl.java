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
     * @param description some description, may be null
     * @param failure TODO
     * @return a byte array containing a PNG image
     * @throws RemoteControlException on occasion
     */
    public byte[] screenshot(String description, boolean failure) throws RemoteControlException;

    public void overrideSpeedFactor(double speedFactor);

    public boolean combinedWaitFor(double timeoutInSeconds, ComponentMatcher componentMatcher, ControllerMatcher controllerMatcher) throws RemoteControlException;

}
