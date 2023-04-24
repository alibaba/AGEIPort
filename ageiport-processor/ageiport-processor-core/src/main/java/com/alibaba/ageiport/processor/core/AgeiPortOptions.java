package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.NetUtils;
import com.alibaba.ageiport.ext.file.store.FileStoreOptions;
import com.alibaba.ageiport.processor.core.api.http.HttpApiServerOptions;
import com.alibaba.ageiport.processor.core.client.http.HttpTaskServerClientOptions;
import com.alibaba.ageiport.processor.core.client.memory.MemoryTaskServerClientOptions;
import com.alibaba.ageiport.processor.core.cluster.DefaultClusterOptions;
import com.alibaba.ageiport.processor.core.constants.ConstValues;
import com.alibaba.ageiport.processor.core.dispatcher.http.HttpDispatcherOptions;
import com.alibaba.ageiport.processor.core.dispatcher.local.LocalDispatcherOptions;
import com.alibaba.ageiport.processor.core.eventbus.http.HttpEventBusOptions;
import com.alibaba.ageiport.processor.core.eventbus.local.LocalEventBusOptions;
import com.alibaba.ageiport.processor.core.file.excel.ExcelWriteHandlerProviderSpiConfig;
import com.alibaba.ageiport.processor.core.file.store.LocalFileStoreOptions;
import com.alibaba.ageiport.processor.core.spi.api.ApiServerOptions;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientOptions;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterOptions;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherOptions;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingyi
 */
@ToString
@Getter
@Setter
public class AgeiPortOptions {

    private String factory = "AgeiPortFactoryImpl";

    private String namespace;

    private String app;

    private String accessKeyId;

    private String accessKeySecret;

    private ClusterOptions clusterOptions = new DefaultClusterOptions();

    private FileStoreOptions fileStoreOptions = new LocalFileStoreOptions();

    private TaskServerClientOptions taskServerClientOptions = new HttpTaskServerClientOptions();

    private ApiServerOptions apiServerOptions = new HttpApiServerOptions();

    private SubWorkerExecutor subWorkerExecutor = new SubWorkerExecutor();

    private MainWorkerExecutor mainWorkerExecutor = new MainWorkerExecutor();

    private DataMergeExecutor dataMergeExecutor = new DataMergeExecutor();

    private Security security = new Security();

    private Local local = new Local();

    private Cluster cluster = new Cluster();

    private String localBigDataCache = "LocalMemoryBigDataCache";

    private String clusterBigDataCache = "FileStoreBigDataCache";

    private Integer bigDataCacheExpireMs = ConstValues.DEFAULT_MAX_TASK_TIMEOUT;

    private String taskAcceptor = "DefaultTaskAcceptorFactory";

    private String mainTaskCallback = "DefaultMainTaskCallback";

    private Map<String, String> spiSelectMappings = new HashMap<>();

    private Map<String, Map<String, String>> spiConfigs = new HashMap<>();
    private Map<String, String> fileTypeWriterSpiMappings = new HashMap<>();
    private Map<String, String> fileTypeReaderSpiMappings = new HashMap<>();


    public AgeiPortOptions() {
        fileTypeWriterSpiMappings.put("xlsx", "ExcelFileWriterFactory");
        fileTypeReaderSpiMappings.put("xlsx", "ExcelFileReaderFactory");

        ExcelWriteHandlerProviderSpiConfig excelWriteHandlerProviderSpiConfig = new ExcelWriteHandlerProviderSpiConfig();
        spiConfigs.put("ExcelWriteHandlerProvider", JsonUtil.toMap(excelWriteHandlerProviderSpiConfig));
    }

    @Getter
    @Setter
    @ToString
    public static class SubWorkerExecutor {
        private String name = "SubWorker";
        private int corePoolSize = 4;
        private int maxPoolSize = 8;
        private int queueSize = 128;
        private int timeoutMs = 1000 * 60 * 30;
    }

    @Getter
    @Setter
    @ToString
    public static class MainWorkerExecutor {
        private String name = "MainWorker";
        private int corePoolSize = 4;
        private int maxPoolSize = 8;
        private int queueSize = 128;
        private int timeoutMs = 1000 * 60 * 30;
    }

    @Getter
    @Setter
    @ToString
    public static class DataMergeExecutor {
        private String name = "MergeWorker";
        private int corePoolSize = 4;
        private int maxPoolSize = 8;
        private int queueSize = 16;
        private int timeoutMs = 1000 * 60 * 30;
    }

    @Getter
    @Setter
    @ToString
    public static class Security {
        private String signerName = "HmacSHA256";
        private String signerComposerName = "DefaultSignerComposer";
    }

    @Getter
    @Setter
    @ToString
    public static class EventBus {

        private String clusterEventBus = "HttpEventBus";

        private String localEventBus = "LocalAsyncEventBus";
    }

    @Getter
    @Setter
    @ToString
    public static class Cluster {

        private EventBusOptions eventBusOptions = new HttpEventBusOptions();

        private DispatcherOptions dispatcherOptions = new HttpDispatcherOptions();
    }

    @Getter
    @Setter
    @ToString
    public static class Local {

        private EventBusOptions eventBusOptions = new LocalEventBusOptions();

        private DispatcherOptions dispatcherOptions = new LocalDispatcherOptions();
    }

    @Getter
    @Setter
    @ToString
    private static class Debug {

        private String namespace = "namespace";

        private String app = "app";

        private String accessKeyId = "accessKeyId";

        private String accessKeySecret = "accessKeySecret";

        private FileStoreOptions fileStoreOptions = new LocalFileStoreOptions();

        private TaskServerClientOptions taskServerClientOptions = new MemoryTaskServerClientOptions();
    }

    public static AgeiPortOptions debug() {
        AgeiPortOptions options = new AgeiPortOptions();
        Debug debug = new AgeiPortOptions.Debug();
        if (debug.getNamespace() != null) {
            options.setNamespace(debug.getNamespace());
        }
        if (debug.getApp() != null) {
            options.setApp(debug.getApp());
        }
        if (debug.getAccessKeyId() != null) {
            options.setAccessKeyId(debug.getAccessKeyId());
        }
        if (debug.getAccessKeySecret() != null) {
            options.setAccessKeySecret(debug.getAccessKeySecret());
        }
        if (debug.getFileStoreOptions() != null) {
            options.setFileStoreOptions(debug.getFileStoreOptions());
        }
        if (debug.getTaskServerClientOptions() != null) {
            HttpTaskServerClientOptions clientOptions = new HttpTaskServerClientOptions();
            boolean portAvailable = NetUtils.isPortAvailable(clientOptions.getPort());
            if (portAvailable) {
                clientOptions.setEndpoint("localhost");
                options.setTaskServerClientOptions(clientOptions);
            } else {
                options.setTaskServerClientOptions(debug.getTaskServerClientOptions());
            }
        }
        return options;
    }
}
