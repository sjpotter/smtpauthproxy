package org.yucs.spotter.smtpauthproxy.proxy;

import org.apache.commons.codec.binary.Base64;
import org.yucs.spotter.smtpauthproxy.utils.Config;
import org.yucs.spotter.smtpauthproxy.utils.ReaderWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractProxy implements Proxy {
    protected Config config;
    protected Socket client;
    protected Socket smtpServer;

    public AbstractProxy(Config c, Socket client) {
        this.config = c;
        this.client = client;
    }

    public abstract void connect() throws IOException;

    public void run() {
        OutputStream serverWriter;
        InputStream serverReader;
        OutputStream clientWriter;
        InputStream clientReader;

        String authString = genAuth();

        final ByteArrayOutputStream header = new ByteArrayOutputStream();
        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
            serverWriter = smtpServer.getOutputStream();
            serverReader = smtpServer.getInputStream();

            clientWriter = client.getOutputStream();
            clientReader = client.getInputStream();

            //1a. read header
            header.write(serverReader.read());
            while (serverReader.available() != 0) {
                header.write(serverReader.read());
            }

            //2a. auth
            serverWriter.write(authString.getBytes());

            //2b. read response: (would be smarter to check result)
            ret.write(serverReader.read());
            while (serverReader.available() != 0) {
                ret.write(serverReader.read());
            }

            //1b. write header to client
            clientWriter.write(header.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //3. connect the sockets together
        ReaderWriter c2s = new ReaderWriter(clientReader, serverWriter);
        ReaderWriter s2c = new ReaderWriter(serverReader, clientWriter);

        c2s.setOther(s2c);
        s2c.setOther(c2s);

        c2s.start(); s2c.start();
    }

    private String genAuth() {
        String authString = config.getUser() + "\0" + config.getUser() + "\0" + config.getPassword();
        String base64Auth = Base64.encodeBase64String(authString.getBytes());

        return "auth plain " + base64Auth + "\n";
    }
}
