package org.yucs.spotter.smtpauthproxy.filter;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class NullFilterTest {
    Filter n;
    String testString = "ThisIsATest";

    @Before
    public void setUp() throws Exception {
        n = new NullFilter();
    }

    @Test
    public void testInputOutput() throws Exception {
        byte[] test = testString.getBytes();
        n.Input(test, test.length);
        assertTrue(n.Ready());
        assertArrayEquals(test, n.ReadyOutput());
    }

    @Test
    public void testFlush() throws Exception {
        byte[] test = testString.getBytes();
        n.Input(test, test.length);
        assertArrayEquals(test, n.Flush());
    }
}