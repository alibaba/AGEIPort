package com.alibaba.ageiport.ext.arch.context;

/**
 * The Lifecycle
 *
 * @author xuechao.sxc
 */
public interface Lifecycle {

    /**
     * Initialize the component before {@link #start() start}
     *
     * @throws IllegalStateException
     */
    void initialize() throws IllegalStateException;

    /**
     * Start the component
     *
     * @throws IllegalStateException
     */
    void start() throws IllegalStateException;

    /**
     * Destroy the component
     *
     * @throws IllegalStateException
     */
    void destroy() throws IllegalStateException;
}
