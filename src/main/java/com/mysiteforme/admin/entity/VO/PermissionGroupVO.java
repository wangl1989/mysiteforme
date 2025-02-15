/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 23:19:20
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 23:21:08
 * @ Description: 权限分组VO
 */

package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionGroupVO {

    private Long id;

    private String groupCode;

    private String groupName;

}
