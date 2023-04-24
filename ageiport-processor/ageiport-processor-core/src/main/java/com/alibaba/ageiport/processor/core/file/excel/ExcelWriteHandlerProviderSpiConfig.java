package com.alibaba.ageiport.processor.core.file.excel;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExcelWriteHandlerProviderSpiConfig {

    private List<String> extensionNames;

    private Boolean inMemory = false;
}
