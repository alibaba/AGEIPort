package com.alibaba.ageiport.security.auth.defaults;

import com.alibaba.ageiport.security.auth.Credentials;

/**
 * @author lingyi
 */
public class DefaultCredentials implements Credentials {

    private String accessKeyId;
    private String accessKeySecret;

    public DefaultCredentials(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getAccessKeySecret() {
        return accessKeySecret;
    }
}
