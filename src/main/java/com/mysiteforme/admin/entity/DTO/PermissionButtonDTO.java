/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:45:45
 * @ Description: 权限按钮数据传输对象
 */

package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionButton;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionButtonDTO extends PermissionButton {

    private Integer page;
    private Integer limit;

}