package com.alibaba.ageiport.security.auth.defaults;

import com.alibaba.ageiport.security.auth.Credentials;

/**
 * @author lingyi
 */
@FunctionalInterface
public interface GetCredentialsFunction {
    Credentials find(String accessKeyId);
}
