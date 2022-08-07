package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.publisher.ManageablePublisher;
import com.alibaba.ageiport.processor.core.spi.publisher.PublishPayload;

/**
 * @author lingyi
 */
public class WaitDispatchMainTaskReduceEventPublisher implements ManageablePublisher<WaitDispatchMainTaskReduceEvent> {

    private AgeiPort ageiPort;

    @Override
    public void startPublish(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }

    @Override
    public void publish(PublishPayload payload) {
        this.ageiPort.getLocalEventBus().post(new WaitDispatchMainTaskReduceEvent(payload.getMainTaskId()));
    }

    @Override
    public Class<WaitDispatchMainTaskReduceEvent> publishEventType() {
        return WaitDispatchMainTaskReduceEvent.class;
    }
}
