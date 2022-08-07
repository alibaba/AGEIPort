package com.alibaba.ageiport.security.auth;

import com.alibaba.ageiport.ext.arch.SPI;

import java.util.Map;

/**
 * @author lingyi
 */
@SPI
public interface SignerComposer {
    String composeStringToSign(String protocol, String endpoint, String path, Map<String, String> parameters,
                               Map<String, String> headers, Map<String, String> paths);
}
