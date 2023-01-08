package com.alibaba.ageiport.test.processor.core.model;


import com.alibaba.ageiport.processor.core.annotation.ViewField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class View {

    @ViewField(headerName = "编码")
    private Integer id;

    @ViewField(headerName = "姓名", columnWidth = 30)
    private String name;

}
