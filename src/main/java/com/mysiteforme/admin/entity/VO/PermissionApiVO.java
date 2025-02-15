/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 23:23:04
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:47:24
 * @ Description: 权限API展示对象
 */

package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionApiVO {

    private Long id;    

    private String apiUrl;

    private String httpMethod;  

}
