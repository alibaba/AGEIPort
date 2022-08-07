package com.alibaba.ageiport.processor.core.spi.api.model;

import com.alibaba.ageiport.sdk.core.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lingyi
 */
@ToString
@Getter
@Setter
public class ExecuteMainTaskResponse extends Response {

    private static final long serialVersionUID = 2586797218135463338L;

    private String mainTaskId;

}
