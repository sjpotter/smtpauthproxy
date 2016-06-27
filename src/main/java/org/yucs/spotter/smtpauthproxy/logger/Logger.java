package org.yucs.spotter.smtpauthproxy.logger;

import java.io.IOException;

public interface Logger {
    void log(String s) throws IOException;
    void close() throws IOException;
}
