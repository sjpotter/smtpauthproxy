package org.yucs.spotter.smtpauthproxy;

import java.io.IOException;
import java.net.Socket;

public class PlainProxy extends AbstractProxy {

    public PlainProxy(Config c, Socket client) {
        super(c, client);
    }

    public void connect() throws IOException {
        smtpServer = new Socket(config.getServer(), config.getServerPort());
    }
}