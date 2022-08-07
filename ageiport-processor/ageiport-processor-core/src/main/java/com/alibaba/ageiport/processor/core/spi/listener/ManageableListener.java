package com.alibaba.ageiport.processor.core.spi.listener;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.eventbus.Listener;

import java.util.EventObject;

/**
 * @author lingyi
 */
@SPI
public interface ManageableListener<E extends EventObject> extends Listener<E> {
    void startListen(AgeiPort ageiPort);

}
