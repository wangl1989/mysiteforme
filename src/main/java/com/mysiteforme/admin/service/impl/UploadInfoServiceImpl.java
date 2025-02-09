package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    /**
     * 获取唯一的上传配置信息
     * 结果会被缓存
     * 只返回未删除的配置信息
     * @return 上传配置信息对象
     */
    @Cacheable(value = "uploadInfoCache",key = "'getinfo'",unless = "#result == null")
    @Override
    public UploadInfo getOneInfo() {
        QueryWrapper<UploadInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return getOne(wrapper);
    }

    /**
     * 更新上传配置信息
     * 同时清除配置信息缓存
     * @param uploadInfo 要更新的上传配置信息对象
     */
    @CacheEvict(value = "uploadInfoCache",key = "'getinfo'")
    @Override
    public void updateInfo(UploadInfo uploadInfo) {
        updateById(uploadInfo);
    }
}
