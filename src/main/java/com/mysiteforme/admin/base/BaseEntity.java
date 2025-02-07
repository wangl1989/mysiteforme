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
 * Entity支持类
 *
 */

@Getter
@Setter
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 6962439201546719734L;

    /**
     * 实体编号（唯一标识）
     */
    @TableId
    protected Long id;



    public BaseEntity() {

    }

    public BaseEntity(Long id) {
        this();
        this.id = id;
    }
    @JsonSerialize(using=ToStringSerializer.class)
    public Long getId() {
        return id;
    }





}
