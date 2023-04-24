package com.alibaba.ageiport.test.processor.core.excel;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.io.FastByteArrayOutputStream;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.file.excel.ExcelWriteHandlerProvider;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.handler.WriteHandler;

import java.util.List;

public class UDFExcelWriteHandlerProvider implements ExcelWriteHandlerProvider {
    @Override
    public List<WriteHandler> provide(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext, DataGroup.Data data) {
        return Lists.newArrayList(
                new ExcelStyleWriteHandler()
        );
    }

    @Override
    public ExcelWriter provideExcelWriter(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream(10240);
        return EasyExcel.write(output).inMemory(true).build();
    }

}
