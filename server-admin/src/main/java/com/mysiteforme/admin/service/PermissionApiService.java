/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 13:00:19
 * @ Description: API权限Service
 */

 package com.mysiteforme.admin.service;

 import com.baomidou.mybatisplus.extension.service.IService;
 import com.mysiteforme.admin.entity.PermissionApi;
 
 public interface PermissionApiService extends IService<PermissionApi> {
     
     /**
      * 根据API URL和HTTP方法检查唯一性
      */
     boolean checkApiUrlUnique(String apiUrl, String httpMethod, Long id);
     
 }