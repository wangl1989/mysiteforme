/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:28:48
 * @ Description: 数据实体基类 提供创建者、创建时间、更新者、更新时间等审计字段
 */

package com.mysiteforme.admin.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mysiteforme.admin.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class DataEntity extends BaseEntity {

    /**
     * 创建者ID
     */
    @Getter
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    protected Long createId;

    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    protected Date createDate;

    /**
     * 更新者
     */
    @Getter
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    protected Long updateId;

    /**
     * 更新日期
      */
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    protected Date updateDate;

    /**
     * 删除标记（Y：正常；N：删除；A：审核；）
     */
    @Getter
    @TableField(value = "del_flag")
    protected Boolean delFlag;

    /**
     * 备注
     */
    @Getter
    protected String remarks;

    /**
     * 创建着
     */
    @TableField(exist = false)
    protected User createUser;

    /**
     * 修改者
     */
    @TableField(exist = false)
    protected User updateUser;

    /**
     * 获取创建时间
     * @return 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 默认构造函数
     * 初始化删除标记为false
     */
    public DataEntity() {
        super();
        this.delFlag = false;
    }

    /**
     * 带ID的构造函数
     * @param id 实体ID
     */
    public DataEntity(Long id) {
        super(id);
    }

    /**
     * 获取更新时间
     * @return 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateDate() {
        return updateDate;
    }
}
