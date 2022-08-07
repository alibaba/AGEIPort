package com.alibaba.ageiport.processor.core.task.exporter.stage;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.StageImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportSubTaskStageProvider implements SubTaskStageProvider {

    private static String GROUP = "ExportSubTaskStage";

    Stage S01_CREATED = new StageImpl(GROUP, 100, "S01_CREATED", "子任务已创建", 0.10, 0.10, "");

    Stage S02_DISPATCH_ON_NODE = new StageImpl(GROUP, 200, "S02_DISPATCH_ON_NODE", "子任务分发至节点", 0.15, 0.15, "");

    Stage S03_EXECUTE_START = new StageImpl(GROUP, 300, "S03_EXECUTE_START", "子任务执行-开始", 0.20, 0.20, "");

    Stage S04_EXECUTE_QUERY_DATA_START = new StageImpl(GROUP, 400, "S04_EXECUTE_QUERY_DATA_START", "查询数据-开始", 0.21, 0.21, "");


    Stage S05_EXECUTE_QUERY_DATA_END = new StageImpl(GROUP, 500, "S05_EXECUTE_QUERY_DATA_END", "查询数据-结束", 0.70, 0.70, "");

    Stage S06_EXECUTE_CONVERT_START = new StageImpl(GROUP, 600, "S06_EXECUTE_CONVERT_START", "转换数据-开始", 0.71, 0.71, "");

    Stage S07_EXECUTE_CONVERT_END = new StageImpl(GROUP, 700, "S07_EXECUTE_CONVERT_END", "转换数据-结束", 0.85, 0.85, "");

    Stage S08_EXECUTE_GROUP_START = new StageImpl(GROUP, 800, "S08_EXECUTE_GROUP_START", "视图分组-开始", 0.87, 0.87, "");

    Stage S09_EXECUTE_GROUP_END = new StageImpl(GROUP, 900, "S09_EXECUTE_GROUP_END", "视图分组-结束", 0.89, 0.89, "");

    Stage S10_EXECUTE_TO_MAP_START = new StageImpl(GROUP, 1000, "S10_EXECUTE_TO_MAP_START", "转换为DataSet-开始", 0.90, 0.90, "");

    Stage S11_EXECUTE_TO_MAP_END = new StageImpl(GROUP, 1100, "S11_EXECUTE_TO_MAP_END", "转换为DataSet-结束", 0.91, 0.91, "");

    Stage S12_SAVE_DATA_START = new StageImpl(GROUP, 1200, "S12_SAVE_DATA_START", "存储数据-开始", 0.92, 0.92, "");


    Stage S13_SAVE_DATA_END = new StageImpl(GROUP, 1300, "S13_SAVE_DATA_END", "存储数据-结束", 0.99, 0.99, "");


    Stage S14_FINISHED = new StageImpl(GROUP, 1400, CommonStage.FINISHED.getCode(), "子任务已完成", 1.00, 1.00, "", true, true, false);


    Stage S14_ERROR = new StageImpl(GROUP, 1400, CommonStage.ERROR.getCode(), "子任务失败", 1.00, 1.00, "", true, false, true);


    List<Stage> TASK_STAGES = Lists.newArrayList(
            S01_CREATED

            , S02_DISPATCH_ON_NODE

            , S03_EXECUTE_START

            , S04_EXECUTE_QUERY_DATA_START

            , S05_EXECUTE_QUERY_DATA_END

            , S06_EXECUTE_CONVERT_START

            , S07_EXECUTE_CONVERT_END

            , S08_EXECUTE_GROUP_START

            , S09_EXECUTE_GROUP_END

            , S10_EXECUTE_TO_MAP_START

            , S11_EXECUTE_TO_MAP_END

            , S12_SAVE_DATA_START

            , S13_SAVE_DATA_END

            , S14_FINISHED

            , S14_ERROR

    );


    private Map<String, Stage> codeIndexMap = new HashMap<>();
    private Map<Integer, Stage> orderIndexMap = new HashMap<>();

    public ExportSubTaskStageProvider() {
        Collections.sort(TASK_STAGES);
        for (Stage stage : TASK_STAGES) {
            ((StageImpl) stage).setStageProvider(this);
            codeIndexMap.put(stage.getCode(), stage);
            orderIndexMap.put(stage.getOrder(), stage);
        }
    }

    @Override
    public List<Stage> getStages() {
        return TASK_STAGES;
    }

    @Override
    public Stage getStage(String code) {
        return codeIndexMap.get(code);
    }

    @Override
    public Stage getStage(Integer order) {
        return orderIndexMap.get(order);
    }

    @Override
    public Stage subTaskCreated() {
        return S01_CREATED;
    }

    @Override
    public Stage subTaskDispatchedOnNode() {
        return S02_DISPATCH_ON_NODE;
    }

    @Override
    public Stage subTaskStart() {
        return S03_EXECUTE_START;
    }

    @Override
    public Stage subTaskFinished() {
        return S14_FINISHED;
    }

    @Override
    public Stage subTaskError() {
        return S14_ERROR;
    }
}
