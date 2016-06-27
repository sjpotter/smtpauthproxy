package org.yucs.spotter.smtpauthproxy.filter;

import org.junit.Before;
import org.junit.Test;
import org.yucs.spotter.smtpauthproxy.logger.NullLogger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoggingFilterTest {
    Filter n;

    @Before
    public void setUp() throws Exception {
        n = new LoggingFilter("", new NullLogger());
    }

    @Test
    public void testReadyNoNewline() throws Exception {
        String testString = "ThisIsATest";

        byte[] test = testString.getBytes();
        n.Input(test, test.length);
        assertFalse(n.Ready());
    }

    @Test
    public void testWithNewLine() throws Exception {
        String testString = "ThisIsATest\r\n";

        byte[] test = testString.getBytes();
        n.Input(test, test.length);
        assertTrue(n.Ready());
        assertArrayEquals(test, n.ReadyOutput());
    }

    @Test
    public void testFlush() throws Exception {
        String testString = "ThisIsATest";

        byte[] test = testString.getBytes();
        n.Input(test, test.length);
        assertArrayEquals(test, n.Flush());
    }

    @Test
    public void testFlushWithNewline() throws Exception {
        String testString = "ThisIsATest\r\nMoreData";

        byte[] test = testString.getBytes();
        n.Input(test, test.length);
        assertArrayEquals(test, n.Flush());
    }
}
