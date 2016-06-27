package org.yucs.spotter.smtpauthproxy.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NullFilter implements Filter {
    final ByteArrayOutputStream data = new ByteArrayOutputStream();

    @Override
    public synchronized void Input(byte[] array, int len) throws IOException {
        data.write(array, 0, len);
    }

    @Override
    public synchronized boolean Ready() {
        return data.size() != 0;
    }

    @Override
    public synchronized byte[] ReadyOutput() {
        byte[] output = data.toByteArray();
        data.reset();

        return output;
    }

    @Override
    public byte[] Flush() {
        return ReadyOutput();
    }
}
