package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.publisher.ManageablePublisher;
import com.alibaba.ageiport.processor.core.spi.publisher.PublishPayload;

/**
 * @author lingyi
 */
public class WaitDispatchMainTaskReduceEventPublisher implements ManageablePublisher<WaitDispatchMainTaskReduceEvent> {

    public static Logger logger = LoggerFactory.getLogger(WaitDispatchMainTaskReduceEventPublisher.class);

    private AgeiPort ageiPort;

    @Override
    public void startPublish(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }

    @Override
    public void publish(PublishPayload payload) {
        final WaitDispatchMainTaskReduceEvent event = new WaitDispatchMainTaskReduceEvent(payload.getMainTaskId());
        logger.info("publish, main:{}", event.getMainTaskId());
        this.ageiPort.getLocalEventBus().post(event);
    }

    @Override
    public Class<WaitDispatchMainTaskReduceEvent> publishEventType() {
        return WaitDispatchMainTaskReduceEvent.class;
    }
}
