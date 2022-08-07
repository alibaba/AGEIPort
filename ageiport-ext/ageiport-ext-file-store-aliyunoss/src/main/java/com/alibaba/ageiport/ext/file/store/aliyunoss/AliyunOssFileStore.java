package com.alibaba.ageiport.ext.file.store.aliyunoss;

import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;

import java.io.InputStream;
import java.util.Map;

/**
 * @author lingyi
 */
public class AliyunOssFileStore implements FileStore {
    private OSSClient ossClient;
    private String bucketName;

    public AliyunOssFileStore(OSSClient ossClient, String bucketName) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
    }

    @Override
    public void save(String path, InputStream inputStream, Map<String, Object> runtimeParams) {
        String bucketName = (String) runtimeParams.get(AliyunOssConstants.BUCKET_NAME_KEY);
        if (StringUtils.isBlank(bucketName)) {
            ossClient.putObject(this.bucketName, path, inputStream);
        } else {
            ossClient.putObject(bucketName, path, inputStream);
        }
    }

    @Override
    public InputStream get(String path, Map<String, Object> runtimeParams) {
        String bucketName = (String) runtimeParams.get(AliyunOssConstants.BUCKET_NAME_KEY);
        OSSObject object;
        if (StringUtils.isBlank(bucketName)) {
            object = ossClient.getObject(this.bucketName, path);
        } else {
            object = ossClient.getObject(bucketName, path);
        }
        if (object == null) {
            return null;
        }
        return object.getObjectContent();
    }

    @Override
    public void remove(String path, Map<String, Object> runtimeParams) {
        String bucketName = (String) runtimeParams.get(AliyunOssConstants.BUCKET_NAME_KEY);
        if (StringUtils.isBlank(bucketName)) {
            ossClient.deleteObject(this.bucketName, path);
        } else {
            ossClient.deleteObject(bucketName, path);
        }
    }

    @Override
    public boolean exists(String path, Map<String, Object> runtimeParams) {
        String bucketName = (String) runtimeParams.get(AliyunOssConstants.BUCKET_NAME_KEY);
        if (StringUtils.isBlank(bucketName)) {
            return ossClient.doesObjectExist(this.bucketName, path);
        } else {
            return ossClient.doesObjectExist(bucketName, path);
        }
    }
}
