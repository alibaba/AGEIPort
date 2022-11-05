package com.alibaba.ageiport.processor.core.spi.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncExtensionApiResult {

    private Boolean success;

    private String errorMessage;

    private String syncExtensionApiResult;

}
