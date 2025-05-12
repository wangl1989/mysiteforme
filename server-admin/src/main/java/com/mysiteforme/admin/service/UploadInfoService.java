/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:30:56
 * @ Description: 文件上传配置Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.UploadInfo;

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
