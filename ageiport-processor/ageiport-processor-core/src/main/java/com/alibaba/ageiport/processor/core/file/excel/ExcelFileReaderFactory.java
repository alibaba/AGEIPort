package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.ageiport.processor.core.spi.file.FileReader;
import com.alibaba.ageiport.processor.core.spi.file.FileReaderFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;

/**
 * @author lingyi
 */
public class ExcelFileReaderFactory implements FileReaderFactory {


    @Override
    public FileReader create(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {
        ZipSecureFile.setMinInflateRatio(0);
        ExcelFileReader excelFileReader = new ExcelFileReader(ageiPort, columnHeaders, fileContext);
        return excelFileReader;
    }

}
