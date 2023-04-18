package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka.model;

import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class PingParam extends TaskStageEvent {
    private static final long serialVersionUID = 3283036316304535449L;
    private String host;
}
