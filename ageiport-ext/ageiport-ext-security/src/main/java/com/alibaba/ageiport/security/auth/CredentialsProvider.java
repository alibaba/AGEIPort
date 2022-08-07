package com.alibaba.ageiport.security.auth;

import com.alibaba.ageiport.ext.arch.SPI;

/**
 * @author lingyi
 */
@SPI
public interface CredentialsProvider {

    void setCredentials(Credentials creds);

    Credentials getCredentials();

    Credentials getCredentials(String accessKeyId);
}
