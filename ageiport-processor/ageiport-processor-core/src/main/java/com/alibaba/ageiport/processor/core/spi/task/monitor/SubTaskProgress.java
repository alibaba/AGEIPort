package com.alibaba.ageiport.processor.core.spi.task.monitor;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class SubTaskProgress implements Serializable {

    private static final long serialVersionUID = -4140371841820894525L;

    private String subTaskId;

    private String stageCode;

    private String stageName;

    private Double percent;

    private Boolean isFinished;

    private Boolean isError;

    private List<TaskProgressLog> logs;

    public void addLog(TaskProgressLog log) {
        if (logs == null) {
            logs = new ArrayList<>();
        }
        logs.add(log);
    }
}
