package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.dao.SiteDao;
import com.mysiteforme.admin.service.SiteService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2017-12-30
 */
@Service("siteService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class SiteServiceImpl extends ServiceImpl<SiteDao, Site> implements SiteService {

    @Cacheable(value = "currentSite",key = "'currentSite'")
    @Override
    public Site getCurrentSite() {
        QueryWrapper<Site> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return getOne(wrapper);
    }

    @CacheEvict(value = "currentSite",key = "'currentSite'")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSite(Site site) {
        baseMapper.updateById(site);
    }


}
