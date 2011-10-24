package net.ulrice.remotecontrol;

/**
 * General exception class for all remote control methods
 * 
 * @author Manfred HANTSCHEL
 */
public class RemoteControlException extends Exception {

    private static final long serialVersionUID = 1L;

    public RemoteControlException(String message) {
        super(message);
    }

    public RemoteControlException(String message, Throwable cause) {
        super(message, cause);
    }

}
