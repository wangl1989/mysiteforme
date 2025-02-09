package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.UploadInfo;
/**
 * <p>
 * 文件上传配置 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-07-06
 */
public interface UploadInfoService extends IService<UploadInfo> {

    /**
     * 获取唯一的上传配置信息
     * @return 上传配置信息对象
     */
    UploadInfo getOneInfo();

    /**
     * 更新上传配置信息
     * @param uploadInfo 要更新的上传配置信息对象
     */
    void updateInfo(UploadInfo uploadInfo);

}
