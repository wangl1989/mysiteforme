/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 11:56:05
 * @ Description: 按钮权限Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.PermissionButton;

public interface PermissionButtonService extends IService<PermissionButton> {
    
    /**
     * 根据按钮标识检查唯一性
     */
    boolean checkButtonKeyUnique(String buttonKey, Long id);
    
    /**
     * 根据ID查询按钮权限
     */
    PermissionButton getById(Long id);
    
    /**
     * 根据按钮标识查询按钮权限
     */
    PermissionButton getByButtonKey(String buttonKey);
}