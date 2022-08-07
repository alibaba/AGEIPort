package com.alibaba.ageiport.processor.core.task.importer.slice;

import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ImportSliceImpl implements ImportSlice {

    private static final long serialVersionUID = -8059500843262421888L;

    private Integer no;

    private Integer count;

    private Integer pageSize;

    private String queryJson;

    private DataGroup dataGroup;
}
