package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.publisher.ManageablePublisher;
import com.alibaba.ageiport.processor.core.spi.publisher.PublishPayload;

/**
 * @author lingyi
 */
public class WaitDispatchSubTaskEventPublisher implements ManageablePublisher<WaitDispatchSubTaskEvent> {

    public static Logger logger = LoggerFactory.getLogger(WaitDispatchSubTaskEventPublisher.class);


    private AgeiPort ageiPort;

    @Override
    public void startPublish(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }

    @Override
    public void publish(PublishPayload payload) {
        final WaitDispatchSubTaskEvent event = new WaitDispatchSubTaskEvent(payload.getMainTaskId());
        logger.info("publish, main:{}", event.getMainTaskId(), event.getMainTaskId());
        this.ageiPort.getLocalEventBus().post(event);
    }

    @Override
    public Class<WaitDispatchSubTaskEvent> publishEventType() {
        return WaitDispatchSubTaskEvent.class;
    }
}
