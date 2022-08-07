package com.alibaba.ageiport.ext.file.store.aliyunoss;

import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.ext.file.store.FileStoreFactory;
import com.alibaba.ageiport.ext.file.store.FileStoreOptions;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;

/**
 * @author lingyi
 */
public class AliyunOssFileStoreFactory implements FileStoreFactory {
    @Override
    public FileStore create(FileStoreOptions fileStoreOptions) {
        AliyunOssFileStoreOptions configuration = (AliyunOssFileStoreOptions) fileStoreOptions;

        String endpoint = configuration.getEndpoint();
        CredentialsProvider credentialsProvider = configuration.getCredentialsProvider();
        ClientConfiguration config = configuration.getConfig();
        String bucketName = configuration.getBucketName();


        OSSClient ossClient = new OSSClient(endpoint, credentialsProvider, config);

        AliyunOssFileStore aliyunOssFileStore = new AliyunOssFileStore(ossClient, bucketName);

        return aliyunOssFileStore;
    }
}
