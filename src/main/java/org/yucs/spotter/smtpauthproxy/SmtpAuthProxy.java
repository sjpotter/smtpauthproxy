package org.yucs.spotter.smtpauthproxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SmtpAuthProxy {
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
        }
    }
}

