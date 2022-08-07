package com.alibaba.ageiport.processor.core.spi.file;

import java.io.Closeable;
import java.io.InputStream;

/**
 * @author lingyi
 */
public interface FileReader extends Closeable {

    void read(InputStream inputStream);

    DataGroup finish();
}
