package org.yucs.spotter.smtpauthproxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private int localPort = 2525;
    private String server = "example.com";
    private int serverPort = 25;
    private String user = "example";
    private String password = "example";
    private boolean ssl = false;

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSSL(String isSSL) { this.ssl = Boolean.parseBoolean(isSSL); }

    public boolean getSSL() { return ssl; }

    public static Config getConfig() throws IOException {
        String propFile = "./config.properties";
        Properties props = new Properties();
        Config c = new Config();

        FileInputStream file = new FileInputStream(propFile);

        if (file != null) {
            props.load(file);
        } else {
            throw new FileNotFoundException("property file '" + propFile + "' not found in the classpath");
        }

        if (!props.containsKey("localport") || !props.containsKey("server") || !props.containsKey("serverport") || !props.containsKey("username") || !props.containsKey("password")) {
            System.out.println("props = " + props.toString());
            throw new FileNotFoundException("property file '" + propFile + "' does not contain all needed settings");
        }

        try {
            c.setLocalPort(Integer.parseInt(props.getProperty("localport")));
        } catch (NumberFormatException e) {
            throw new FileNotFoundException("property file '" + propFile + "' contains an invalid value for local port: " + props.getProperty("localport"));
        }

        c.setServer(props.getProperty("server"));
        try {
            c.setServerPort(Integer.parseInt(props.getProperty("serverport")));
        } catch (NumberFormatException e) {
            throw new FileNotFoundException("property file '" + propFile + "' contains an invalid value for server port: " + props.getProperty("serverport"));
        }

        c.setUser(props.getProperty("username"));
        c.setPassword(props.getProperty("password"));

        c.setSSL(props.getProperty("ssl", "false"));

        return c;
    }
}
