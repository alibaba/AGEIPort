package com.alibaba.ageiport.test.processor.core.excel;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;

public class ExcelStyleWriteHandler implements RowWriteHandler {


    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        Boolean head = context.getHead();
        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
        CellStyle newCellStyle;
        if (head) {
            //can be cached
            newCellStyle = workbook.createCellStyle();
            newCellStyle.setWrapText(true);
            newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            newCellStyle.setAlignment(HorizontalAlignment.CENTER);
            newCellStyle.setLocked(true);
            newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            newCellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            newCellStyle.setBorderTop(BorderStyle.THIN);
            newCellStyle.setBorderBottom(BorderStyle.THIN);
            newCellStyle.setBorderLeft(BorderStyle.THIN);
            newCellStyle.setBorderRight(BorderStyle.THIN);
        } else {
            //can be cached
            newCellStyle = workbook.createCellStyle();
            newCellStyle.setWrapText(true);
            newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            newCellStyle.setAlignment(HorizontalAlignment.LEFT);
            newCellStyle.setBorderTop(BorderStyle.THIN);
            newCellStyle.setBorderBottom(BorderStyle.THIN);
            newCellStyle.setBorderLeft(BorderStyle.THIN);
            newCellStyle.setBorderRight(BorderStyle.THIN);
        }

        final Row row = context.getRow();
        for (Cell cell : row) {
            cell.setCellStyle(newCellStyle);
        }
    }
}
