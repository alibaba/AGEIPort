package com.alibaba.ageiport.processor.core.task.importer.stage;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.StageImpl;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public class ImportSubTaskStageProvider implements SubTaskStageProvider {

    private static String GROUP = "ImportSubTaskStage";

    public static Stage S01_CREATED = new StageImpl(GROUP, 100, "S01_CREATED", "子任务已创建", 0.10, 0.10, "");

    public static Stage S02_DISPATCH_ON_NODE = new StageImpl(GROUP, 200, "S02_DISPATCH_ON_NODE", "子任务分发至节点", 0.15, 0.15, "");

    public static Stage S03_EXECUTE_START = new StageImpl(GROUP, 300, "S03_EXECUTE_START", "子任务执行-开始", 0.20, 0.20, "");

    public static Stage S04_EXECUTE_GET_SLICE_DATA_START = new StageImpl(GROUP, 400, "S04_EXECUTE_GET_SLICE_DATA_START", "获取分片数据-开始", 0.21, 0.21, "");


    public static Stage S05_EXECUTE_GET_SLICE_DATA_END = new StageImpl(GROUP, 500, "S05_EXECUTE_GET_SLICE_DATA_END", "获取分片数据-结束", 0.22, 0.22, "");

    public static Stage S06_CHECK_HEADERS_START = new StageImpl(GROUP, 550, "S06_CHECK_HEADERS_START", "检查表头-开始", 0.21, 0.21, "");


    public static Stage S07_CHECK_HEADERS_END = new StageImpl(GROUP, 551, "S07_CHECK_HEADERS_END", "检查表头-结束", 0.22, 0.22, "");

    public static Stage S08_EXECUTE_BIZ_DATA_GROUP_START = new StageImpl(GROUP, 600, "S08_EXECUTE_BIZ_DATA_GROUP_START", "转换BizDataGroup-开始", 0.21, 0.21, "");


    public static Stage S09_EXECUTE_BIZ_DATA_GROUP_END = new StageImpl(GROUP, 700, "S07_EXECUTE_BIZ_DATA_GROUP_END", "转换BizDataGroup-结束", 0.22, 0.22, "");

    public static Stage S10_EXECUTE_FLAT_START = new StageImpl(GROUP, 800, "S08_EXECUTE_FLAT_START", "BizDataGroup打平-开始", 0.23, 0.23, "");

    public static Stage S11_EXECUTE_FLAT_END = new StageImpl(GROUP, 900, "S09_EXECUTE_FLAT_END", "BizDataGroup打平-结束", 0.24, 0.24, "");

    public static Stage S12_EXECUTE_CONVERT_AND_CHECK_START = new StageImpl(GROUP, 1000, "S10_EXECUTE_CONVERT_AND_CHECK_START", "convertAndCheck-开始", 0.25, 0.25, "");

    public static Stage S13_EXECUTE_CONVERT_AND_CHECK_END = new StageImpl(GROUP, 1100, "S11_EXECUTE_CONVERT_AND_CHECK_END", "convertAndCheck-结束", 0.50, 0.50, "");

    public static Stage S14_EXECUTE_WRITE_START = new StageImpl(GROUP, 1200, "S12_EXECUTE_WRITE_START", "write-开始", 0.51, 0.51, "");

    public static Stage S15_EXECUTE_WRITE_END = new StageImpl(GROUP, 1300, "S13_EXECUTE_WRITE_END", "write-结束", 0.91, 0.91, "");

    public static Stage S16_EXECUTE_GROUP_START = new StageImpl(GROUP, 1400, "S14_EXECUTE_GROUP_START", "group-开始", 0.90, 0.90, "");

    public static Stage S17_EXECUTE_GROUP_END = new StageImpl(GROUP, 1500, "S15_EXECUTE_GROUP_END", "group-结束", 0.91, 0.91, "");

    public static Stage S18_EXECUTE_DATA_GROUP_START = new StageImpl(GROUP, 1600, "S16_EXECUTE_DATA_GROUP_START", "转换为DataGroup-开始", 0.92, 0.92, "");

    public static Stage S19_EXECUTE_DATA_GROUP_END = new StageImpl(GROUP, 1700, "S17_EXECUTE_DATA_GROUP_END", "转换为DataGroup-结束", 0.93, 0.93, "");

    public static Stage S20_SAVE_DATA_START = new StageImpl(GROUP, 1800, "S18_SAVE_DATA_START", "存储数据-开始", 0.94, 0.94, "");


    public static Stage S21_SAVE_DATA_END = new StageImpl(GROUP, 1900, "S19_SAVE_DATA_END", "存储数据-结束", 0.99, 0.99, "");


    public static Stage S22_FINISHED = new StageImpl(GROUP, 2000, CommonStage.FINISHED.getCode(), "子任务已完成", 1.00, 1.00, "", true, true, false);


    public static Stage S23_ERROR = new StageImpl(GROUP, 2100, CommonStage.ERROR.getCode(), "子任务失败", 1.00, 1.00, "", true, false, true);


    List<Stage> TASK_STAGES = Lists.newArrayList(
            S01_CREATED
            , S02_DISPATCH_ON_NODE
            , S03_EXECUTE_START
            , S04_EXECUTE_GET_SLICE_DATA_START
            , S05_EXECUTE_GET_SLICE_DATA_END
            , S06_CHECK_HEADERS_START
            , S07_CHECK_HEADERS_END
            , S08_EXECUTE_BIZ_DATA_GROUP_START
            , S09_EXECUTE_BIZ_DATA_GROUP_END
            , S10_EXECUTE_FLAT_START
            , S11_EXECUTE_FLAT_END
            , S12_EXECUTE_CONVERT_AND_CHECK_START
            , S13_EXECUTE_CONVERT_AND_CHECK_END
            , S14_EXECUTE_WRITE_START
            , S15_EXECUTE_WRITE_END
            , S16_EXECUTE_GROUP_START
            , S17_EXECUTE_GROUP_END
            , S18_EXECUTE_DATA_GROUP_START
            , S19_EXECUTE_DATA_GROUP_END
            , S20_SAVE_DATA_START
            , S21_SAVE_DATA_END
            , S22_FINISHED
            , S23_ERROR

    );


    private Map<String, Stage> codeIndexMap = new HashMap<>();
    private Map<Integer, Stage> orderIndexMap = new HashMap<>();

    public ImportSubTaskStageProvider() {
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
        return S22_FINISHED;
    }

    @Override
    public Stage subTaskError() {
        return S23_ERROR;
    }
}
