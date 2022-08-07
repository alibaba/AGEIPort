package com.alibaba.ageiport.processor.core.dispatcher.http;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class HttpDispatchRequest implements Serializable {

    private static final long serialVersionUID = 6755612788072553919L;

    private String mainTaskId;

    private List<Integer> subTaskNos;

    public HttpDispatchRequest() {
    }

    public HttpDispatchRequest(String mainTaskId, List<Integer> subTaskNos) {
        this.mainTaskId = mainTaskId;
        this.subTaskNos = subTaskNos;
    }
}
