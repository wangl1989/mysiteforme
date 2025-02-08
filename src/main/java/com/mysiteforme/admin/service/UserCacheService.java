package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
