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
public class CreateTaskSpecificationResponse extends Response {

    private static final long serialVersionUID = -4725069330912983684L;

    private Data data;

    @ToString
    @Setter
    @Getter
    public static class Data {
        private Long id;
    }
}
