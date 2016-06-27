package org.yucs.spotter.smtpauthproxy.filter;

import org.yucs.spotter.smtpauthproxy.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoggingFilter implements Filter {
    final String prefix;
    static final String delim = "\r\n";

    final ByteArrayOutputStream data = new ByteArrayOutputStream();
    final ByteArrayOutputStream ready = new ByteArrayOutputStream();
    final Logger logger;

    public LoggingFilter(String p, Logger l) {
        prefix = p;
        logger = l;
    }

    @Override
    public synchronized void Input(byte[] array, int len) throws IOException {
        data.write(array, 0, len);

        String temp = new String(data.toByteArray()); //.replaceAll("\0", "");
        String[] splits = temp.split(delim, -1);
        for(int i=0; i < splits.length - 1; i++) {
            logger.log(prefix + splits[i]);
            ready.write(splits[i].getBytes());
            ready.write(delim.getBytes());
        }
        data.reset();
        data.write(splits[splits.length-1].getBytes());
    }

    @Override
    public synchronized boolean Ready() {
        return ready.size() != 0;
    }

    @Override
    public synchronized byte[] ReadyOutput() {
        byte[] output = ready.toByteArray();
        ready.reset();

        return output;
    }

    @Override
    public byte[] Flush() throws IOException {
        String temp = new String(data.toByteArray());
        logger.log(prefix + temp);

        ready.write(data.toByteArray());

        byte[] output = ready.toByteArray();
        ready.reset();

        return output;
    }
}
