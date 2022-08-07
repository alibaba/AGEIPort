package com.alibaba.ageiport.common.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lingyi
 */
public class NamedThreadFactory implements ThreadFactory {

    protected static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

    protected final AtomicInteger mThreadNum = new AtomicInteger(1);

    protected final String mPrefix;

    protected final boolean mDaemon;

    protected final ThreadGroup mGroup;

    protected final UncaughtExceptionHandler uncaughtExceptionHandler;

    public NamedThreadFactory() {
        this("pool-" + POOL_SEQ.getAndIncrement(), false, null);
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, false, null);
    }

    public NamedThreadFactory(String prefix, UncaughtExceptionHandler uncaughtExceptionHandler) {
        this(prefix, false, uncaughtExceptionHandler);
    }

    public NamedThreadFactory(String prefix, boolean daemon, UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.mPrefix = prefix + "-t-";
        this.mDaemon = daemon;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        SecurityManager s = System.getSecurityManager();
        mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup, runnable, name, 0);
        ret.setDaemon(mDaemon);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return mGroup;
    }
}