package org.yucs.spotter.smtpauthproxy;

import org.apache.commons.codec.binary.Base64;

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

        String authString = config.getUser() + "\0" + config.getUser() + "\0" + config.getPassword();
        String base64Auth = Base64.encodeBase64String(authString.getBytes());
        String smtpAuth = "auth plain " + base64Auth + "\n";

        final ByteArrayOutputStream header = new ByteArrayOutputStream();
        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
            serverWriter = smtpServer.getOutputStream();
            serverReader = smtpServer.getInputStream();

            clientWriter = client.getOutputStream();
            clientReader = client.getInputStream();

            //1. read header
            header.write(serverReader.read());
            while (serverReader.available() != 0) {
                header.write(serverReader.read());
            }

            //2. auth
            serverWriter.write(smtpAuth.getBytes());

            //2a. read response: (would be smarter to check result)
            ret.write(serverReader.read());
            while (serverReader.available() != 0) {
                ret.write(serverReader.read());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //2. for every line on client, write to server
        final Thread[] threads = new Thread[2];

        final OutputStream to_server = serverWriter;
        final InputStream from_server = serverReader;
        final OutputStream to_client = clientWriter;
        final InputStream from_client = clientReader;

        // Define and create a thread to transmit bytes from client to server
        Thread c2s = new Thread() {
            public void run() {
                byte[] buffer = new byte[2048];
                int bytes_read;
                try {
                    while((bytes_read = from_client.read(buffer)) != -1) {
                        to_server.write(buffer, 0, bytes_read);
                        to_server.flush();
                    }
                }
                catch (IOException e) {}

                // if the client closed its stream to us, we close our stream
                // to the server.  First, stop the other thread
                threads[1].stop();
                try { to_server.close(); } catch (IOException e) {}
            }
        };

        // Define and create a thread to copy bytes from server to client.
        Thread s2c = new Thread() {
            public void run() {
                byte[] buffer = new byte[2048];
                int bytes_read;
                try {
                    to_client.write(header.toByteArray());
                    while((bytes_read = from_server.read(buffer)) != -1) {
                        to_client.write(buffer, 0, bytes_read);
                        to_client.flush();
                    }
                }
                catch (IOException e) {}

                // if the server closed its stream to us, we close our stream
                // to the client.  First, stop the other thread, though.
                threads[0].stop();
                try { to_client.close(); } catch (IOException e) {}
            }
        };

        // Store the threads into the final threads[] array, so that the
        // anonymous classes can refer to each other.
        threads[0] = c2s; threads[1] = s2c;

        // start the threads
        c2s.start(); s2c.start();

        // Wait for them to exit
        try { c2s.join(); s2c.join(); } catch (InterruptedException e) {}


    }
}
