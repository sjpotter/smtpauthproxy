package org.yucs.spotter.smtpauthproxy.proxy;

import java.io.IOException;

/**
 * Created by spotter on 6/21/16.
 */
public interface Proxy extends Runnable {
    public void connect() throws IOException;
}
