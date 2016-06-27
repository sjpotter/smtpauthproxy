package org.yucs.spotter.smtpauthproxy;

import org.yucs.spotter.smtpauthproxy.proxy.Proxy;
import org.yucs.spotter.smtpauthproxy.proxy.ProxyException;
import org.yucs.spotter.smtpauthproxy.proxy.ProxyFactory;
import org.yucs.spotter.smtpauthproxy.utils.Config;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class SmtpAuthProxy {
    public static void main(String[] args) {
        Config config;

        try {
            config = Config.getConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(config.getLocalPort());
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                Proxy proxy = ProxyFactory.getProxy(config, clientSocket);
                proxy.connect();
                new Thread(proxy).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProxyException e) {
            e.printStackTrace();
        }
    }
}

