/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 01:38:51
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:15:43
 * @ Description: 角色展示对象
 */

package com.mysiteforme.admin.entity.VO;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 是否为默认角色
     */
    private Boolean isDefault;

    /**
     * 备注信息
     */
    private String remarks;

}
