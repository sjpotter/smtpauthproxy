package org.yucs.spotter.smtpauthproxy.logger;

import java.io.IOException;

public class NullLogger implements Logger {
    @Override
    public void Log(String s) throws IOException {
        return;
    }
}
