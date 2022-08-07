package com.alibaba.ageiport.processor.core.task.exporter.slice;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ExportSliceImpl implements ExportSlice {

    private static final long serialVersionUID = -8059500843262421888L;

    private Integer no;

    private Integer size;

    private Integer offset;

    private String queryJson;
}
