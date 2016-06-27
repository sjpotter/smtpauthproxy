package org.yucs.spotter.smtpauthproxy.logger;

import java.io.IOException;

public interface Logger {
    void Log(String s) throws IOException;
    void Close() throws IOException;
}
