package com.alibaba.ageiport.processor.core.task.exporter.slice;

import com.alibaba.ageiport.processor.core.spi.task.slice.SliceStrategy;
import com.alibaba.ageiport.processor.core.task.exporter.context.ExportMainTaskContext;

/**
 * @author lingyi
 */
public interface ExportSliceStrategy<QUERY, DATA, VIEW> extends SliceStrategy<ExportSlice, ExportMainTaskContext<QUERY, DATA, VIEW>> {
}
