package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.file.FileReader;
import com.alibaba.ageiport.processor.core.spi.file.FileReaderFactory;

/**
 * @author lingyi
 */
public class ExcelFileReaderFactory implements FileReaderFactory {


    @Override
    public FileReader create(AgeiPort ageiPort, MainTask mainTask, ColumnHeaders columnHeaders) {
        ExcelFileReader excelFileReader = new ExcelFileReader(ageiPort, mainTask, columnHeaders);
        return excelFileReader;
    }

}
