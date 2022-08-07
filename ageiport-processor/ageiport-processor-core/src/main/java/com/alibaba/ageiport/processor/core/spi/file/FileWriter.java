package com.alibaba.ageiport.processor.core.spi.file;

import java.io.Closeable;
import java.io.InputStream;

/**
 * @author lingyi
 */
public interface FileWriter extends Closeable {

    void write(DataGroup fileData);

    InputStream finish();

}
