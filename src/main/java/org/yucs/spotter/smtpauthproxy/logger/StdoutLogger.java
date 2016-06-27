package org.yucs.spotter.smtpauthproxy.logger;

public class StdoutLogger implements Logger {
    @Override
    public void Log(String s) {
        System.out.println(s);
    }
}
