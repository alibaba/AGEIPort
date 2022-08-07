package com.alibaba.ageiport.ext.file.store;

import com.alibaba.ageiport.ext.arch.SPI;

/**
 * @author lingyi
 */
@SPI
public interface FileStoreFactory {
    FileStore create(FileStoreOptions fileStoreOptions);
}
