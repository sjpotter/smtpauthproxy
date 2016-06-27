package org.yucs.spotter.smtpauthproxy.proxy;

import org.yucs.spotter.smtpauthproxy.utils.Config;

import java.io.IOException;
import java.net.Socket;

public class PlainProxy extends AbstractProxy {

    public PlainProxy(Config c, Socket client) throws ProxyException {
        super(c, client);
    }

    public void connect() throws IOException {
        smtpServer = new Socket(config.getServer(), config.getServerPort());
    }
}