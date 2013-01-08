package net.ulrice.remotecontrol.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class StreamConsumer implements Runnable {

    private final InputStream in;
    private final PrintStream out;

    public StreamConsumer(InputStream in, PrintStream out) {
        super();

        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        int length;

        try {
            while ((length = in.read(buffer)) >= 0) {
                out.write(buffer, 0, length);
            }

        }
        catch (IOException e) {
            e.printStackTrace(out);
        }

        out.println("Stream closed.");
    }

}
