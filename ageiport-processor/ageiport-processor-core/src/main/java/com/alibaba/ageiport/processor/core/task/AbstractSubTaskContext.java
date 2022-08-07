package com.alibaba.ageiport.processor.core.task;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskContext;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author lingyi
 */
@Getter
@Setter
public abstract class AbstractSubTaskContext<QUERY, DATA, VIEW> implements SubTaskContext {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private QUERY query;

    private AgeiPort ageiPort;

    private MainTask mainTask;

    private SubTask subTask;

    private TaskSpec taskSpec;

    private BizUser bizUser;

    private Stage stage;

    private SubTaskStageProvider subTaskStageProvider;

    private Map<String, Long> stageTimestampMap = new TreeMap<>();

    @Override
    public Long getStageTimestamp(String code) {
        return stageTimestampMap.get(code);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stageTimestampMap.put(stage.getCode(), System.currentTimeMillis());
    }
}
