package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.common.function.Builder;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.ext.file.store.FileStoreFactory;
import com.alibaba.ageiport.ext.file.store.FileStoreOptions;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.executor.DataMergeExecutor;
import com.alibaba.ageiport.processor.core.executor.MainWorkerExecutor;
import com.alibaba.ageiport.processor.core.executor.SubWorkerExecutor;
import com.alibaba.ageiport.processor.core.spi.api.ApiServer;
import com.alibaba.ageiport.processor.core.spi.api.ApiServerFactory;
import com.alibaba.ageiport.processor.core.spi.api.ApiServerOptions;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCacheManager;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientFactory;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientOptions;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManagerFactory;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterOptions;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherManager;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusManager;
import com.alibaba.ageiport.processor.core.spi.listener.ListenerManager;
import com.alibaba.ageiport.processor.core.spi.publisher.PublisherManager;
import com.alibaba.ageiport.processor.core.spi.service.TaskService;
import com.alibaba.ageiport.processor.core.spi.service.TaskServiceImpl;
import com.alibaba.ageiport.processor.core.spi.task.acceptor.TaskAcceptor;
import com.alibaba.ageiport.processor.core.spi.task.acceptor.TaskAcceptorFactory;
import com.alibaba.ageiport.processor.core.spi.task.callback.MainTaskCallback;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressMonitor;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressService;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.task.monitor.TaskProgressMonitorImpl;
import com.alibaba.ageiport.processor.core.task.monitor.TaskProgressServiceImpl;
import com.alibaba.ageiport.processor.core.task.registry.SpecificationRegistryImpl;
import com.alibaba.ageiport.processor.core.task.selector.TaskSpiSelectorImpl;
import com.alibaba.ageiport.security.Security;
import com.alibaba.ageiport.security.SecurityImpl;
import com.alibaba.ageiport.security.auth.CredentialsProvider;
import com.alibaba.ageiport.security.auth.Signer;
import com.alibaba.ageiport.security.auth.SignerComposer;
import com.alibaba.ageiport.security.auth.defaults.DefaultCredentials;
import com.alibaba.ageiport.security.auth.defaults.DefaultCredentialsProvider;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingyi
 */
@Getter
public class AgeiPortImpl implements AgeiPort {

    private AgeiPortOptions options;

    private Security security;

    private FileStore fileStore;

    private TaskService taskService;

    private TaskServerClient taskServerClient;

    private MainWorkerExecutor mainWorkerExecutor;

    private SubWorkerExecutor subWorkerExecutor;

    private DataMergeExecutor dataMergeExecutor;

    private TaskSpiSelector taskSpiSelector;

    private TaskSpecificationRegistry specificationRegistry;

    private EventBusManager eventBusManager;

    private ListenerManager listenerManager;

    private PublisherManager publisherManager;

    private TaskProgressMonitor taskProgressMonitor;

    private TaskProgressService taskProgressService;

    private BigDataCacheManager bigDataCacheManager;

    private DispatcherManager dispatcherManager;

    private TaskAcceptor taskAcceptor;

    private ClusterManager clusterManager;

    private ApiServer apiServer;

    private MainTaskCallback mainTaskCallback;


    private AgeiPortImpl(AgeiPortOptions options) {
        this.options = options;
    }

    static AgeiPortImpl agei(AgeiPortOptions options) {
        AgeiPortImpl ageiPort = new AgeiPortImpl(options);
        ageiPort.init();
        return ageiPort;
    }

    @Override
    public EventBus getLocalEventBus() {
        return this.getEventBusManager().getEventBus(ExecuteType.STANDALONE);
    }

    @Override
    public EventBus getClusterEventBus() {
        return this.getEventBusManager().getEventBus(ExecuteType.CLUSTER);
    }


    private Map<String, Object> beans = new HashMap<>();

    @Override
    public <T, P> T getBean(Class<T> clazz, Builder<T, P> builder, P param) {
        Object o = beans.get(clazz.getName());
        if (o == null) {
            if (builder == null) {
                return null;
            } else {
                synchronized (this) {
                    o = beans.get(clazz.getName());
                    if (o == null) {
                        o = builder.build(param);
                        setBean(o);
                    }
                }
            }
        }
        return (T) o;
    }

