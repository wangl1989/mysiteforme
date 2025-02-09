package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Site;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2017-12-30
 */
public interface SiteService extends IService<Site> {

    /**
     * 获取当前站点信息
     * @return 站点信息对象
     */
    Site getCurrentSite();

    /**
     * 更新站点信息
     * @param site 站点信息对象
     */
    void updateSite(Site site);
	
}
