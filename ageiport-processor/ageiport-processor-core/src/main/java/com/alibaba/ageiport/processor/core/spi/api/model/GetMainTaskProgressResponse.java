package com.alibaba.ageiport.processor.core.spi.api.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class GetMainTaskProgressResponse extends ApiResponse {

    private static final long serialVersionUID = -3095081024018928299L;

    private String mainTaskId;

    private String status;

    private String stageCode;

    private String stageName;

    private Double percent;

    private Boolean isFinished;

    private Boolean isError;

    private Integer totalSubTaskCount;

    private Integer finishedSubTaskCount;

    private Integer errorSubTaskCount;

}
