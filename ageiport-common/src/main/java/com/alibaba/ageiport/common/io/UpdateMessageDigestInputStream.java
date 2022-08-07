package com.alibaba.ageiport.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;


abstract class UpdateMessageDigestInputStream extends InputStream {


    public void updateMessageDigest(MessageDigest messageDigest) throws IOException {
        int data;
        while ((data = read()) != -1) {
            messageDigest.update((byte) data);
        }
    }

    public void updateMessageDigest(MessageDigest messageDigest, int len) throws IOException {
        int data;
        int bytesRead = 0;
        while (bytesRead < len && (data = read()) != -1) {
            messageDigest.update((byte) data);
            bytesRead++;
        }
    }

}
