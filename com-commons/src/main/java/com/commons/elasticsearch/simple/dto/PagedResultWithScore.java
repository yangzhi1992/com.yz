package com.commons.elasticsearch.simple.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 分页结果DTO
 */
public class PagedResultWithScore<T> implements Serializable {

    private static final long serialVersionUID = 483463855051953642L;

    /**
     * 总数
     */
    private Long total = 0L;

    /**
     * 返回行数
     */
    private List<ScoreWrapper<T>> rows = new LinkedList<>();


    public PagedResultWithScore(Long total, List<ScoreWrapper<T>> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<ScoreWrapper<T>> getRows() {
        return rows;
    }

    public void setRows(List<ScoreWrapper<T>> rows) {
        this.rows = rows;
    }

}
