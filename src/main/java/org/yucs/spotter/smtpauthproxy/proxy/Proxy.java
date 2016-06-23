package org.yucs.spotter.smtpauthproxy.proxy;

import java.io.IOException;

public interface Proxy extends Runnable {
    void connect() throws IOException;
}
