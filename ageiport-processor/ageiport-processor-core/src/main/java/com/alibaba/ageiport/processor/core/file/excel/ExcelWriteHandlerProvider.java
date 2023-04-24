package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.common.io.FastByteArrayOutputStream;
import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.handler.WriteHandler;

import java.util.List;

@SPI
public interface ExcelWriteHandlerProvider {

    List<WriteHandler> provide(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext, DataGroup.Data data);

    default ExcelWriter provideExcelWriter(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream(10240);
        return EasyExcel.write(output).build();
    }

}
