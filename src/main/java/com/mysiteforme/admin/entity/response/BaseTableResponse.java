/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:48:55
 * @ Description: 数据库的表展示对象
 */

package com.mysiteforme.admin.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysiteforme.admin.entity.VO.TableField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class BaseTableResponse implements Serializable{

    @Serial
    private static final long serialVersionUID = 6962439201546719734L;
    /**
     * 表的名称
     */
    private String name;

    /**
     * 表的schema名称
     */
    private String schemaName;
    /**
     * 字段列表
     */
    private List<BaseTableFieldResponse> fieldList;
    /**
     * 表备注
     */
    private String comment;

    /**
     * table类型 1.基本类型  2.树结构类型
     */
    private Integer tableType;

    @JsonIgnore
    private Boolean hasParentId;

    @JsonIgnore
    private Boolean hasDelFlag;
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


}
