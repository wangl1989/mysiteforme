package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.UploadInfoDao;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.service.UploadInfoService;
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

    @Override
    public UploadInfo getOneInfo() {
        UploadInfo uploadInfo = baseMapper.selectOne(new UploadInfo());
        return uploadInfo;
    }
}
