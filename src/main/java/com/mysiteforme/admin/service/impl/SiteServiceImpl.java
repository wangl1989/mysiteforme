/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:30
 * @ Description: 站点服务实现类 提供站点的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.SiteDao;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.service.SiteService;

@Service("siteService")
@Transactional(rollbackFor = Exception.class)
public class SiteServiceImpl extends ServiceImpl<SiteDao, Site> implements SiteService {

    /**
     * 获取当前站点信息
     * 结果会被缓存
     * 只返回未删除的站点
     * @return 站点信息对象
     */
    @Cacheable(value = "system::site",key = "'currentSite'")
    @Override
    public Site getCurrentSite() {
        QueryWrapper<Site> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return getOne(wrapper);
    }

    /**
     * 更新站点信息
     * 同时清除站点缓存
     * @param site 要更新的站点信息对象
     */
    @CacheEvict(value = "system::site",key = "'currentSite'")
    @Override
    public void updateSite(Site site) {
        baseMapper.updateById(site);
    }


}
