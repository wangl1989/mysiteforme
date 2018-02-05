package com.mysiteforme.admin.entity.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author:tnt
 * @Description:${TODO}
 * @Date: Create in 17:55 2017/12/25.
 */
public class TableVO implements Serializable{
    /**
     * 表的名称
     */
    private String name;
    /**
     * 字段列表
     */
    private List<TableField> fieldList;
    /**
     * 表备注
     */
    private String comment;

    /**
     * table类型 1.基本类型  2.树结构类型
     */
    private Integer tabletype;
    /**
     * 表有多少条数据
     */
    private Integer tableRows;

    /**
     * 表的创建时间
     */
    private Date createTime;

    /**
     * 表的更新时间
     */
    private Date updateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<TableField> fieldList) {
        this.fieldList = fieldList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getTabletype() {
        return tabletype;
    }

    public void setTabletype(Integer tabletype) {
        this.tabletype = tabletype;
    }

    public Integer getTableRows() {
        return tableRows;
    }

    public void setTableRows(Integer tableRows) {
        this.tableRows = tableRows;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
