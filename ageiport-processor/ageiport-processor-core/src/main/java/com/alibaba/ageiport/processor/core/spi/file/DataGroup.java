package com.alibaba.ageiport.processor.core.spi.file;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class DataGroup {

    List<Data> data;

    public DataGroup() {
    }

    @Getter
    @Setter
    public static class Data {

        private String name;

        private List<Item> items;
    }


    @Getter
    @Setter
    public static class Item {

        private String code;

        private Map<String, Object> values;
    }
}
