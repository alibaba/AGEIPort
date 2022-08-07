package com.alibaba.ageiport.ext.file.store.aliyunoss;

import com.alibaba.ageiport.ext.file.store.FileStoreOptions;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.common.auth.CredentialsProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
@NoArgsConstructor
public class AliyunOssFileStoreOptions implements FileStoreOptions {

    @Override
    public String type() {
        return AliyunOssConstants.TYPE;
    }

    private String endpoint;

    private CredentialsProvider credentialsProvider;

    private ClientConfiguration config;

    private String bucketName;
}
