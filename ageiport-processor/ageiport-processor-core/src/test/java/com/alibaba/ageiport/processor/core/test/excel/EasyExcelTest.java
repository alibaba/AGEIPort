package com.alibaba.ageiport.processor.core.test.excel;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.processor.core.test.TestHelper;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

public class EasyExcelTest {

    @Test
    public void test() {
        TestHelper testHelper = new TestHelper(null);
        String filePath = testHelper.file("MultiSheetImportProcessor.xlsx");
        InputStream inputStream1 = this.getClass().getClassLoader().getResourceAsStream(filePath);

        ExcelReader excelReader = EasyExcel.read(inputStream1).build();
        List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();
        for (ReadSheet readSheet : readSheets) {
            readSheet.setCustomReadListenerList(Lists.newArrayList(new NoModelDataListener()));
        }
        excelReader.read(readSheets);
    }
}
