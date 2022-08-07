package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;

/**
 * @author lingyi
 */
public interface MainTaskWorker extends Runnable {

    MainTask getMainTask();

    AgeiPort getAgei();

    boolean isReduce();


    void isReduce(boolean isReduce);


    void doPrepare();

    void doReduce();


    @Override
    default void run() {
        if (isReduce()) {
            doReduce();
        } else {
            doPrepare();
        }

    }
}
