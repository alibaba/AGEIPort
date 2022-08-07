package com.alibaba.ageiport.security;

import com.alibaba.ageiport.security.auth.CredentialsProvider;
import com.alibaba.ageiport.security.auth.Signer;
import com.alibaba.ageiport.security.auth.SignerComposer;

/**
 * @author lingyi
 */
public interface Security {

    CredentialsProvider getCredentialsProvider();

    Signer getSigner();

    SignerComposer getSignerComposer();
}
