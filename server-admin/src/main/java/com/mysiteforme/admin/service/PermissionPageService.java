/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 11:56:05
 * @ Description: 页面权限Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.PermissionPage;

public interface PermissionPageService extends IService<PermissionPage> {
    
    /**
     * 根据页面URL检查唯一性
     */
    boolean checkPageUrlUnique(String pageUrl, Long id);
    
    /**
     * 根据ID查询页面权限
     */
    PermissionPage getById(Long id);
    
    /**
     * 根据URL查询页面权限
     */
    PermissionPage getByUrl(String url);
}