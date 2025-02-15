/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:39:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:46:11
 * @ Description: 权限组数据传输对象
 */

package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionGroup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionGroupDTO extends PermissionGroup {

    private Integer page;
    private Integer limit;

}