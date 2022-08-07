package com.alibaba.ageiport.processor.core.file.store;

import com.alibaba.ageiport.ext.file.store.FileStoreOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

/**
 * @author lingyi
 */
@ToString
@Getter
@Setter
public class LocalFileStoreOptions implements FileStoreOptions {

    private String basePath = System.getProperty("user.home") + File.separator + "agei" + File.separator;

    @Override
    public String type() {
        return "LocalFileStore";
    }
}
