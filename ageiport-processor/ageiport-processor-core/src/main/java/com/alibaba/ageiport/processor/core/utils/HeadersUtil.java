package com.alibaba.ageiport.processor.core.utils;

import com.alibaba.ageiport.common.utils.TypeUtils;
import com.alibaba.ageiport.processor.core.annotation.ViewField;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeadersImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lingyi
 */
public class HeadersUtil {
    public static <T> ColumnHeaders buildHeaders(BizColumnHeaders bizColumnHeaders,
                                                 Class<T> viewClass,
                                                 BizDynamicColumnHeaders bizDynamicColumnHeaders) {
        if (bizColumnHeaders != null) {
            return buildHeaders(bizColumnHeaders, bizDynamicColumnHeaders);
        } else {
            return buildHeaders(viewClass, bizDynamicColumnHeaders);
        }
    }

    private static ColumnHeaders buildHeaders(BizColumnHeaders bizColumnHeaders,
                                              BizDynamicColumnHeaders bizDynamicColumnHeaders) {
        List<BizColumnHeader> bizColumnHeaderList = bizColumnHeaders.getBizColumnHeaders();

        List<ColumnHeader> columnHeaders = new ArrayList<>(bizColumnHeaderList.size());

        int index = 1;
        for (BizColumnHeader bizColumnHeader : bizColumnHeaderList) {
            if (bizColumnHeader.isDynamicColumn()) {
                String fieldName = bizColumnHeader.getFieldName();
                BizDynamicColumnHeader bizDynamicColumnHeader = bizDynamicColumnHeaders.getBizDynamicColumnHeaderByFiledName(fieldName);
                List<BizColumnHeader> flatColumnHeaders = bizDynamicColumnHeader.getFlatColumnHeaders();
                for (BizColumnHeader flatColumnHeader : flatColumnHeaders) {
                    ColumnHeaderImpl columnHeader = new ColumnHeaderImpl();
                    columnHeader.setFieldName(flatColumnHeader.getFieldName());
                    columnHeader.setHeaderName(flatColumnHeader.getHeaderName());
                    columnHeader.setType(flatColumnHeader.getType());
                    columnHeader.setIndex(index);
                    columnHeader.setDynamicColumn(true);
                    columnHeader.setDynamicColumnKey(flatColumnHeader.getDynamicColumnKey());
                    columnHeader.setGroupIndex(flatColumnHeader.getGroupIndex());
                    columnHeader.setGroupName(flatColumnHeader.getGroupName());
                    columnHeader.setErrorHeader(flatColumnHeader.isErrorHeader());
                    columnHeader.setRequired(flatColumnHeader.isRequired());
                    columnHeaders.add(columnHeader);
                    index++;
                }
            } else {
                ColumnHeaderImpl columnHeader = new ColumnHeaderImpl();
                columnHeader.setFieldName(bizColumnHeader.getFieldName());
                columnHeader.setHeaderName(bizColumnHeader.getHeaderName());
                columnHeader.setType(bizColumnHeader.getDataType());
                columnHeader.setDynamicColumn(false);
                columnHeader.setIndex(index);
                columnHeader.setGroupIndex(bizColumnHeader.getGroupIndex());
                columnHeader.setGroupName(bizColumnHeader.getGroupName());
                columnHeader.setErrorHeader(bizColumnHeader.isErrorHeader());
                columnHeader.setRequired(bizColumnHeader.isRequired());
                columnHeaders.add(columnHeader);
                index++;
            }
        }

        ColumnHeadersImpl headers = new ColumnHeadersImpl(columnHeaders);
        return headers;
    }

    private static <T> ColumnHeaders buildHeaders(Class<T> viewClass,
                                                  BizDynamicColumnHeaders bizDynamicColumnHeaders) {
        List<Field> fields = resolveTargetFields(viewClass);

        List<Field> dataFields = fields.stream()
                .filter(f -> Objects.nonNull(f.getAnnotation(ViewField.class)))
                .sorted((left, right) -> {
                    ViewField lAnnotation = left.getAnnotation(ViewField.class);
                    ViewField rAnnotation = right.getAnnotation(ViewField.class);
                    return Integer.compare(lAnnotation.index(), rAnnotation.index());
                }).collect(Collectors.toList());

        List<ColumnHeader> columnHeaders = new ArrayList<>(dataFields.size());
        int index = 1;
        for (Field dataField : dataFields) {
            ViewField viewField = dataField.getAnnotation(ViewField.class);
            if (viewField.isDynamicColumn()) {
                String fieldName = dataField.getName();
                BizDynamicColumnHeader bizDynamicColumnHeader = bizDynamicColumnHeaders.getBizDynamicColumnHeaderByFiledName(fieldName);
                List<BizColumnHeader> flatColumnHeaders = bizDynamicColumnHeader.getFlatColumnHeaders();
                for (BizColumnHeader flatColumnHeader : flatColumnHeaders) {
                    ColumnHeaderImpl columnHeader = new ColumnHeaderImpl();
                    columnHeader.setFieldName(flatColumnHeader.getFieldName());
                    columnHeader.setHeaderName(flatColumnHeader.getHeaderName());
                    columnHeader.setType(flatColumnHeader.getType());
                    columnHeader.setIndex(index);
                    columnHeader.setDynamicColumn(true);
                    columnHeader.setDynamicColumnKey(flatColumnHeader.getDynamicColumnKey());
                    columnHeader.setGroupIndex(flatColumnHeader.getGroupIndex());
                    columnHeader.setGroupName(flatColumnHeader.getGroupName());
                    columnHeader.setErrorHeader(flatColumnHeader.isErrorHeader());
                    columnHeader.setRequired(flatColumnHeader.isRequired());
                    columnHeaders.add(columnHeader);
                    index++;
                }
            } else {
                ColumnHeaderImpl columnHeader = new ColumnHeaderImpl();
                columnHeader.setFieldName(dataField.getName());
                columnHeader.setHeaderName(viewField.headerName());
                columnHeader.setType(viewField.type());
                columnHeader.setIndex(index);
                columnHeader.setDynamicColumn(false);
                columnHeader.setGroupIndex(viewField.groupIndex());
                columnHeader.setGroupName(viewField.groupName());
                columnHeader.setErrorHeader(viewField.isErrorHeader());
                columnHeader.setRequired(viewField.isRequired());
                columnHeaders.add(columnHeader);
                index++;
            }
        }
        ColumnHeadersImpl headers = new ColumnHeadersImpl(columnHeaders);
        return headers;
    }

    private static List<Field> resolveTargetFields(Class targetClass) {
        List<Field> declaredFields = TypeUtils.getDeclaredFields(targetClass);
        List<Field> targetFields = new ArrayList<>(declaredFields.size());
        for (Field declaredField : declaredFields) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
                targetFields.add(declaredField);
            }
        }
        return targetFields;
    }
}
