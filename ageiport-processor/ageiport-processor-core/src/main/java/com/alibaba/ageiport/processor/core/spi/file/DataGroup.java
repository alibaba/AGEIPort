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

        private String code;

        private List<Item> items;

        private Map<String, String> meta;
    }


    @Getter
    @Setter
    public static class Item {

        private String code;

        private Map<String, Object> values;
    }
}
