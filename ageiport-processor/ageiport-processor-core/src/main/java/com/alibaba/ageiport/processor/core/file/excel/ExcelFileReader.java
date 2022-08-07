package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileReader;
import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author lingyi
 */
public class ExcelFileReader implements FileReader {

    private AgeiPort ageiPort;

    private MainTask mainTask;

    private ColumnHeaders columnHeaders;

    private List<EasyExcelReadListener> readListeners;

    public ExcelFileReader(AgeiPort ageiPort, MainTask mainTask, ColumnHeaders columnHeaders) {
        this.ageiPort = ageiPort;
        this.mainTask = mainTask;
        this.columnHeaders = columnHeaders;
        this.readListeners = Lists.newArrayList(new EasyExcelReadListener(ageiPort, mainTask, columnHeaders));

    }

    @Override
    public void read(InputStream inputStream) {
        for (EasyExcelReadListener readListener : readListeners) {
            EasyExcel.read(inputStream, readListener)
                    .readCache(new MapCache())
                    .headRowNumber(1)
                    .sheet()
                    .doRead();
        }

    }

    @Override
    public DataGroup finish() {
        DataGroup dataGroup = new DataGroup();
        List<DataGroup.Data> data = new ArrayList<>();
        for (EasyExcelReadListener listener : readListeners) {
            DataGroup.Data uploadData = listener.getUploadData();
            if (uploadData != null) {
                data.add(uploadData);
            }
        }
        dataGroup.setData(data);
        return dataGroup;
    }

    @Override
    public void close() {

    }


    @Getter
    @Setter
    public static class EasyExcelReadListener extends AnalysisEventListener<Map<Integer, Object>> {

        Logger log = LoggerFactory.getLogger(TaskContext.class);

        private AgeiPort ageiPort;

        private MainTask mainTask;

        private ColumnHeaders columnHeaders;

        private List<Map<Integer, String>> uploadHeaders = new ArrayList<>(4);

        private Map<Integer, String> lastHeadMap = new HashMap<>();


        private DataGroup.Data uploadData;

        private CountDownLatch countDownLatch;

        public EasyExcelReadListener(AgeiPort ageiPort, MainTask mainTask, ColumnHeaders columnHeaders) {
            this.ageiPort = ageiPort;
            this.mainTask = mainTask;
            this.columnHeaders = columnHeaders;
            this.countDownLatch = new CountDownLatch(1);
        }

        @Override
        public void invoke(Map<Integer, Object> data, AnalysisContext context) {
            Map<String, Object> line = new HashMap<>(data.size() * 2);

            for (Map.Entry<Integer, Object> entry : data.entrySet()) {
                Integer column = entry.getKey();
                String header = lastHeadMap.get(column);
                ColumnHeader columnHeader = columnHeaders.getColumnHeaderByHeaderName(header);
                if (columnHeader != null) {
                    String fieldName = columnHeader.getFieldName();
                    if (columnHeader.getDynamicColumn()) {
                        Object o = line.get(fieldName);
                        if (o == null) {
                            o = new HashMap<>();
                            line.put(fieldName, o);
                        } else {
                            if (o instanceof Map) {
                                Map map = (Map) o;
                                map.put(columnHeader.getDynamicColumnKey(), entry.getValue());
                            }
                        }
                    } else {
                        line.put(fieldName, entry.getValue());
                    }
                }
            }
            DataGroup.Item item = new DataGroup.Item();
            String sheetName = context.readSheetHolder().getSheetName();
            String sheetNo = context.readSheetHolder().getSheetNo().toString();
            Integer rowIndex = context.readRowHolder().getRowIndex();
            String groupName = sheetName + "-" + sheetNo;
            String code = groupName + "-" + rowIndex;
            item.setCode(code);
            item.setValues(line);

            if (uploadData == null) {
                uploadData = new DataGroup.Data();
                uploadData.setName(groupName);
                uploadData.setItems(new ArrayList<>());
            }
            uploadData.getItems().add(item);
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            uploadHeaders.add(headMap);
            lastHeadMap.putAll(headMap);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            List<ColumnHeader> columnHeaderList = columnHeaders.getColumnHeaders();
            List<String> headers = new ArrayList<>(lastHeadMap.values());
            List<String> lostHeaders = new ArrayList<>();
            for (ColumnHeader columnHeader : columnHeaderList) {
                if (columnHeader.getRequired() && !columnHeader.getIgnoreHeader() && !columnHeader.getErrorHeader()) {
                    if (!headers.contains(columnHeader.getHeaderName())) {
                        lostHeaders.add(columnHeader.getHeaderName());
                    }
                }
            }
            if (!lostHeaders.isEmpty()) {
                throw new IllegalArgumentException("no header:" + JsonUtil.toJsonString(lostHeaders));
            }
            countDownLatch.countDown();
        }

        public DataGroup.Data getData() {
            try {
                this.countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return uploadData;
        }
    }
}
