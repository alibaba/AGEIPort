package com.alibaba.ageiport.ext.file.store;

import java.io.InputStream;
import java.util.Map;

/**
 * @author lingyi
 */
public interface FileStore {

    void save(String path, InputStream inputStream, Map<String, Object> runtimeParams);

    InputStream get(String path, Map<String, Object> runtimeParams);

    void remove(String path, Map<String, Object> runtimeParams);

    boolean exists(String path, Map<String, Object> runtimeParams);
}
