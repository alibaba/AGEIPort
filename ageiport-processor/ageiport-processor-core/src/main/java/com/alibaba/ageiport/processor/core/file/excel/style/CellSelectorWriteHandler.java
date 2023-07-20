package com.alibaba.ageiport.processor.core.file.excel.style;

import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel 下拉框处理器
 */
public class CellSelectorWriteHandler implements SheetWriteHandler {

    private Map<Integer, List<String>> selectorMap;

    private ColumnHeaders columnHeaders;

    private DataGroup.Data data;

    public CellSelectorWriteHandler(ColumnHeaders columnHeaders, DataGroup.Data data) {
        this.selectorMap = new HashMap<>();
        this.columnHeaders = columnHeaders;
        final List<ColumnHeader> headers = columnHeaders.getColumnHeaders();
        for (ColumnHeader header : headers) {
            selectorMap.put(header.getIndex(), header.getValues());
        }
        this.data = data;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (MapUtils.isEmpty(selectorMap)) {
            return;
        }

        Integer headerRowCount = columnHeaders.getHeaderRowCount(writeSheetHolder.getSheetNo());

        Integer sheetNo = writeSheetHolder.getSheetNo();
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = writeSheetHolder.getSheet();

        int lineCount = this.data.getItems().size();
        int lastRow = lineCount + headerRowCount;

        List<ColumnHeader> headersByGroup = columnHeaders.getHeadersByGroup(sheetNo);
        int index = 0;
        for (ColumnHeader columnHeader : headersByGroup) {
            if (CollectionUtils.isNotEmpty(columnHeader.getValues())) {
                setLongHSSFValidation(workbook, sheet, columnHeader.getValues(), headerRowCount, lastRow, index);
            }
            index++;
        }


    }


    /**
     * 解决下拉框过长不显示问题
     */
    public static void setLongHSSFValidation(Workbook workbook, Sheet sheet, List<String> deptList, int firstRow, int lastRow, int colNum) {
        String hiddenName = "hidden_" + sheet.getSheetName() + "_col_" + colNum;
        //1.创建隐藏的sheet页
        Sheet hidden = workbook.createSheet(hiddenName);
        final int sheetIndex1 = workbook.getSheetIndex(hidden);
        //2.循环赋值（为了防止下拉框的行数与隐藏域的行数相对应，将隐藏域加到结束行之后）
        for (int i = 0, length = deptList.size(); i < length; i++) {
            hidden.createRow(lastRow + i).createCell(colNum).setCellValue(deptList.get(i));
        }
        Name category1Name = workbook.createName();
        category1Name.setNameName(hiddenName);
        //3 A1:A代表隐藏域创建第N列createCell(N)时。以A1列开始A行数据获取下拉数组
        category1Name.setRefersToFormula(hiddenName + "!A1:A" + (deptList.size() + lastRow));

        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint(hiddenName);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, colNum, colNum);
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        if (dataValidation instanceof XSSFDataValidation) {
            // 数据校验
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        // 作用在目标sheet上
        sheet.addValidationData(dataValidation);
        // 设置hiddenSheet隐藏
        workbook.setSheetHidden(sheetIndex1, true);
    }
}