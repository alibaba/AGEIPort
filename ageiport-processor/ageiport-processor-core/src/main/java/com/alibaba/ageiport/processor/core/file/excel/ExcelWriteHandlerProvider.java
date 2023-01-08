package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.excel.write.handler.WriteHandler;

import java.util.List;

@SPI
public interface ExcelWriteHandlerProvider {

    List<WriteHandler> provide(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext);

}
