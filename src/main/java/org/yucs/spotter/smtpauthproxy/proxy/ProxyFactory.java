package org.yucs.spotter.smtpauthproxy.proxy;

import org.yucs.spotter.smtpauthproxy.utils.Config;

import java.net.Socket;

public class ProxyFactory {
    public static Proxy getProxy(Config c, Socket client) {
        if (c.getSSL())
            return new SSLProxy(c, client);

        return new PlainProxy(c, client);
    }
}
