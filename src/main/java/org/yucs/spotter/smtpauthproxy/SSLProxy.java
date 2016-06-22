package org.yucs.spotter.smtpauthproxy;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class SSLProxy extends AbstractProxy {
    public SSLProxy(Config c, Socket client) {
        super(c, client);
    }

    public void connect() throws IOException {
        SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();

        SSLSocket smtpServer = (SSLSocket) f.createSocket(config.getServer(), config.getServerPort());
        smtpServer.startHandshake();
        this.smtpServer = smtpServer;
    }
}
