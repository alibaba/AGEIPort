package com.alibaba.ageiport.test.processor.core.model;

import com.alibaba.ageiport.processor.core.annotation.ViewField;
import lombok.Data;

import java.util.Map;

@Data
public class DynamicColumnView {

    @ViewField(headerName = "编码")
    private Integer id;

    @ViewField(headerName = "姓名")
    private String name;

    @ViewField(headerName = "查询参数决定的动态列", isDynamicColumn = true)
    private Map<String, Object> dynamicColumnByQueryParams;

    @ViewField(headerName = "一些系统参数决定的动态列，如日期", isDynamicColumn = true)
    private Map<String, Object> dynamicColumnByDate;
}
