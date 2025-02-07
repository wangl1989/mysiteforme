package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.User;

public interface UserCacheService extends IService<User> {

    User findUserById(Long id);
}
