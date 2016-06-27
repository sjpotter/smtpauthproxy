package org.yucs.spotter.smtpauthproxy.filter;

import java.io.IOException;

public interface Filter {
    void Input(byte[] array, int len) throws IOException;
    boolean Ready();
    byte[] ReadyOutput();
    byte[] Flush() throws IOException;
}
