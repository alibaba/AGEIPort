package com.alibaba.ageiport.security.auth;

import com.alibaba.ageiport.ext.arch.SPI;

/**
 * @author lingyi
 */
@SPI
public interface Signer {

    String getName();

    String getVersion();

    String signString(String stringToSign, Credentials credentials);

    boolean verify(String stringToSign, String sign, Credentials credentials);

    String getAuthorization(Credentials credentials, String sign);
}
