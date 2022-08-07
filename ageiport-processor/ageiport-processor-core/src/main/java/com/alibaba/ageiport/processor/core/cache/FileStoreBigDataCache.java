package com.alibaba.ageiport.processor.core.cache;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCache;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author lingyi
 */
public class FileStoreBigDataCache implements BigDataCache {

    private AgeiPort ageiPort;

    private FileStore fileStore;

    @Override
    public void init(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.fileStore = ageiPort.getFileStore();
    }


    @Override
    public <T> T get(String key, Class<T> clazz) {
        InputStream inputStream = fileStore.get(key, new HashMap<>());
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String s = new String(bytes);
            final T t = JsonUtil.toObject(s, clazz);
            return t;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public void put(String key, Object value) {
        final String s = JsonUtil.toJsonString(value);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        try {
            fileStore.save(key, inputStream, new HashMap<>());
        } catch (Throwable e) {
            IOUtils.closeQuietly(inputStream);
        }
    }


    @Override
    public <T> T remove(String key, Class<T> clazz) {
        T t = get(key, clazz);
        fileStore.remove(key, new HashMap<>());
        return t;
    }

    @Override
    public boolean exist(String key) {
        return fileStore.exists(key, new HashMap<>());
    }
}
