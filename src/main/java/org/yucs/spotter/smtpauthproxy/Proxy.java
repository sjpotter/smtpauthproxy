package org.yucs.spotter.smtpauthproxy;

import java.io.IOException;

/**
 * Created by spotter on 6/21/16.
 */
public interface Proxy extends Runnable {
    public void connect() throws IOException;
}
