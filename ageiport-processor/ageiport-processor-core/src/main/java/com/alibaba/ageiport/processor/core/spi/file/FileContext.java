package com.alibaba.ageiport.processor.core.spi.file;

import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import lombok.Data;

@Data
public class FileContext {

    private TaskSpec taskSpec;

    private MainTask mainTask;

    private String bizQuery;

}
