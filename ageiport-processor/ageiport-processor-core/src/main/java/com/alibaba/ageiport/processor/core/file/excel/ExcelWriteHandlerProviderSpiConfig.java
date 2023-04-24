package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.processor.core.file.excel.style.DefaultExcelWriteHandlerProvider;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExcelWriteHandlerProviderSpiConfig {

    private String extensionName = DefaultExcelWriteHandlerProvider.class.getSimpleName();

    private Boolean onWriterPeerSheet = false;
}
