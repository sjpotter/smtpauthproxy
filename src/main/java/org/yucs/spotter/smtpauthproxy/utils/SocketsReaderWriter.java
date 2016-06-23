package org.yucs.spotter.smtpauthproxy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketsReaderWriter extends Thread {
    final private InputStream reader;
    final private OutputStream writer;
    private SocketsReaderWriter other;

    public SocketsReaderWriter(InputStream r, OutputStream w) {
        reader = r;
        writer = w;
    }

    public void setOther(SocketsReaderWriter t) {
        other = t;
    }

    public void run() {
        byte[] buffer = new byte[2048];
        int bytes_read;
        try {
            while((bytes_read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytes_read);
                writer.flush();
            }
        }
        catch (IOException ignored) {}

        // if the client closed its stream to us, we close our stream
        // to the server.  First, interrupt the other thread to break the wait
        other.interrupt();
        try { writer.close(); } catch (IOException ignored) {}
    }
}
