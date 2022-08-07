package com.alibaba.ageiport.processor.core.spi.dispatcher;

/**
 * @author lingyi
 */
public interface Dispatcher {

    void dispatchMainTaskPrepare(RootDispatcherContext context);

    void dispatchSubTasks(SubDispatcherContext context);

    void dispatchMainTaskReduce(RootDispatcherContext context);
}
