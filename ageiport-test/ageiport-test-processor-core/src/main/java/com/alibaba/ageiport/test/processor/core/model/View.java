package com.alibaba.ageiport.test.processor.core.model;


import com.alibaba.ageiport.processor.core.annotation.ViewField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class View {

    @ViewField(headerName = {"基本信息", "编码"})
    private Integer id;

    @ViewField(headerName = {"基本信息", "姓名"}, columnWidth = 30)
    private String name;

    @ViewField(headerName = {"基本信息", "性别"}, values = {"男", "女", "其他"})
    private String gender;

    @ViewField(headerName = {"基本信息", "年龄"})
    private BigDecimal age;

    private String groupName;

    private Integer groupIndex;
}
