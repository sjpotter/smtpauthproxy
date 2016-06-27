package org.yucs.spotter.smtpauthproxy.utils;

import org.yucs.spotter.smtpauthproxy.filter.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketsReaderWriter extends Thread {
    final private InputStream reader;
    final private OutputStream writer;
    private SocketsReaderWriter other;
    final private Filter filter;

    public SocketsReaderWriter(InputStream r, OutputStream w, Filter f) {
        reader = r;
        writer = w;
        filter = f;
    }

    public void setOther(SocketsReaderWriter t) {
        other = t;
    }

    public void run() {
        byte[] buffer = new byte[2048];
        int bytes_read;
        try {
            while((bytes_read = reader.read(buffer)) != -1) {
                filter.Input(buffer, bytes_read);
                if (filter.Ready()) {
                    writer.write(filter.ReadyOutput());
                    writer.flush();
                }
            }
            writer.write(filter.Flush());
            writer.flush();
        }
        catch (IOException ignored) {}

        // if the client closed its stream to us, we close our stream
        // to the server.  First, interrupt the other thread to break the wait
        other.interrupt();
        try { writer.close(); } catch (IOException ignored) {}
    }
}
