package com.alibaba.ageiport.security;

import java.io.UnsupportedEncodingException;

public class URLEncoder {
    public final static String URL_ENCODING = "UTF-8";

    public static String encode(String value) throws UnsupportedEncodingException {
        return java.net.URLEncoder.encode(value, URL_ENCODING).replace("+", "%20");
    }

    public static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? java.net.URLEncoder.encode(value, URL_ENCODING).replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~") : null;
    }

    public static String hexEncode(byte[] raw) {
        if (raw == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < raw.length; i++) {
            String hex = Integer.toHexString(raw[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
