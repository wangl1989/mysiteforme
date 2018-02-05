package com.mysiteforme.admin.entity.VO;

import com.mysiteforme.admin.entity.Dict;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:tnt
 * @Description:${TODO}
 * @Date: Create in 18:00 2017/12/25.
 */
public class TableField implements Serializable{
    private String name;
    private Integer length;
    private String type;
    /**
     * 字段是否可以为空
     */
    private String isNullValue;
    /**
     * 字段描述
     */
    private String comment;

    /**
     * 字段用户(输入框..select..等等)
     */
    private String dofor;

    /***
     * 表名
     */
    private String tableName;

    /***
     * 表类型
     */
    private Integer tableType;

    /***
     * 表的注释
     */
    private String tableComment;

    /***
     * 是否为系统默认选项值(switch类型的按钮用得到)
     */
    private Boolean defaultValue;

    /***
     * 多选项目内置属性
     */
    private List<Dict> dictlist;

    /**
     * 字段原名称
     */
    private String oldName;

    /**
     * 列表table里是否显示该字段
     */
    private Boolean listIsShow = true;

    /**
     * 列表上方是否根据此字段来搜索
     */
    private Boolean listIsSearch = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getIsNullValue() {
        return isNullValue;
    }

    public void setIsNullValue(String isNullValue) {
        this.isNullValue = isNullValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDofor() {
        return dofor;
    }

    public void setDofor(String dofor) {
        this.dofor = dofor;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getTableType() {
        return tableType;
    }

    public void setTableType(Integer tableType) {
        this.tableType = tableType;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<Dict> getDictlist() {
        return dictlist;
    }

    public void setDictlist(List<Dict> dictlist) {
        this.dictlist = dictlist;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Boolean getListIsShow() {
        return listIsShow;
    }

    public void setListIsShow(Boolean listIsShow) {
        this.listIsShow = listIsShow;
    }

    public Boolean getListIsSearch() {
        return listIsSearch;
    }

    public void setListIsSearch(Boolean listIsSearch) {
        this.listIsSearch = listIsSearch;
    }
}
