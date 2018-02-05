package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.Rescource;
import com.baomidou.mybatisplus.service.IService;
/**
 * <p>
 * 系统资源 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-01-14
 */
public interface RescourceService extends IService<Rescource> {

    int getCountByHash(String hash);

    Rescource getRescourceByHash(String hash);

}
