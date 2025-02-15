/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:24:17
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:47:43
 * @ Description: 权限页面展示对象
 */

package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionPageVO  {

    private Long id;

    private String pageUrl;

}
