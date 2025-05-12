/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:30:31
 * @ Description: 站点Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.entity.response.SiteUploadTypeResponse;

import java.util.List;

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

    List<SiteUploadTypeResponse> getSiteUploadTypeList();

    /**
     * 检测webservice服务是否可用
     * @param key 腾讯位置服务申请的key
     * @return 是否通过测试
     */
    Boolean checkWebService(String key);


}
