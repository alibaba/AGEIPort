package com.alibaba.ageiport.processor.core.task.exporter.slice;

import com.alibaba.ageiport.processor.core.spi.task.slice.Slice;

/**
 * @author lingyi
 */
public interface ExportSlice extends Slice {

    Integer getOffset();

    Integer getSize();
}
