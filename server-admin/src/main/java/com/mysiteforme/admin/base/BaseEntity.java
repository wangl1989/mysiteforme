/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:27:51
 * @ Description: 实体类基类
 */
package com.mysiteforme.admin.base;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6962439201546719734L;

    /**
     * 实体ID,唯一标识
     */
    @TableId
    protected Long id;

    /**
     * 获取实体ID
     * @return 实体ID
     */
    @JsonSerialize(using=ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    /**
     * 默认构造函数
     */
    public BaseEntity() {
    }

    /**
     * 带ID的构造函数
     * @param id 实体ID
     */
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }
}
