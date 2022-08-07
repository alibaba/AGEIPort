package com.alibaba.ageiport.security.auth.defaults.signer;

import com.alibaba.ageiport.security.URLEncoder;
import com.alibaba.ageiport.security.auth.Credentials;
import com.alibaba.ageiport.security.auth.Signer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author lingyi
 */
public class HmacSHA256 implements Signer {

    private static final String ALGORITHM_NAME = "HmacSHA256";

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String signString(String stringToSign, Credentials credentials) {
        try {
            String accessKeySecret = credentials.getAccessKeySecret();
            Mac sha256_HMAC = Mac.getInstance(ALGORITHM_NAME);
            SecretKeySpec secret_key = new SecretKeySpec(accessKeySecret.getBytes(), ALGORITHM_NAME);
            sha256_HMAC.init(secret_key);
            return URLEncoder.hexEncode(sha256_HMAC.doFinal(stringToSign.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.toString());
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    @Override
    public boolean verify(String stringToSign, String sign, Credentials credentials) {
        return true;
    }

    @Override
    public String getAuthorization(Credentials credentials, String sign) {
        return this.getName() + " Credential=" + credentials.getAccessKeyId() + ",Signature=" + sign;
    }
}
