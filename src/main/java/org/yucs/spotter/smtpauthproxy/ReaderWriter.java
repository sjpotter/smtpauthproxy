package org.yucs.spotter.smtpauthproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReaderWriter extends Thread {
    private InputStream reader;
    private OutputStream writer;
    private ReaderWriter other;

    ReaderWriter(InputStream r, OutputStream w) {
        reader = r;
        writer = w;
    }

    public void setOther(ReaderWriter t) {
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
        catch (IOException e) {}

        // if the client closed its stream to us, we close our stream
        // to the server.  First, stop the other thread
        other.stop();
        try { writer.close(); } catch (IOException e) {}
    }
}
