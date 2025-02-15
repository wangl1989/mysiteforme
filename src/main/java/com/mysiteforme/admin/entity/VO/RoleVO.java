/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 01:38:51
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:15:43
 * @ Description: 角色展示对象
 */

package com.mysiteforme.admin.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RoleVO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

}
