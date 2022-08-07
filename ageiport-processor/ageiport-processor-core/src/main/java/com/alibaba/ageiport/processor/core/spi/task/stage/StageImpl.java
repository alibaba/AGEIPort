package com.alibaba.ageiport.processor.core.spi.task.stage;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.EventObject;
import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class StageImpl implements Stage {

    private String group;

    private Integer order;

    private String code;

    private String name;

    private Double minPercent;

    private Double maxPercent;

    private String description;

    private Class<? extends EventObject> triggerEvent;

    private StageProvider stageProvider;

    private boolean isFinal;

    private boolean isFinished;

    private boolean isError;

    public StageImpl() {
    }

    public StageImpl(String group, Integer order, String code, String name, Double minPercent, Double maxPercent, String description) {
        this.group = group;
        this.order = order;
        this.code = code;
        this.name = name;
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
        this.description = description;
    }

    public StageImpl(String group, Integer order, String code, String name, Double minPercent, Double maxPercent, String description, boolean isFinal, boolean isFinished, boolean isError) {
        this.group = group;
        this.order = order;
        this.code = code;
        this.name = name;
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
        this.description = description;
        this.isFinal = isFinal;
        this.isFinished = isFinished;
        this.isError = isError;
    }

    public StageImpl(String group, Integer order, String code, String name, Double minPercent, Double maxPercent, String description, Class<? extends EventObject> triggerEvent) {
        this.group = group;
        this.order = order;
        this.code = code;
        this.name = name;
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
        this.description = description;
        this.triggerEvent = triggerEvent;
    }

    @Override
    public Stage next() {
        if (isFinal) {
            return null;
        }

        List<Stage> stages = stageProvider.getStages();
        int current = -1;
        for (int i = 0; i < stages.size(); i++) {
            final Stage stage = stages.get(i);
            if (stage.getCode().equals(code)) {
                current = i;
            }
        }
        if (current < 0 || current >= stages.size() - 1) {
            return null;
        }
        return stages.get(current + 1);
    }

    @Override
    public Stage pre() {
        List<Stage> stages = stageProvider.getStages();
        int current = -1;
        for (int i = 0; i < stages.size(); i++) {
            final Stage stage = stages.get(i);
            if (stage.getCode().equals(code)) {
                current = i;
            }
        }
        if (current <= 0 || current >= stages.size()) {
            return null;
        }
        return stages.get(current - 1);
    }

    @Override
    public boolean isAfterThan(String code) {
        Stage stage = getStageProvider().getStage(code);
        return this.getOrder() > stage.getOrder();
    }

    @Override
    public boolean isBeforeThan(String code) {
        Stage stage = getStageProvider().getStage(code);
        return this.getOrder() < stage.getOrder();
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public int compareTo(@NotNull Stage o) {
        return this.getOrder().compareTo(o.getOrder());
    }
}
