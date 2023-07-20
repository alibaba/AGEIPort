package com.alibaba.ageiport.processor.core.file.excel.style;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.file.excel.ExcelWriteHandlerProvider;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.excel.write.handler.WriteHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultExcelWriteHandlerProvider implements ExcelWriteHandlerProvider {
    @Override
    public List<WriteHandler> provide(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext, DataGroup.Data data) {
        String runtimeParam = fileContext.getMainTask().getRuntimeParam();
        Map<String, String> runtimeMap = JsonUtil.toMap(runtimeParam);
        String fileType = runtimeMap.get("fileType");
        if ("CSV".equalsIgnoreCase(fileType)) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(
                new CellSelectorWriteHandler(columnHeaders, data),
                new ColumnWidthStyleStrategy(columnHeaders)
        );
    }

}
