package com.alibaba.ageiport.processor.core.spi.sync;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.service.SyncExtensionApiParam;
import com.alibaba.ageiport.processor.core.spi.service.SyncExtensionApiResult;

@SPI
public interface SyncExtension {

    SyncExtensionApiResult execute(AgeiPort ageiPort, SyncExtensionApiParam param);
}
