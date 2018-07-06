package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.UploadInfo;
import com.baomidou.mybatisplus.service.IService;
/**
 * <p>
 * 文件上传配置 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-07-06
 */
public interface UploadInfoService extends IService<UploadInfo> {

    public UploadInfo getOneInfo();

}
