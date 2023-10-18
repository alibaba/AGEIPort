package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.common.function.Builder;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.executor.DataMergeExecutor;
import com.alibaba.ageiport.processor.core.executor.MainWorkerExecutor;
import com.alibaba.ageiport.processor.core.executor.SubWorkerExecutor;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.AgeiPortFactory;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCacheManager;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherManager;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusManager;
import com.alibaba.ageiport.processor.core.spi.listener.ListenerManager;
import com.alibaba.ageiport.processor.core.spi.publisher.PublisherManager;
import com.alibaba.ageiport.processor.core.spi.service.TaskService;
import com.alibaba.ageiport.processor.core.spi.task.acceptor.TaskAcceptor;
import com.alibaba.ageiport.processor.core.spi.task.callback.MainTaskCallback;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressMonitor;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressService;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import com.alibaba.ageiport.security.Security;

import java.util.Date;

public interface AgeiPort {

    static Logger LOGGER = LoggerFactory.getLogger(AgeiPort.class);

    static AgeiPort ageiPort() {
        AgeiPortOptions options = new AgeiPortOptions();
        AgeiPortFactory factory = ExtensionLoader.getExtensionLoader(AgeiPortFactory.class).getOrDefaultExtension(options.getFactory());
        return factory.ageiPort(options);
    }

    static AgeiPort ageiPort(AgeiPortOptions options) {
        AgeiPortFactory factory = ExtensionLoader.getExtensionLoader(AgeiPortFactory.class).getOrDefaultExtension(options.getFactory());
        return factory.ageiPort(options);
    }

    AgeiPortOptions getOptions();

    Security getSecurity();

    FileStore getFileStore();

    TaskService getTaskService();

    TaskServerClient getTaskServerClient();

    MainWorkerExecutor getMainWorkerExecutor();

    SubWorkerExecutor getSubWorkerExecutor();

    DataMergeExecutor getDataMergeExecutor();

    TaskSpiSelector getTaskSpiSelector();

    TaskSpecificationRegistry getSpecificationRegistry();

    EventBusManager getEventBusManager();

    PublisherManager getPublisherManager();

    ListenerManager getListenerManager();

    BigDataCacheManager getBigDataCacheManager();


    TaskProgressMonitor getTaskProgressMonitor();

    TaskProgressService getTaskProgressService();

    EventBus getLocalEventBus();

    EventBus getClusterEventBus();

    DispatcherManager getDispatcherManager();

    TaskAcceptor getTaskAcceptor();

    ClusterManager getClusterManager();

    MainTaskCallback getMainTaskCallback();

    <T, P> T getBean(Class<T> clazz, Builder<T, P> builder, P param);

    <T> T setBean(T t);

    default void onMainError(String mainTaskId, Throwable throwable) {
        MainTask mainTask = this.getTaskServerClient().getMainTask(mainTaskId);
        onMainError(mainTask, throwable);
    }
    default void onSubError(String subTaskId, Throwable throwable) {
        SubTask subTask = this.getTaskServerClient().getSubTask(subTaskId);
        onSubError(subTask, throwable);
    }

    default void onMainError(MainTask mainTask, Throwable throwable) {
        try {
            LOGGER.error("onError MainTask, main:{}", mainTask.getMainTaskId(), throwable);
            AgeiPort ageiPort = this;
            ageiPort.getMainTaskCallback().beforeError(mainTask);
            mainTask.setGmtFinished(new Date());
            mainTask.setStatus(TaskStatus.ERROR);
            if (mainTask.getResultMessage() == null) {
                if (throwable != null) {
                    mainTask.setResultMessage(throwable.getMessage());
                    if (throwable instanceof BizException) {
                        BizException bizException = (BizException) throwable;
                        mainTask.setResultCode(bizException.getErrorCode());
                    }
                }
            }
            ageiPort.getTaskServerClient().updateMainTask(mainTask);
            if (ExecuteType.STANDALONE.equals(mainTask.getExecuteType())) {
                ageiPort.getLocalEventBus().post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), CommonStage.ERROR));
            } else {
                ageiPort.getClusterEventBus().post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), CommonStage.ERROR));
            }
            ageiPort.getMainTaskCallback().afterError(mainTask);
        } catch (Throwable e) {
            LOGGER.error("onError MainTask failed, ", e);
        }
    }

    default void onSubError(SubTask subTask, Throwable throwable) {
        try {
            LOGGER.error("onError SubTask, main:{}, sub:{}", subTask.getMainTaskId(), subTask.getSubTaskNo(), throwable);
            AgeiPort ageiPort = this;
            subTask.setGmtFinished(new Date());
            subTask.setStatus(TaskStatus.ERROR);
            if (subTask.getResultMessage() == null) {
                subTask.setResultMessage(throwable.getMessage());
                if (throwable instanceof BizException) {
                    BizException bizException = (BizException) throwable;
                    subTask.setResultCode(bizException.getErrorCode());
                }
            }
            ageiPort.getTaskServerClient().updateSubTask(subTask);
            if (ExecuteType.STANDALONE.equals(subTask.getExecuteType())) {
                ageiPort.getLocalEventBus().post(TaskStageEvent.subTaskEvent(subTask.getSubTaskId(), CommonStage.ERROR));
            } else {
                ageiPort.getClusterEventBus().post(TaskStageEvent.subTaskEvent(subTask.getSubTaskId(), CommonStage.ERROR));
            }
        } catch (Throwable e) {
            LOGGER.error("onError SubTask failed, ", e);
        }
    }
}
