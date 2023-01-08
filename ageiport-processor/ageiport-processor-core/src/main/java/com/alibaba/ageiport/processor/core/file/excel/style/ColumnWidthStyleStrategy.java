package com.alibaba.ageiport.processor.core.file.excel.style;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.style.column.AbstractHeadColumnWidthStyleStrategy;

/**
 * 列宽度策略
 *
 * @author xuechao.sxc
 */
public class ColumnWidthStyleStrategy extends AbstractHeadColumnWidthStyleStrategy {

    private ColumnHeaders columnHeaders;

    public ColumnWidthStyleStrategy(ColumnHeaders columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    @Override
    protected Integer columnWidth(Head head, Integer columnIndex) {
        ColumnHeader headerByIndex = columnHeaders.getHeaderByIndex(columnIndex + 1);
        final Integer columnWidth = headerByIndex.getColumnWidth();
        return columnWidth == null ? 25 : columnWidth;
    }


}