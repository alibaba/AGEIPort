package com.alibaba.ageiport.processor.core.file.store;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.ext.file.store.FileStore;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Map;

/**
 * @author lingyi
 */
public class LocalFileStore implements FileStore {

    private static Logger log = LoggerFactory.getLogger(LocalFileStore.class);

    private String basePath;

    public LocalFileStore(LocalFileStoreOptions options) {
        this.basePath = options.getBasePath();
        File folder = new File(basePath);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
    }

    @Override
    public void save(String path, InputStream inputStream, Map<String, Object> runtimeParams) {
        String filePath = basePath + path;
        File file = new File(filePath);

        FileOutputStream downloadFile = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            int index;
            byte[] bytes = new byte[1024];
            downloadFile = new FileOutputStream(filePath);
            while ((index = inputStream.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
            downloadFile.close();
        } catch (IOException e) {
            log.error("LocalFileStore save failed, path:{}");
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(downloadFile);
        }

    }

    @Override
    public InputStream get(String path, Map<String, Object> runtimeParams) {
        String filePath = basePath + path;
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(String path, Map<String, Object> runtimeParams) {
        String filePath = basePath + path;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return;
        }
        file.delete();
    }

    @Override
    public boolean exists(String path, Map<String, Object> runtimeParams) {
        String filePath = basePath + path;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        } else {
            return true;
        }
    }
}
