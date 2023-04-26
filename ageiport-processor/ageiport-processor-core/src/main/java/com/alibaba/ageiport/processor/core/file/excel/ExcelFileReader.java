package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.ageiport.processor.core.spi.file.FileReader;
import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
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

    public static Logger logger = LoggerFactory.getLogger(ExcelFileReader.class);

    private AgeiPort ageiPort;

    private ColumnHeaders columnHeaders;

    private List<EasyExcelReadListener> readListeners;

    private FileContext fileContext;

    public ExcelFileReader(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {
        this.ageiPort = ageiPort;
        this.columnHeaders = columnHeaders;
        this.readListeners = new ArrayList<>();
        this.fileContext = fileContext;
    }

    @Override
    public void read(InputStream inputStream) {
        ExcelTypeEnum excelTypeEnum = ExcelWriteHandlerProvider.getExcelTypeEnum(fileContext);
        ExcelReader excelReader = EasyExcel.read(inputStream).excelType(excelTypeEnum).build();
        List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();

        List<ReadSheet> sheetsNeedRead = new ArrayList<>();

        int sheetIndex = 0;
        for (ReadSheet readSheet : readSheets) {
            String sheetName = readSheet.getSheetName();
            if (sheetName != null && sheetName.startsWith("hidden_")) {
                logger.warn("ignore sheet, main:{}, sheetNo:{}, sheetName:{}", fileContext.getMainTask(), readSheet.getSheetNo(), sheetName);
                continue;
            }
            EasyExcelReadListener readListener = new EasyExcelReadListener(ageiPort, fileContext, columnHeaders);
            readListeners.add(readListener);
            readSheet.setCustomReadListenerList(Lists.newArrayList(readListener));
            readSheet.setHeadRowNumber(columnHeaders.getHeaderRowCount(sheetIndex));
            sheetsNeedRead.add(readSheet);
            sheetIndex++;
        }

        excelReader.read(sheetsNeedRead);
    }

    @Override
    public DataGroup finish() {
        DataGroup dataGroup = new DataGroup();
        List<DataGroup.Data> data = new ArrayList<>();
        for (EasyExcelReadListener listener : readListeners) {
            DataGroup.Data uploadData = listener.getData();
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

        private FileContext fileContext;

        private ColumnHeaders columnHeaders;

        private Map<Integer, String> uploadHeaderNameKeys = new HashMap<>(4);

        private DataGroup.Data uploadData;

        private CountDownLatch countDownLatch;

        public EasyExcelReadListener(AgeiPort ageiPort, FileContext fileContext, ColumnHeaders columnHeaders) {
            this.ageiPort = ageiPort;
            this.columnHeaders = columnHeaders;
            this.fileContext = fileContext;
            this.countDownLatch = new CountDownLatch(1);
        }

        @Override
        public void invoke(Map<Integer, Object> data, AnalysisContext context) {
            Map<String, Object> line = new HashMap<>(data.size() * 2);

            for (Map.Entry<Integer, Object> entry : data.entrySet()) {
                Integer column = entry.getKey();
                String headerNameKey = uploadHeaderNameKeys.get(column);
                ColumnHeader columnHeader = columnHeaders.getColumnHeaderByHeaderNameKey(headerNameKey);
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
            String groupName = sheetName + "@" + sheetNo;
            String code = groupName + "@" + rowIndex;
            item.setCode(code);
            item.setValues(line);

            if (this.uploadData == null) {
                this.uploadData = new DataGroup.Data();
                this.uploadData.setCode(groupName);
                this.uploadData.setItems(new ArrayList<>());
                Map<String, String> meta = new HashMap<>();
                meta.put(ExcelConstants.sheetNoKey, sheetNo);
                meta.put(ExcelConstants.sheetNameKey, sheetName);
                this.uploadData.setMeta(meta);
            }

            this.uploadData.getItems().add(item);
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
                Integer key = entry.getKey();
                String headerNameKey = uploadHeaderNameKeys.get(key);
                if (headerNameKey == null) {
                    headerNameKey = entry.getValue();
                } else {
                    headerNameKey = headerNameKey + ColumnHeader.headerSplit + entry.getValue();
                }
                uploadHeaderNameKeys.put(key, headerNameKey);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
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
