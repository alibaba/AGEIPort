package com.alibaba.ageiport.test.processor.core.model;

import com.alibaba.ageiport.processor.core.annotation.ViewField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@ToString
@Getter
@Setter
public class Data {

    private Integer id;

    private String name;

    private String gender;

    private BigDecimal age;

    private Integer groupIndex;

    private String groupName;

    private String manQuestion1;

    private String manQuestion2;

    private String womenQuestion1;

    private String womenQuestion2;

    private String otherQuestion1;

    private String otherQuestion2;

    private Map<String, Object> dynamicColumns;
}
