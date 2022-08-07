package com.alibaba.ageiport.processor.core.task.importer.slice;

import com.alibaba.ageiport.processor.core.spi.task.slice.SliceStrategy;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportMainTaskContext;

/**
 * @author lingyi
 */
public interface ImportSliceStrategy<QUERY, DATA, VIEW> extends SliceStrategy<ImportSlice, ImportMainTaskContext<QUERY, DATA, VIEW>> {
}
