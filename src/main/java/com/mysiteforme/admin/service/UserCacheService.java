/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:31:26
 * @ Description: 用户缓存Service（待修改）
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;

import java.util.Map;

public interface UserCacheService {

    /**
     * 根据ID查询用户详细信息
     * @param id 用户ID
     * @return 用户详细信息
     */
    UserVO findUserByIdDetails(Long id);

    UserVO getSuperAdminUserDetail(User user);

    Map<Long,User> getAllUser();
}
