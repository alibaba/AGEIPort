package com.alibaba.ageiport.processor.core.spi.file;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;

/**
 * @author lingyi
 */
@SPI
public interface FileWriterFactory {

    FileWriter create(AgeiPort ageiPort, MainTask mainTask, ColumnHeaders columnHeaders);

}
