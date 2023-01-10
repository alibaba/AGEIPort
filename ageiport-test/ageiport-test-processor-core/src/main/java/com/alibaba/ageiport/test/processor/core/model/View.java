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

    @ViewField(headerName = "性别", values = {"男", "女", "其他"})
    private String gender;

    @ViewField(headerName = "男性问题1", groupName = "男性问题1", groupIndex = 0)
    private String manQuestion1;

    @ViewField(headerName = "男性问题2", groupName = "男性问题2", groupIndex = 0)
    private String manQuestion2;

    @ViewField(headerName = "女性问题1", groupName = "女性问题1", groupIndex = 1)
    private String womenQuestion1;

    @ViewField(headerName = "女性问题2", groupName = "女性问题2", groupIndex = 1)
    private String womenQuestion2;

    @ViewField(headerName = "其他性别问题1", groupName = "其他性别问题1", groupIndex = 2)
    private String otherQuestion1;

    @ViewField(headerName = "其他性别问题2", groupName = "其他性别问题2", groupIndex = 2)
    private String otherQuestion2;
}
