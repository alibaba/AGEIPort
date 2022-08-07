package com.alibaba.ageiport.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * PageQuery
 *
 * @author lingyi
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = -1888524305184542332L;
    private Integer pageSize = 50;

    /**
     * start from 1
     */
    private Integer pageIndex = 1;


    public Integer getRowOffset() {
        return (pageIndex - 1) * pageSize;
    }
}
