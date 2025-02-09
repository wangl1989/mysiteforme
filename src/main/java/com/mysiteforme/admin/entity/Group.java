package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_group")
@Data
@Getter
@Setter
public class Group extends TreeEntity<Group> {

    private String name;

}
