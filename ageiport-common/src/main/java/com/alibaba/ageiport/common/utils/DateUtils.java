package com.alibaba.ageiport.common.utils;

import com.alibaba.ageiport.common.exception.UtilException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author xuechao.sxc
 */
public class DateUtils {

    /**
     * 标准日期格式：yyyyMMdd
     */
    public final static String PURE_DATE_PATTERN = "yyyyMMdd";
    public final static SimpleDateFormat PURE_DATE_FORMAT = new SimpleDateFormat(PURE_DATE_PATTERN);

    /**
     * 标准日期格式：HHmmss
     */
    public final static String PURE_TIME_PATTERN = "HHmmss";
    public final static SimpleDateFormat PURE_TIME_FORMAT = new SimpleDateFormat(PURE_TIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMddHHmmss
     */
    public final static String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    public final static SimpleDateFormat PURE_DATETIME_FORMAT = new SimpleDateFormat(PURE_DATETIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMddHHmmssSSS
     */
    public final static String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
    public final static SimpleDateFormat PURE_DATETIME_MS_FORMAT = new SimpleDateFormat(PURE_DATETIME_MS_PATTERN);


    /**
     * 标准日期格式：yyyy-MM-dd
     */
    public final static String NORM_DATE_PATTERN = "yyyy-MM-dd";
    public final static SimpleDateFormat NORM_DATE_FORMAT = new SimpleDateFormat(NORM_DATE_PATTERN);

    /**
     * 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm
     */
    public final static String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public final static SimpleDateFormat NORM_DATETIME_MINUTE_FORMAT = new SimpleDateFormat(NORM_DATETIME_MINUTE_PATTERN);

    /**
     * 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss
     */
    public final static String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static SimpleDateFormat NORM_DATETIME_FORMAT = new SimpleDateFormat(NORM_DATETIME_PATTERN);

    /**
     * {@link TemporalAccessor}类型时间转为{@link Date}<br>
     * 始终根据已有{@link TemporalAccessor} 产生新的{@link Date}对象
     *
     * @param temporalAccessor {@link TemporalAccessor}
     * @return 时间对象
     */
    public static Date date(TemporalAccessor temporalAccessor) {
        Instant instant = toInstant(temporalAccessor);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        Instant result;
        if (temporalAccessor instanceof Instant) {
            result = (Instant) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
        } else {
            result = Instant.from(temporalAccessor);
        }

        return result;
    }

    /**
     * 将日期字符串转换为{@link Date}对象，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd HH:mm:ss</li>
     * <li>yyyy/MM/dd HH:mm:ss</li>
     * <li>yyyy.MM.dd HH:mm:ss</li>
     * <li>yyyy年MM月dd日 HH时mm分ss秒</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy.MM.dd</li>
     * <li>HH:mm:ss</li>
     * <li>HH时mm分ss秒</li>
     * <li>yyyy-MM-dd HH:mm</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSS</li>
     * <li>yyyyMMddHHmmss</li>
     * <li>yyyyMMddHHmmssSSS</li>
     * <li>yyyyMMdd</li>
     * <li>EEE, dd MMM yyyy HH:mm:ss z</li>
     * <li>EEE MMM dd HH:mm:ss zzz yyyy</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * </ol>
     *
     * @param dateCharSequence 日期字符串
     * @return 日期
     */
    public static Date parse(CharSequence dateCharSequence) {
        if (StringUtils.isBlank(dateCharSequence)) {
            return null;
        }
        String dateStr = dateCharSequence.toString();
        // 去掉两边空格并去掉中文日期中的“日”和“秒”，以规范长度
        dateStr = StringUtils.removeAll(dateStr.trim(), '日', '秒');
        int length = dateStr.length();

        if (NumberUtils.isNumber(dateStr)) {
            // 纯数字形式
            if (length == PURE_DATETIME_PATTERN.length()) {
                return parse(dateStr, PURE_DATETIME_FORMAT);
            } else if (length == PURE_DATETIME_MS_PATTERN.length()) {
                return parse(dateStr, PURE_DATETIME_MS_FORMAT);
            } else if (length == PURE_DATE_PATTERN.length()) {
                return parse(dateStr, PURE_DATE_FORMAT);
            } else if (length == PURE_TIME_PATTERN.length()) {
                return parse(dateStr, PURE_TIME_FORMAT);
            }
        }
        if (length == NORM_DATETIME_PATTERN.length()) {
            // yyyy-MM-dd HH:mm:ss
            return parseDateTime(dateStr);
        } else if (length == NORM_DATE_PATTERN.length()) {
            // yyyy-MM-dd
            return parseDate(dateStr);
        } else if (length == NORM_DATETIME_MINUTE_PATTERN.length()) {
            // yyyy-MM-dd HH:mm
            return parse(dateStr, NORM_DATETIME_MINUTE_FORMAT);
        }

        // 没有更多匹配的时间格式
        throw new UtilException("No format fit for date String:" + dateStr);
    }

    /**
     * 转换字符串为Date
     *
     * @param dateStr    日期字符串
     * @param dateFormat {@link SimpleDateFormat}
     * @return {@link Date}
     */
    private static Date parse(CharSequence dateStr, DateFormat dateFormat) {
        if (StringUtils.isBlank(dateStr)) {
            throw new UtilException("dataStr is blank");
        }
        try {
            return dateFormat.parse(dateStr.toString());
        } catch (Throwable e) {
            String pattern;
            if (dateFormat instanceof SimpleDateFormat) {
                pattern = ((SimpleDateFormat) dateFormat).toPattern();
            } else {
                pattern = dateFormat.toString();
            }
            throw new UtilException(StringUtils.format("Parse [{}] with format [{}] error!", dateStr, pattern), e);
        }
    }

    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateStr 特定格式的日期
     * @param format  格式，例如yyyy-MM-dd
     * @return 日期对象
     */
    public static Date parse(CharSequence dateStr, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return parse(dateStr, fmt);
    }

    /**
     * 解析格式为yyyy-MM-dd的日期，忽略时分秒
     *
     * @param dateString 标准形式的日期字符串
     * @return 日期对象
     */
    public static Date parseDate(CharSequence dateString) {
        return parse(dateString, NORM_DATE_FORMAT);
    }

    /**
     * 格式yyyy-MM-dd HH:mm:ss
     *
     * @param dateString 标准形式的时间字符串
     * @return 日期对象
     */
    public static Date parseDateTime(CharSequence dateString) {
        return parse(dateString, NORM_DATETIME_FORMAT);
    }


    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link SimpleDateFormat}
     * @return 格式化后的字符串
     */
    public static String format(Date date, DateFormat format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }
}
