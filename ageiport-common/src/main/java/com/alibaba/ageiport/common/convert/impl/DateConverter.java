package com.alibaba.ageiport.common.convert.impl;

import com.alibaba.ageiport.common.convert.AbstractConverter;
import com.alibaba.ageiport.common.utils.DateUtils;
import com.alibaba.ageiport.common.utils.StringUtils;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换器
 *
 * @author Looly
 */
public class DateConverter extends AbstractConverter<Date> {
    private static final long serialVersionUID = 1L;

    private Class<? extends Date> targetType;
    /**
     * 日期格式化
     */
    private String format;

    /**
     * 构造
     *
     * @param targetType 目标类型
     */
    public DateConverter(Class<? extends Date> targetType) {
        this.targetType = targetType;
    }

    /**
     * 构造
     *
     * @param targetType 目标类型
     * @param format     日期格式
     */
    public DateConverter(Class<? extends Date> targetType, String format) {
        this.targetType = targetType;
        this.format = format;
    }

    /**
     * 获取日期格式
     *
     * @return 设置日期格式
     */
    public String getFormat() {
        return format;
    }

    /**
     * 设置日期格式
     *
     * @param format 日期格式
     */
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    protected Date convertInternal(Object value) {
        Long mills = null;
        if (value instanceof Calendar) {
            // Handle Calendar
            mills = ((Calendar) value).getTimeInMillis();
        } else if (value instanceof Number) {
            // Handle Number
            mills = ((Number) value).longValue();
        } else if (value instanceof TemporalAccessor) {
            return DateUtils.date((TemporalAccessor) value);
        } else {
            // 统一按照字符串处理
            final String valueStr = convertToStr(value);
            Date date = null;
            try {
                date = StringUtils.isBlank(this.format)
                    ? DateUtils.parse(valueStr)
                    : DateUtils.parse(valueStr, this.format);
            } catch (Throwable e) {
                // Ignore Exception
            }
            if (null != date) {
                mills = date.getTime();
            }
        }

        if (null == mills) {
            return null;
        }

        // 返回指定类型
        if (Date.class == targetType) {
            return new Date(mills);
        } else if (java.sql.Date.class == targetType) {
            return new java.sql.Date(mills);
        } else if (java.sql.Time.class == targetType) {
            return new java.sql.Time(mills);
        } else if (java.sql.Timestamp.class == targetType) {
            return new java.sql.Timestamp(mills);
        }

        throw new UnsupportedOperationException(StringUtils.format("Unsupport Date type: {}", this.targetType.getName()));
    }


}
