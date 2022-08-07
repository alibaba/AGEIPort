package com.alibaba.ageiport.security.auth;

/**
 * @author lingyi
 */
public interface Credentials {

    String getAccessKeyId();

    String getAccessKeySecret();
}