package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.UploadInfoDao;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.service.UploadInfoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 文件上传配置 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-07-06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UploadInfoServiceImpl extends ServiceImpl<UploadInfoDao, UploadInfo> implements UploadInfoService {

    @Cacheable(value = "uploadInfoCache",key = "'getinfo'",unless = "#result == null")
    @Override
    public UploadInfo getOneInfo() {
        EntityWrapper<UploadInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        return selectOne(wrapper);
    }

    @CacheEvict(value = "uploadInfoCache",key = "'getinfo'")
    @Override
    public void updateInfo(UploadInfo uploadInfo) {
        updateById(uploadInfo);
    }
}
