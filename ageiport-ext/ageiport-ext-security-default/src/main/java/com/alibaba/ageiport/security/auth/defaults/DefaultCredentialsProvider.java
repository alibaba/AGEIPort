package com.alibaba.ageiport.security.auth.defaults;

import com.alibaba.ageiport.security.auth.Credentials;
import com.alibaba.ageiport.security.auth.CredentialsProvider;

/**
 * @author lingyi
 */
public class DefaultCredentialsProvider implements CredentialsProvider {

    private Credentials credentials;

    @Override
    public void setCredentials(Credentials creds) {
        this.credentials = creds;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public Credentials getCredentials(String accessKeyId) {
        if (credentials.getAccessKeyId().equals(accessKeyId)) {
            return credentials;
        }
        return null;
    }
}
