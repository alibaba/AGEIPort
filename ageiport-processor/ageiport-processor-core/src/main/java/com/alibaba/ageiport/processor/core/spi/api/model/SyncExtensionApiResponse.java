package com.alibaba.ageiport.processor.core.spi.api.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SyncExtensionApiResponse extends ApiResponse {

    private static final long serialVersionUID = 4726630628840800990L;

    private String syncExtensionApiResult;

}
