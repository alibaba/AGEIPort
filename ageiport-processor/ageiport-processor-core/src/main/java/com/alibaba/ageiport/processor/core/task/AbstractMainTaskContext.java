package com.alibaba.ageiport.processor.core.task;


import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContext;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author lingyi
 */
@Getter
@Setter
public abstract class AbstractMainTaskContext implements MainTaskContext {

    private AgeiPort ageiPort;

    private MainTask mainTask;

    private TaskSpec taskSpec;

    private BizUser bizUser;

    private Stage stage;

    private MainTaskStageProvider mainTaskStageProvider;

    private Map<String, Long> stageTimestampMap = new TreeMap<>();

    @Override
    public Long getStageTimestamp(String code) {
        return this.stageTimestampMap.get(code);
    }

    @Override
    public SubTask getSubTask() {
        return null;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stageTimestampMap.put(stage.getCode(), System.currentTimeMillis());
    }
}
