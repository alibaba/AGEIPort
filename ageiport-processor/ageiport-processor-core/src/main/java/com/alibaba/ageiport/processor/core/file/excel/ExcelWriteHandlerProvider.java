package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.common.io.FastByteArrayOutputStream;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfigImpl;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.handler.WriteHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@SPI
public interface ExcelWriteHandlerProvider {

    List<WriteHandler> provide(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext, DataGroup.Data data);

    default ExcelWriter provideExcelWriter(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream(10240);
        ExcelTypeEnum excelTypeEnum = getExcelTypeEnum(fileContext);
        return EasyExcel.write(output).excelType(excelTypeEnum).build();
    }

    @NotNull
    static ExcelTypeEnum getExcelTypeEnum(FileContext fileContext) {
        String runtimeParam = fileContext.getMainTask().getRuntimeParam();
        Map<String, String> runtimeMap = JsonUtil.toMap(runtimeParam);
        String fileType;
        if (runtimeMap == null) {
            fileType = "xlsx";
        } else {
            fileType = runtimeMap.get("fileType");
            if (fileType == null) {
                fileType = "xlsx";
            }
        }

        if (!fileType.startsWith(".")) {
            fileType = "." + fileType;
        }
        ExcelTypeEnum excelTypeEnum = ExcelTypeEnum.XLSX;
        for (ExcelTypeEnum value : ExcelTypeEnum.values()) {
            if (value.getValue().equals(fileType)) {
                excelTypeEnum = value;
            }
        }
        return excelTypeEnum;
    }

}