    @Override
    public <T> T setBean(T t) {
        beans.put(t.getClass().getName(), t);
        return t;
    }


    private void init() {

        AgeiPortOptions.Security security = this.options.getSecurity();
        Signer signer = ExtensionLoader.getExtensionLoader(Signer.class).getExtension(security.getSignerName());
        SignerComposer signerComposer = ExtensionLoader.getExtensionLoader(SignerComposer.class).getExtension(security.getSignerComposerName());
        CredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        credentialsProvider.setCredentials(new DefaultCredentials(options.getAccessKeyId(), options.getAccessKeySecret()));
        SecurityImpl securityImpl = new SecurityImpl();
        securityImpl.setSigner(signer);
        securityImpl.setSignerComposer(signerComposer);
        securityImpl.setCredentialsProvider(credentialsProvider);
        this.security = securityImpl;

        ClusterOptions clusterOptions = this.options.getClusterOptions();
        String clusterType = clusterOptions.type();
        ClusterManagerFactory clusterManagerFactory = ExtensionLoader.getExtensionLoader(ClusterManagerFactory.class).getExtension(clusterType);
        this.clusterManager = clusterManagerFactory.create(this, clusterOptions);

        FileStoreOptions fileStoreOptions = this.options.getFileStoreOptions();
        String fileStoreType = fileStoreOptions.type();
        FileStoreFactory fileStoreFactory = ExtensionLoader.getExtensionLoader(FileStoreFactory.class).getExtension(fileStoreType);
        this.fileStore = fileStoreFactory.create(fileStoreOptions);

        this.taskService = new TaskServiceImpl(this);

        TaskServerClientOptions taskServerClientOptions = this.options.getTaskServerClientOptions();
        String taskServerClientType = taskServerClientOptions.type();
        TaskServerClientFactory taskServerClientFactory = ExtensionLoader.getExtensionLoader(TaskServerClientFactory.class).getExtension(taskServerClientType);
        this.taskServerClient = taskServerClientFactory.taskServerClient(this, taskServerClientOptions);

        AgeiPortOptions.MainWorkerExecutor mainWorkerExecutor = this.options.getMainWorkerExecutor();
        this.mainWorkerExecutor = new MainWorkerExecutor(mainWorkerExecutor);

        AgeiPortOptions.SubWorkerExecutor subWorkerExecutor = this.options.getSubWorkerExecutor();
        this.subWorkerExecutor = new SubWorkerExecutor(subWorkerExecutor);

        AgeiPortOptions.DataMergeExecutor dataMergeExecutor = this.options.getDataMergeExecutor();
        this.dataMergeExecutor = new DataMergeExecutor(dataMergeExecutor);

        this.taskSpiSelector = new TaskSpiSelectorImpl(options.getSpiSelectMappings());

        this.specificationRegistry = new SpecificationRegistryImpl(this);

        this.taskProgressMonitor = new TaskProgressMonitorImpl(this);
        this.taskProgressService = new TaskProgressServiceImpl(this);

        this.eventBusManager = new EventBusManager(this);
        this.listenerManager = new ListenerManager(this);
        this.publisherManager = new PublisherManager(this);

        this.bigDataCacheManager = new BigDataCacheManager(this);

        this.dispatcherManager = new DispatcherManager(this);

        this.taskAcceptor = ExtensionLoader.getExtensionLoader(TaskAcceptorFactory.class).getExtension(this.options.getTaskAcceptor()).create(this);

        ApiServerOptions apiServerOptions = options.getApiServerOptions();
        String apiServerType = apiServerOptions.type();
        ApiServerFactory apiServerFactory = ExtensionLoader.getExtensionLoader(ApiServerFactory.class).getExtension(apiServerType);
        this.apiServer = apiServerFactory.create(this, apiServerOptions);

        this.mainTaskCallback = ExtensionLoader.getExtensionLoader(MainTaskCallback.class).getExtension(this.options.getMainTaskCallback());
    }
}
