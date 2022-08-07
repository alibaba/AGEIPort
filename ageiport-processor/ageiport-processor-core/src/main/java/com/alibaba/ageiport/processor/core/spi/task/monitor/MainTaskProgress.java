package com.alibaba.ageiport.processor.core.spi.task.monitor;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingyi
 */
@Getter
@Setter
public class MainTaskProgress implements Serializable {

    private static final long serialVersionUID = -1995868611052223256L;

    private String mainTaskId;

    private String stageCode;

    private String stageName;

    private Double percent;

    private Boolean isFinished;

    private Boolean isError;

    private Integer totalSubTaskCount;

    private Integer finishedSubTaskCount;

    private Integer errorSubTaskCount;

    private List<TaskProgressLog> logs;

    private Map<String, SubTaskProgress> subTaskProgressMap;

    public void addLog(TaskProgressLog log) {
        if (logs == null) {
            logs = new ArrayList<>();
        }
        logs.add(log);
    }

    public void addSubTaskProgress(SubTaskProgress subTaskProgress) {
        if (subTaskProgressMap == null) {
            subTaskProgressMap = new ConcurrentHashMap<>();
        }
        subTaskProgressMap.put(subTaskProgress.getSubTaskId(), subTaskProgress);
    }

    public SubTaskProgress getSubTaskProgress(String subTaskId) {
        if (subTaskProgressMap == null) {
            return null;
        }
        return subTaskProgressMap.get(subTaskId);
    }
}
