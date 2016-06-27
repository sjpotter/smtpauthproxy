package org.yucs.spotter.smtpauthproxy.logger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Logger {
    FileWriter out;

    FileLogger(String f) throws IOException {
        out = new FileWriter(f);
    }

    @Override
    public void Log(String s) throws IOException {
        out.write(s);
        out.write("\n");
    }
}
