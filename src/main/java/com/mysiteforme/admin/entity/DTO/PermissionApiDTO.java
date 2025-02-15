/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:45:41
 * @ Description: 权限API数据传输对象 
 */

package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionApi;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionApiDTO extends PermissionApi {

    private Integer page;
    private Integer limit;

}