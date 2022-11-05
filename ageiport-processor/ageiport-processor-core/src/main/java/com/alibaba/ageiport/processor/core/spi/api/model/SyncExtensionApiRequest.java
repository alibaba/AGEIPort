package com.alibaba.ageiport.processor.core.spi.api.model;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SyncExtensionApiRequest extends ApiRequest<SyncExtensionApiResponse> {

    private static final long serialVersionUID = 3221896829114478022L;

    private String syncExtensionApiCode;

    private String syncExtensionApiParam;

}
