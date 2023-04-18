package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.common.function.Builder;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.executor.DataMergeExecutor;
import com.alibaba.ageiport.processor.core.executor.MainWorkerExecutor;
import com.alibaba.ageiport.processor.core.executor.SubWorkerExecutor;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
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
import java.util.Map;

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

    default void onError(MainTask mainTask, Throwable throwable) {
        try {
            LOGGER.error("onError, main:{}", mainTask.getMainTaskId(), throwable);
            AgeiPort ageiPort = this;
            ageiPort.getMainTaskCallback().beforeError(mainTask);
            if (throwable != null) {
                mainTask.setResultMessage(throwable.getMessage());
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
        } catch (Throwable e) {
            LOGGER.error("onError failed, ", e);
        }

    }
}
