package com.alibaba.ageiport.processor.core.spi.task.monitor;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskProgressLog {

    private String stage;

    private String name;

    private String date;

    private Long cost;

    private Integer order;

}
