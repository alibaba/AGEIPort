package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lingyi
 */
@ToString
@Setter
@Getter
public class CreateMainTaskInstanceResponse extends Response {

    private static final long serialVersionUID = -1328965422014584720L;

    private Data data;

    @ToString
    @Setter
    @Getter
    public static class Data {
        private String mainTaskId;
    }

}