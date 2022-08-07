package com.alibaba.ageiport.security.auth.defaults;

import com.alibaba.ageiport.security.auth.SignerComposer;

import java.util.Map;

/**
 * @author lingyi
 */
public class DefaultSignerComposer implements SignerComposer {
    @Override
    public String composeStringToSign(String protocol, String endpoint, String path, Map<String, String> parameters, Map<String, String> headers, Map<String, String> paths) {
        //later
        return "";
    }
}
