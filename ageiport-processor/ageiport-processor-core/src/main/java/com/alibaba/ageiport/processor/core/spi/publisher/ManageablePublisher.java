package com.alibaba.ageiport.processor.core.spi.publisher;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

import java.util.EventObject;

/**
 * @author lingyi
 */
@SPI
public interface ManageablePublisher<T extends EventObject> {

    void startPublish(AgeiPort ageiPort);

    void publish(PublishPayload payload);

    Class<T> publishEventType();

}
