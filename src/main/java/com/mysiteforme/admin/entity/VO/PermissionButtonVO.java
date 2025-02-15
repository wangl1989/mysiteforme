/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 23:20:33
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 23:21:11
 * @ Description: 按钮权限VO
 */

package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionButtonVO {
    
    private Long id;

    private String buttonKey;

    private String buttonName;

}
