package com.alibaba.ageiport.security;

import com.alibaba.ageiport.security.auth.CredentialsProvider;
import com.alibaba.ageiport.security.auth.Signer;
import com.alibaba.ageiport.security.auth.SignerComposer;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Setter
@Getter
public class SecurityImpl implements Security {

    private Signer signer;

    private SignerComposer signerComposer;

    private CredentialsProvider credentialsProvider;

}
