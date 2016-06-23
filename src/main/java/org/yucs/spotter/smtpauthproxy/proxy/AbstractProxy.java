package org.yucs.spotter.smtpauthproxy.proxy;

import org.apache.commons.codec.binary.Base64;
import org.yucs.spotter.smtpauthproxy.utils.Config;
import org.yucs.spotter.smtpauthproxy.utils.SocketsReaderWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractProxy implements Proxy {
    final Config config;
    Socket smtpServer;
    final private Socket client;

    AbstractProxy(Config c, Socket client) {
        this.config = c;
        this.client = client;
    }

    public abstract void connect() throws IOException;

    public void run() {
        OutputStream smtpServerWriter;
        InputStream smtpServerReader;
        OutputStream clientWriter;
        InputStream clientReader;

        final ByteArrayOutputStream header = new ByteArrayOutputStream();

        try {
            smtpServerWriter = smtpServer.getOutputStream();
            smtpServerReader = smtpServer.getInputStream();

            clientWriter = client.getOutputStream();
            clientReader = client.getInputStream();

            //1a. read header
            header.write(smtpServerReader.read());
            while (smtpServerReader.available() != 0) {
                header.write(smtpServerReader.read());
            }

            doAuth();

            //1b. write header to client
            clientWriter.write(header.toByteArray());
        } catch (IOException | ProxyException e) {
            e.printStackTrace();
            return;
        }

        //3. connect the sockets together
        SocketsReaderWriter c2s = new SocketsReaderWriter(clientReader, smtpServerWriter);
        SocketsReaderWriter s2c = new SocketsReaderWriter(smtpServerReader, clientWriter);

        c2s.setOther(s2c);
        s2c.setOther(c2s);

        c2s.start(); s2c.start();
    }

    private void doAuth() throws ProxyException, IOException {
        OutputStream smtpServerWriter = smtpServer.getOutputStream();
        InputStream smtpServerReader = smtpServer.getInputStream();

        String ehlo = "ehlo " + config.getServer() + "\n";
        smtpServerWriter.write(ehlo.getBytes());

        ByteArrayOutputStream byteAbilities = new ByteArrayOutputStream();
        byteAbilities.write(smtpServerReader.read());
        while (smtpServerReader.available() != 0) {
            byteAbilities.write(smtpServerReader.read());
        }
        String stringAbilities = new String(byteAbilities.toByteArray(), "ASCII");
        String[] abilities = stringAbilities.split("\\n");
        for (String ability : abilities) {
            if (ability.startsWith("250-AUTH")) {
                if (ability.contains("PLAIN")) {
                    doPlain();
                } else if (ability.contains("LOGIN")) {
                    throw new ProxyException("LOGIN Unsupported yet");
                } else {
                    throw new ProxyException("Unsupported AUTH types: " + ability);
                }

                break;
            }
        }
    }

    private void doPlain() throws IOException {
        OutputStream smtpServerWriter = smtpServer.getOutputStream();
        InputStream smtpServerReader = smtpServer.getInputStream();

        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        smtpServerWriter.write(getPlainAuth().getBytes());

        //would be smart to do something with this
        ret.write(smtpServerReader.read());
        while (smtpServerReader.available() != 0) {
            ret.write(smtpServerReader.read());
        }
    }

    private String getPlainAuth() {
        String authString = config.getUser() + "\0" + config.getUser() + "\0" + config.getPassword();
        String base64Auth = Base64.encodeBase64String(authString.getBytes());

        return "auth plain " + base64Auth + "\n";
    }
}
