package com.tl.common.ext.model;


import com.tl.common.ext.annotation.AntiSqlInjection;
import com.tl.common.ext.annotation.NotNull;

public class BaseListDTO {

    //每页数据大小
    @NotNull(msg="每页数量不能为空")
    protected Integer pageSize;

    //当前页号
    @NotNull(msg="页号不能为空")
    protected Integer pageNo;

    //排序规则
    @AntiSqlInjection(msg = "输入异常")
    protected String orderSql;

    @AntiSqlInjection(msg = "输入异常")
    protected String searchKey;

    protected String searchValue;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getOrderSql() {
        return orderSql;
    }

    public void setOrderSql(String orderSql) {
        this.orderSql = orderSql;
    }

    @Override
    public String toString() {
        return "BaseListDTO{" +
                "pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", orderSql='" + orderSql + '\'' +
                '}';
    }
}
