package net.ulrice.remotecontrol;

import static net.ulrice.remotecontrol.RemoteControlCenter.*;

import java.io.FileOutputStream;
import java.io.IOException;

import net.ulrice.Ulrice;

public class TestClientConnection {

    public static void main(String[] args) throws RemoteControlException, IOException {
        connectClient("localhost", Ulrice.DEFAULT_REMOTE_CONTROL_PORT, 1);

        byte[] bytes = applicationRC().screenshot();

        FileOutputStream out = new FileOutputStream("D:/foo.png");
        try {
            out.write(bytes);
        }
        finally {
            out.close();
        }
    }

}
