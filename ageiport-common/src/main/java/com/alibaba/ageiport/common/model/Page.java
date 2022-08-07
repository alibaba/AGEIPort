package com.alibaba.ageiport.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * usage
 *
 * @author lingyi
 */
@Data
@Accessors(chain = true)
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 6999261967397429173L;
    private Integer currentPage;
    private Integer pageSize;
    private Long totalItem;
    private List<T> rows;

    public Integer getTotalPage() {
        return (int) (this.totalItem / (this.pageSize) + (this.totalItem % (this.pageSize) == 0 ? 0 : 1));
    }

    public static <T> Page<T> empty(PageQuery query) {
        return new Page<T>()
                .setPageSize(query.getPageSize())
                .setCurrentPage(query.getPageIndex())
                .setRows(Collections.emptyList());
    }

    public static <T> Page<T> build(PageQuery query, Long totalItem, List<T> rows) {
        return new Page<T>()
                .setPageSize(query.getPageSize())
                .setCurrentPage(query.getPageIndex())
                .setTotalItem(totalItem)
                .setRows(rows);
    }

    public boolean isEmpty() {
        return rows == null || rows.isEmpty();
    }
}
