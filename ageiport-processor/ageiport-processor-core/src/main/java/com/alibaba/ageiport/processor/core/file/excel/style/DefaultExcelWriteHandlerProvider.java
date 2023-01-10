package com.alibaba.ageiport.processor.core.file.excel.style;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.file.excel.ExcelWriteHandlerProvider;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.excel.write.handler.WriteHandler;

import java.util.List;

public class DefaultExcelWriteHandlerProvider implements ExcelWriteHandlerProvider {
    @Override
    public List<WriteHandler> provide(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {
        return Lists.newArrayList(
                new CellSelectorWriteHandler(columnHeaders),
                new ColumnWidthStyleStrategy(columnHeaders)
        );
    }

}
