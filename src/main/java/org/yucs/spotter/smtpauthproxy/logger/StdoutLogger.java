package org.yucs.spotter.smtpauthproxy.logger;

public class StdoutLogger implements Logger {
    @Override
    public void log(String s) {
        System.out.println(s);
    }

    @Override
    public void close() { }
}
