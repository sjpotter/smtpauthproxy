package org.yucs.spotter.smtpauthproxy.proxy;

import org.apache.commons.codec.binary.Base64;
import org.yucs.spotter.smtpauthproxy.filter.Filter;
import org.yucs.spotter.smtpauthproxy.filter.LoggingFilter;
import org.yucs.spotter.smtpauthproxy.logger.FileLogger;
import org.yucs.spotter.smtpauthproxy.logger.Logger;
import org.yucs.spotter.smtpauthproxy.logger.StdoutLogger;
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
    final private Filter c2s_filter;
    final private Filter s2c_filter;
    Logger l = null;

    AbstractProxy(Config c, Socket client) throws ProxyException {
        this.config = c;
        this.client = client;

        //l = new StdoutLogger();
        try {
            l = new FileLogger("test.log");
        } catch (IOException e) {
            throw new ProxyException("Couldn't open test.log", e);
        }

        c2s_filter = new LoggingFilter("c2s: ", l);
        //c2s_filter = new NullFilter();
        s2c_filter = new LoggingFilter("s2c: ", l);
        //s2c_filter = new NullFilter();
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
            s2c_filter.Input(header.toByteArray(), header.size());
            if (!s2c_filter.Ready()) {
                throw new ProxyException("header wasn't ready through filter");
            }
            clientWriter.write(s2c_filter.ReadyOutput());
        } catch (IOException | ProxyException e) {
            e.printStackTrace();
            return;
        }

        //3. connect the sockets together
        SocketsReaderWriter c2s = new SocketsReaderWriter(clientReader, smtpServerWriter, c2s_filter);
        SocketsReaderWriter s2c = new SocketsReaderWriter(smtpServerReader, clientWriter, s2c_filter);

        c2s.setOther(s2c);
        s2c.setOther(c2s);

        c2s.start(); s2c.start();

        try {
            c2s.join(); s2c.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            l.Close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doAuth() throws ProxyException, IOException {
        OutputStream smtpServerWriter = smtpServer.getOutputStream();
        InputStream smtpServerReader = smtpServer.getInputStream();

        byte[] ehlo = new String("ehlo " + config.getServer() + "\n").getBytes();

        smtpServerWriter.write(ehlo);

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
