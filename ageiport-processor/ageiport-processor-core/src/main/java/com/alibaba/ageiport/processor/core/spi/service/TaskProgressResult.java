package com.alibaba.ageiport.processor.core.spi.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskProgressResult {

    private String mainTaskId;

    private String stageCode;

    private String stageName;

    private Double percent;

    private Boolean isFinished;

    private Boolean isError;

    private Integer totalSubTaskCount;

    private Integer finishedSubTaskCount;

    private Integer errorSubTaskCount;

    private List<Log> logs;

    private Map<String, SubTaskProgress> subTaskProgressMap;


    @Getter
    @Setter
    public static class SubTaskProgress {

        private String subTaskId;

        private String stageCode;

        private String stageName;

        private Double percent;

        private Boolean isFinished;

        private Boolean isError;

        private List<Log> logs;

    }

    @Getter
    @Setter
    public static class Log {

        private String stage;

        private String name;

        private String date;

        private Long cost;

        private Integer order;
    }

}
