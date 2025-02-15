/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:31:26
 * @ Description: 用户缓存Service（待修改）
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.User;

public interface UserCacheService {

    /**
     * 根据用户ID查找用户信息
     * 结果会被缓存
     * @param id 用户ID
     * @return 用户信息对象
     */
    User findUserById(Long id);
}
