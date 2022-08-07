package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.processor.core.spi.AgeiPortFactory;

/**
 * @author lingyi
 */
public class AgeiPortFactoryImpl implements AgeiPortFactory {

    @Override
    public AgeiPort ageiPort(AgeiPortOptions options) {
        AgeiPortOptions.Debug debug = options.getDebug();
        if (debug != null) {
            if (debug.getNamespace() != null) {
                options.setNamespace(debug.getNamespace());
            }
            if (debug.getApp() != null) {
                options.setApp(debug.getApp());
            }
            if (debug.getAccessKeyId() != null) {
                options.setAccessKeyId(debug.getAccessKeyId());
            }
            if (debug.getAccessKeySecret() != null) {
                options.setAccessKeySecret(debug.getAccessKeySecret());
            }
            if (debug.getFileStoreOptions() != null) {
                options.setFileStoreOptions(debug.getFileStoreOptions());
            }
            if (debug.getTaskServerClientOptions() != null) {
                options.setTaskServerClientOptions(debug.getTaskServerClientOptions());
            }
        }
        return AgeiPortImpl.agei(options);
    }
}
