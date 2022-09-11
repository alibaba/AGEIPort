package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.common.function.Builder;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.executor.DataMergeExecutor;
import com.alibaba.ageiport.processor.core.executor.MainWorkerExecutor;
import com.alibaba.ageiport.processor.core.executor.SubWorkerExecutor;
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
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.security.Security;

public interface AgeiPort {

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
}
