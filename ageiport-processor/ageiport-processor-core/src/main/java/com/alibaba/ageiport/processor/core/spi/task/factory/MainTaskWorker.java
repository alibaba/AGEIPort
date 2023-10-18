package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;

import java.util.Date;

/**
 * @author lingyi
 */
public interface MainTaskWorker extends Runnable {

    MainTask getMainTask();

    AgeiPort getAgeiPort();

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

    default void onFinished(TaskContext context) {
        getAgeiPort().getMainTaskCallback().beforeFinished(getMainTask());
        MainTask contextMainTask = context.getMainTask();
        contextMainTask.setStatus(TaskStatus.FINISHED);
        contextMainTask.setSubSuccessCount(contextMainTask.getSubTotalCount());
        contextMainTask.setSubFinishedCount(contextMainTask.getSubTotalCount());
        contextMainTask.setGmtFinished(new Date());
        contextMainTask.setDataSuccessCount(contextMainTask.getDataTotalCount());
        contextMainTask.setDataProcessedCount(contextMainTask.getDataTotalCount());
        context.save();
        getAgeiPort().getMainTaskCallback().afterFinished(getMainTask());
    }
}
