/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.mysiteforme.admin.base;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 实体类基类
 * 提供ID等基础字段
 * @author JeeSite
 */
@Getter
@Setter
@Data
public abstract class BaseEntity implements Serializable {

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
