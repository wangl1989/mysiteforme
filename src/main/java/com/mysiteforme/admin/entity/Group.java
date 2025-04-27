/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:51:17
 * @ Description: 分组基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@TableName("sys_group")
@Data
public class Group extends TreeEntity<Group> {

    private String name;

}
