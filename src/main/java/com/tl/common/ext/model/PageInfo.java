package com.tl.common.ext.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class PageInfo<T> {

    //页面大小
    private Integer records;

    //当前页码
    private Integer page;

    //总页数
    private Integer total;

    //总记录数
    private Long count;

    //分页数据
    private T rows;

    @JsonIgnore
    public Map<String, Object> getMap(){
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("page", this.getPage());
        fieldMap.put("records", this.getRecords());
        fieldMap.put("total", this.getTotal());
        fieldMap.put("count", this.getCount());
        fieldMap.put("rows", this.getRows());
        return fieldMap;
    }

    public Integer getRecords() {
        return records;
    }

    public void setRecords(Integer records) {
        this.records = records;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
