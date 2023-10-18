package com.alibaba.ageiport.test.processor.core;

import com.alibaba.ageiport.processor.core.AgeiPortOptions;

public class ProcessorTestOptions {
    public static AgeiPortOptions options() {
        AgeiPortOptions options = AgeiPortOptions.debug();
        return options;
    }
}
