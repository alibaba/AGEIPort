package com.alibaba.ageiport.processor.core.spi.file;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;

/**
 * @author lingyi
 */
@SPI
public interface FileReaderFactory {

    FileReader create(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext context);

}
