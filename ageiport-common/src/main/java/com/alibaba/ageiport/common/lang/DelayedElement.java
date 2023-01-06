package com.alibaba.ageiport.common.lang;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import lombok.Data;

/**
 * 延迟
 *
 * @param <T>
 * @author lingyue
 */
@Data
public class DelayedElement<T> implements Delayed {

    /**
     * 延迟时间，毫秒
     */
    private final long delay;
    /**
     * 到期时间，毫秒
     */
    private final long expire;
    /**
     * 数据
     */
    private final T data;
    /**
     * 创建时间，毫秒
     */
    private final long now;

    public DelayedElement(long delay, T data) {
        this.delay = delay;
        this.data = data;
        //到期时间 = 当前时间+延迟时间
        expire = System.currentTimeMillis() + delay;
        now = System.currentTimeMillis();
    }

    /**
     * 需要实现的接口，获得延迟时间   用过期时间-当前时间
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 用于延迟队列内部比较排序   当前时间的延迟时间 - 比较对象的延迟时间
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        return (int)(this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DelayedElement{");
        sb.append("delay=").append(delay);
        sb.append(", expire=").append(expire);
        sb.append(", msg='").append(data).append('\'');
        sb.append(", now=").append(now);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DelayedElement<?> that = (DelayedElement<?>)o;

        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}
