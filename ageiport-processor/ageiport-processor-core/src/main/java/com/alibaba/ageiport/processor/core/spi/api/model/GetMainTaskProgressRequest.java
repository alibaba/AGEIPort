package com.alibaba.ageiport.processor.core.spi.api.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class GetMainTaskProgressRequest extends ApiRequest<GetMainTaskProgressResponse> {

    private static final long serialVersionUID = 1128851990349690883L;

    private String mainTaskId;

}
