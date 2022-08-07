package com.alibaba.ageiport.processor.core.task.importer.slice;

import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.task.slice.Slice;

/**
 * @author lingyi
 */
public interface ImportSlice extends Slice {
    DataGroup getDataGroup();

    Integer getCount();

    Integer getPageSize();

}
