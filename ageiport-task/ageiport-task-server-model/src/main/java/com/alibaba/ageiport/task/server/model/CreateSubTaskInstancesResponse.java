package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author lingyi
 */
@ToString
@Setter
@Getter
public class CreateSubTaskInstancesResponse extends Response {

    private static final long serialVersionUID = 8207103833765830968L;

    private Data data;

    @ToString
    @Setter
    @Getter
    public static class Data {
        private List<String> subTaskIds;
    }
}
