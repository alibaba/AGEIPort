package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.FileWriter;
import com.alibaba.ageiport.processor.core.spi.file.FileWriterFactory;

/**
 * @author lingyi
 */
public class ExcelFileWriterFactory implements FileWriterFactory {

    @Override
    public FileWriter create(AgeiPort ageiPort, ColumnHeaders columnHeaders) {
        ExcelFileWriter excelFileWriter = new ExcelFileWriter(ageiPort, columnHeaders);
        return excelFileWriter;
    }


}
