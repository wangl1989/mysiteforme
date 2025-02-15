/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:46:15
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 02:48:18
 * @ Description: 权限DTO
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
