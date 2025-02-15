/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:46:15
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:46:04
 * @ Description: 权限数据传输对象
 */

package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.Permission;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionDTO extends Permission {

    private Integer page;
    private Integer limit;

}
