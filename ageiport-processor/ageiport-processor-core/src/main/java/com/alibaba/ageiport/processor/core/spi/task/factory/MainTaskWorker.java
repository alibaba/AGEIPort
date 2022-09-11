package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.constants.MainTaskFeatureKeys;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;

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

    default void onError(Throwable e) {
        MainTask mainTask = getMainTask();
        AgeiPort ageiPort = getAgeiPort();
        ageiPort.getMainTaskCallback().beforeError(mainTask);
        if (e != null) {
            mainTask.setResultMessage(e.getMessage());
        } else {
            mainTask.setResultMessage("exception is null");
        }
        mainTask.setGmtFinished(new Date());
        mainTask.setStatus(TaskStatus.ERROR);
        ageiPort.getTaskServerClient().updateMainTask(mainTask);
        if (ExecuteType.STANDALONE.equals(mainTask.getExecuteType())) {
            ageiPort.getLocalEventBus().post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), CommonStage.ERROR));
        } else {
            ageiPort.getClusterEventBus().post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), CommonStage.ERROR));
        }
        ageiPort.getMainTaskCallback().afterError(mainTask);
    }

    default void onFinished(TaskContext context) {
        getAgeiPort().getMainTaskCallback().beforeFinished(getMainTask());
        MainTask contextMainTask = context.getMainTask();
        contextMainTask.setStatus(TaskStatus.FINISHED);
        contextMainTask.setSubSuccessCount(contextMainTask.getSubTotalCount());
        contextMainTask.setGmtFinished(new Date());
        contextMainTask.setDataSuccessCount(contextMainTask.getDataTotalCount());
        contextMainTask.setDataProcessedCount(contextMainTask.getDataTotalCount());
        context.save();
        getAgeiPort().getMainTaskCallback().afterFinished(getMainTask());
    }
}
