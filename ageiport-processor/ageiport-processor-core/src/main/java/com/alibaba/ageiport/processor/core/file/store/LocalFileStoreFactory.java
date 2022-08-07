package com.alibaba.ageiport.processor.core.file.store;

import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.ext.file.store.FileStoreFactory;
import com.alibaba.ageiport.ext.file.store.FileStoreOptions;

/**
 * @author lingyi
 */
public class LocalFileStoreFactory implements FileStoreFactory {
    @Override
    public FileStore create(FileStoreOptions fileStoreOptions) {
         LocalFileStoreOptions options = (LocalFileStoreOptions) fileStoreOptions;
        return new LocalFileStore(options);
    }
}
