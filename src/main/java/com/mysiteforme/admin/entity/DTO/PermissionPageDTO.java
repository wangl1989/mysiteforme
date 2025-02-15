/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:46:18
 * @ Description: 权限页面数据传输对象
 */

package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionPageDTO extends PermissionPage {

    private Integer page;
    private Integer limit;

}