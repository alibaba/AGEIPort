package com.alibaba.ageiport.processor.core.task.exporter.stage;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.StageImpl;
import com.alibaba.ageiport.processor.core.task.event.WaitDispatchMainTaskPrepareEvent;
import com.alibaba.ageiport.processor.core.task.event.WaitDispatchMainTaskReduceEvent;
import com.alibaba.ageiport.processor.core.task.event.WaitDispatchSubTaskEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public class ExportMainTaskStageProvider implements MainTaskStageProvider {

    public static String S01_CREATED_CODE = "S01_CREATED";
    public static String S02_DISPATCH_MAIN_TASK_START_CODE = "S02_DISPATCH_MAIN_TASK_START";
    public static String S03_DISPATCH_MAIN_TASK_END_CODE = "S03_DISPATCH_MAIN_TASK_END";
    public static String S04_EXECUTE_START_CODE = "S04_EXECUTE_START";
    public static String S05_TASK_RUNTIME_CONFIG_START_CODE = "S05_TASK_RUNTIME_CONFIG_START";
    public static String S06_TASK_RUNTIME_CONFIG_END_CODE = "S06_TASK_RUNTIME_CONFIG_END";
    public static String S07_RESET_QUERY_START_CODE = "S07_RESET_QUERY_START";
    public static String S08_RESET_QUERY_END_CODE = "S08_RESET_QUERY_END";
    public static String S09_TOTAL_COUNT_START_CODE = "S09_TOTAL_COUNT_START";
    public static String S10_TOTAL_COUNT_END_CODE = "S10_TOTAL_COUNT_END";
    public static String S11_GET_HEADERS_START_CODE = "S11_GET_HEADERS_START";
    public static String S12_GET_HEADERS_END_CODE = "S12_GET_HEADERS_END";
    public static String S13_GET_DYNAMIC_HEADERS_START_CODE = "S13_GET_DYNAMIC_HEADERS_START";
    public static String S14_GET_DYNAMIC_HEADERS_END_CODE = "S14_GET_DYNAMIC_HEADERS_END";
    public static String S15_TASK_SLICE_START_CODE = "S15_TASK_SLICE_START";
    public static String S16_TASK_SLICE_END_CODE = "S16_TASK_SLICE_END";
    public static String S17_SAVE_SUB_TASK_START_CODE = "S17_SAVE_SUB_TASK_START";
    public static String S18_SAVE_SUB_TASK_END_CODE = "S18_SAVE_SUB_TASK_END";
    public static String S19_DISPATCH_SUB_TASK_START_CODE = "S19_DISPATCH_SUB_TASK_START";
    public static String S20_DISPATCH_SUB_TASK_END_CODE = "S20_DISPATCH_SUB_TASK_END";
    public static String S21_EXECUTE_SUB_TASKS_START_CODE = "S21_EXECUTE_SUB_TASKS_START";
    public static String S22_EXECUTE_SUB_TASKS_END_CODE = "S22_EXECUTE_SUB_TASKS_END";
    public static String S23_WRITE_FILE_START_CODE = "S23_WRITE_FILE_START";
    public static String S24_WRITE_FILE_END_CODE = "S24_WRITE_FILE_END";
    public static String S25_SAVE_FILE_START_CODE = "S25_SAVE_FILE_START";
    public static String S26_SAVE_FILE_END_CODE = "S26_SAVE_FILE_END";

    private static String GROUP = "ExportMainTaskStage";
    Stage S01_CREATED = new StageImpl(GROUP, 100, S01_CREATED_CODE, "主任务已创建", 0.01, 0.01, "", WaitDispatchMainTaskPrepareEvent.class);

    Stage S02_DISPATCH_MAIN_TASK_START = new StageImpl(GROUP, 200, S02_DISPATCH_MAIN_TASK_START_CODE, "分发主任务-开始", 0.02, 0.02, "");

    Stage S03_DISPATCH_MAIN_TASK_END = new StageImpl(GROUP, 300, S03_DISPATCH_MAIN_TASK_END_CODE, "分发主任务-结束", 0.03, 0.03, "");

    Stage S04_EXECUTE_START = new StageImpl(GROUP, 400, S04_EXECUTE_START_CODE, "主任务开始执行", 0.04, 0.04, "");

    Stage S05_TASK_RUNTIME_CONFIG_START = new StageImpl(GROUP, 500, S05_TASK_RUNTIME_CONFIG_START_CODE, "获取运行时配置-开始", 0.05, 0.05, "");

    Stage S06_TASK_RUNTIME_CONFIG_END = new StageImpl(GROUP, 600, S06_TASK_RUNTIME_CONFIG_END_CODE, "获取运行时配置-结束", 0.06, 0.06, "");

    Stage S07_RESET_QUERY_START = new StageImpl(GROUP, 700, S07_RESET_QUERY_START_CODE, "重置Query-开始", 0.07, 0.07, "");

    Stage S08_RESET_QUERY_END = new StageImpl(GROUP, 800, S08_RESET_QUERY_END_CODE, "重置Query-结束", 0.08, 0.08, "");

    Stage S09_TOTAL_COUNT_START = new StageImpl(GROUP, 900, S09_TOTAL_COUNT_START_CODE, "获取数据总量-开始", 0.09, 0.09, "");

    Stage S10_TOTAL_COUNT_END = new StageImpl(GROUP, 1000, S10_TOTAL_COUNT_END_CODE, "获取数据总量-结束", 0.10, 0.10, "");

    Stage S11_GET_HEADERS_START = new StageImpl(GROUP, 1100, S11_GET_HEADERS_START_CODE, "获取表头-开始", 0.11, 0.11, "");

    Stage S12_GET_HEADERS_END = new StageImpl(GROUP, 1200, S12_GET_HEADERS_END_CODE, "获取表头-结束", 0.12, 0.12, "");

    Stage S13_GET_DYNAMIC_HEADERS_START = new StageImpl(GROUP, 1300, S13_GET_DYNAMIC_HEADERS_START_CODE, "获取动态表头-开始", 0.13, 0.13, "");

    Stage S14_GET_DYNAMIC_HEADERS_END = new StageImpl(GROUP, 1400, S14_GET_DYNAMIC_HEADERS_END_CODE, "获取动态表头-结束", 0.14, 0.14, "");

    Stage S15_TASK_SLICE_START = new StageImpl(GROUP, 1500, S15_TASK_SLICE_START_CODE, "执行分片策略-开始", 0.15, 0.15, "");

    Stage S16_TASK_SLICE_END = new StageImpl(GROUP, 1600, S16_TASK_SLICE_END_CODE, "执行分片策略-结束", 0.16, 0.16, "");

    Stage S17_SAVE_SUB_TASK_START = new StageImpl(GROUP, 1700, S17_SAVE_SUB_TASK_START_CODE, "持久化子任务-开始", 0.17, 0.17, "");

    Stage S18_SAVE_SUB_TASK_END = new StageImpl(GROUP, 1800, S18_SAVE_SUB_TASK_END_CODE, "持久化子任务-结束", 0.18, 0.18, "", WaitDispatchSubTaskEvent.class);

    Stage S19_DISPATCH_SUB_TASK_START = new StageImpl(GROUP, 1900, S19_DISPATCH_SUB_TASK_START_CODE, "分发子任务-开始", 0.19, 0.19, "");

    Stage S20_DISPATCH_SUB_TASK_END = new StageImpl(GROUP, 2000, S20_DISPATCH_SUB_TASK_END_CODE, "分发子任务-结束", 0.20, 0.20, "");

    Stage S21_EXECUTE_SUB_TASKS_START = new StageImpl(GROUP, 2100, S21_EXECUTE_SUB_TASKS_START_CODE, "执行子任务-开始", 0.21, 0.21, "");

    Stage S22_EXECUTE_SUB_TASKS_END = new StageImpl(GROUP, 2200, S22_EXECUTE_SUB_TASKS_END_CODE, "执行子任务-结束", 0.90, 0.90, "", WaitDispatchMainTaskReduceEvent.class);

    Stage S23_WRITE_FILE_START = new StageImpl(GROUP, 2300, S23_WRITE_FILE_START_CODE, "执行合并数据写文件-开始", 0.91, 0.91, "");

    Stage S24_WRITE_FILE_END = new StageImpl(GROUP, 2400, S24_WRITE_FILE_END_CODE, "执行合并数据写文件-结束", 0.95, 0.95, "");

    Stage S25_SAVE_FILE_START = new StageImpl(GROUP, 2500, S25_SAVE_FILE_START_CODE, "执行存储文件-开始", 0.96, 0.96, "");

    Stage S26_SAVE_FILE_END = new StageImpl(GROUP, 2600, S26_SAVE_FILE_END_CODE, "执行存储文件-结束", 0.99, 0.99, "");

    Stage S27_FINISHED = new StageImpl(GROUP, 2700, CommonStage.FINISHED.getCode(), "主任务执行完成", 1.00, 1.00, "", true, true, false);

    Stage S28_ERROR = new StageImpl(GROUP, 2700, CommonStage.ERROR.getCode(), "主任务执行失败", 1.00, 1.00, "", true, false, true);

    List<Stage> TASK_STAGES = Lists.newArrayList(
            S01_CREATED

            , S02_DISPATCH_MAIN_TASK_START

            , S03_DISPATCH_MAIN_TASK_END

            , S04_EXECUTE_START

            , S05_TASK_RUNTIME_CONFIG_START

            , S06_TASK_RUNTIME_CONFIG_END

            , S07_RESET_QUERY_START

            , S08_RESET_QUERY_END

            , S09_TOTAL_COUNT_START

            , S10_TOTAL_COUNT_END

            , S11_GET_HEADERS_START

            , S12_GET_HEADERS_END

            , S13_GET_DYNAMIC_HEADERS_START

            , S14_GET_DYNAMIC_HEADERS_END

            , S15_TASK_SLICE_START

            , S16_TASK_SLICE_END

            , S17_SAVE_SUB_TASK_START

            , S18_SAVE_SUB_TASK_END

            , S19_DISPATCH_SUB_TASK_START

            , S20_DISPATCH_SUB_TASK_END

            , S21_EXECUTE_SUB_TASKS_START

            , S22_EXECUTE_SUB_TASKS_END

            , S23_WRITE_FILE_START

            , S24_WRITE_FILE_END

            , S25_SAVE_FILE_START

            , S26_SAVE_FILE_END

            , S27_FINISHED

            , S28_ERROR
    );


    private Map<String, Stage> codeIndexMap = new HashMap<>();
    private Map<Integer, Stage> orderIndexMap = new HashMap<>();

    public ExportMainTaskStageProvider() {
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
    public Stage mainTaskCreated() {
        return S01_CREATED;
    }

    @Override
    public Stage mainTaskDispatchStart() {
        return S02_DISPATCH_MAIN_TASK_START;
    }

    @Override
    public Stage mainTaskDispatchEnd() {
        return S03_DISPATCH_MAIN_TASK_END;
    }

    @Override
    public Stage mainTaskStart() {
        return S04_EXECUTE_START;
    }

    @Override
    public Stage mainTaskSliceStart() {
        return S15_TASK_SLICE_START;
    }

    @Override
    public Stage mainTaskSliceEnd() {
        return S16_TASK_SLICE_END;
    }

    @Override
    public Stage mainTaskSaveSliceStart() {
        return S17_SAVE_SUB_TASK_START;
    }

    @Override
    public Stage mainTaskSaveSliceEnd() {
        return S18_SAVE_SUB_TASK_END;
    }

    @Override
    public Stage subTaskDispatchStart() {
        return S19_DISPATCH_SUB_TASK_START;
    }

    @Override
    public Stage subTaskDispatchEnd() {
        return S20_DISPATCH_SUB_TASK_END;
    }

    @Override
    public Stage subTaskExecuteStart() {
        return S21_EXECUTE_SUB_TASKS_START;
    }

    @Override
    public Stage subTaskExecuteEnd() {
        return S22_EXECUTE_SUB_TASKS_END;
    }

    @Override
    public Stage mainTaskReduceStart() {
        return S23_WRITE_FILE_START;
    }

    @Override
    public Stage mainTaskReduceEnd() {
        return S26_SAVE_FILE_END;
    }

    @Override
    public Stage mainTaskFinished() {
        return S27_FINISHED;
    }

    @Override
    public Stage mainTaskError() {
        return S28_ERROR;
    }


}
