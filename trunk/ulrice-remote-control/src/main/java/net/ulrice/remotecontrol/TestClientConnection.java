package net.ulrice.remotecontrol;

import static net.ulrice.remotecontrol.ComponentMatcher.*;
import static net.ulrice.remotecontrol.RemoteControlCenter.*;

import javax.swing.JFrame;

public class TestClientConnection {
    
    public static void main(String[] args) throws RemoteControlException {
        connectClient("localhost", 2103, 1);

        System.out.println(componentRC().statesOf(ofType(JFrame.class)));
    }
    
}
