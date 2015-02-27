package net.ulrice.remotecontrol.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class StreamConsumer implements Runnable {

    private final InputStream in;
    private final byte[] prefix;
    private final PrintStream out;

    private boolean writePrefix = true;

    public StreamConsumer(InputStream in, PrintStream out) {
        this(in, null, out);
    }

    public StreamConsumer(InputStream in, String prefix, PrintStream out) {
        super();

        this.in = in;
        this.prefix = (prefix != null) ? prefix.getBytes() : null;
        this.out = out;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        int length;

        try {
            while ((length = in.read(buffer)) >= 0) {
                int start = 0;

                for (int i = 0; i < length; i += 1) {
                    if ((buffer[i] == '\n') || (buffer[i] == '\r')) {
                        writePrefix = true;
                    }
                    else {
                        if (i > start) {
                            out.write(buffer, start, i - start);
                        }

                        if (writePrefix) {
                            if (prefix != null) {
                                out.write(prefix);
                            }
                            writePrefix = false;
                        }

                        start = i;
                    }
                }

                if (length > start) {
                    out.write(buffer, start, length - start);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace(out);
        }

        out.println("Stream closed.");
    }

}
