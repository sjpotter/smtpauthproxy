package org.yucs.spotter.smtpauthproxy.proxy;

public class ProxyException extends Exception {
    ProxyException(String s) { super(s); }
    ProxyException(String s, Exception e) { super(s, e); }
}
