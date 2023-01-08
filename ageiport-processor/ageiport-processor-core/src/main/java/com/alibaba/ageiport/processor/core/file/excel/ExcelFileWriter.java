package com.alibaba.ageiport.processor.core.file.excel;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.io.FastByteArrayOutputStream;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.constants.ConstValues;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.ageiport.processor.core.spi.file.FileWriter;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.WriteContext;
import com.alibaba.excel.enums.WriteTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.commons.compress.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lingyi
 */
public class ExcelFileWriter implements FileWriter {

    private AgeiPort ageiPort;

    private ColumnHeaders columnHeaders;

    private ExcelWriter excelWriter;

    private FileContext fileContext;

    private List<WriteHandler> writeHandlers;

    public ExcelFileWriter(AgeiPort ageiPort, ColumnHeaders columnHeaders, FileContext fileContext) {

        this.ageiPort = ageiPort;
        this.columnHeaders = columnHeaders;
        this.fileContext = fileContext;
        this.writeHandlers = new ArrayList<>();

        AgeiPortOptions options = ageiPort.getOptions();
        Map<String, Map<String, String>> spiConfigs = options.getSpiConfigs();
        Map<String, String> providerConfigs = spiConfigs.get("ExcelWriteHandlerProvider");
        String configAsJson = JsonUtil.toJsonString(providerConfigs);
        ExcelWriteHandlerProviderSpiConfig providerSpiConfig = JsonUtil.toObject(configAsJson, ExcelWriteHandlerProviderSpiConfig.class);

        for (String extensionName : providerSpiConfig.getExtensionNames()) {
            ExtensionLoader<ExcelWriteHandlerProvider> extensionLoader = ExtensionLoader.getExtensionLoader(ExcelWriteHandlerProvider.class);
            ExcelWriteHandlerProvider handlerProvider = extensionLoader.getExtension(extensionName);
            this.writeHandlers.addAll(handlerProvider.provide(ageiPort, columnHeaders, fileContext));
        }


        Integer sheetNo = ConstValues.DEFAULT_SHEET_NO;
        String sheetName = ConstValues.DEFAULT_SHEET_NAME;

        List<List<String>> head = columnHeaders.getColumnHeaders().stream()
                .filter(s -> !s.getIgnoreHeader())
                .map(s -> Lists.newArrayList(s.getHeaderName()))
                .collect(Collectors.toList());

        ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet()
                .sheetNo(sheetNo)
                .sheetName(sheetName)
                .needHead(true)
                .head(head);

        for (WriteHandler writeHandler : this.writeHandlers) {
            sheetBuilder.registerWriteHandler(writeHandler);
        }

        WriteSheet writeSheet = sheetBuilder.build();

        FastByteArrayOutputStream output = new FastByteArrayOutputStream(10240);
        this.excelWriter = EasyExcel.write(output).build();
        excelWriter.writeContext().currentSheet(writeSheet, WriteTypeEnum.ADD);

    }

    @Override
    public void write(DataGroup fileData) {
        WriteSheet writeSheet = excelWriter.writeContext().writeSheetHolder().getWriteSheet();
        for (DataGroup.Data datum : fileData.getData()) {
            List<List<Object>> lines = resolve(columnHeaders, datum);
            excelWriter.write(lines, writeSheet);
        }
    }

    @Override
    public InputStream finish() {
        WriteContext writeContext = excelWriter.writeContext();
        FastByteArrayOutputStream outputStream = (FastByteArrayOutputStream) writeContext.writeWorkbookHolder().getOutputStream();
        excelWriter.finish();
        return outputStream.getInputStream();
    }


    @Override
    public void close() {
        IOUtils.closeQuietly(excelWriter);
    }

    List<List<Object>> resolve(ColumnHeaders columnHeaders, DataGroup.Data groupData) {
        List<DataGroup.Item> items = groupData.getItems();
        List<List<Object>> data = new ArrayList<>(items.size());
        for (DataGroup.Item item : items) {
            Map<String, Object> values = item.getValues();
            List<Object> result = new ArrayList<>(values.size());
            for (ColumnHeader columnHeader : columnHeaders.getColumnHeaders()) {
                if (columnHeader.getIgnoreHeader()) {
                    continue;
                }
                String fieldName = columnHeader.getFieldName();
                Object value = values.get(fieldName);
                if (columnHeader.getDynamicColumn()) {
                    Map map = (Map) value;
                    Object o = map.get(columnHeader.getDynamicColumnKey());
                    result.add(o);
                } else {
                    result.add(value);
                }
            }
            data.add(result);
        }
        return data;
    }
}
