package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AddTableRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6962439201546719734L;

    @NotBlank(message = MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;
    /**
     * 表的名称
     */
    @NotBlank(message = MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY)
    private String tableName;
    /**
     * 表备注
     */
    @NotBlank(message = MessageConstants.Table.TABLE_COMMENT_NOT_EMPTY)
    private String comment;

    /**
     * table类型 1.基本类型  2.树结构类型 3.辅助表
     */
    @NotNull(message = MessageConstants.Table.TABLE_TYPE_NOT_NULL)
    private Integer tableType;
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
    /**
     * 字段列表
     */
    private List<BaseTableFieldRequest> fieldList;
}
