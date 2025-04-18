package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.entity.request.PageListUploadBaseInfoRequest;

public interface UploadBaseInfoService extends IService<UploadBaseInfo> {

    /**
     * 分页查询上传基础信息
     * @param request 查询参数
     * @return 上传基础信息列表
     */
    IPage<UploadBaseInfo> selectPageInfo(PageListUploadBaseInfoRequest request);

    /**
     * 保存或更新上传基础信息
     * @param uploadBaseInfo 上传基础信息
     */
    UploadBaseInfo saveOrUpdateBaseInfo(UploadBaseInfo uploadBaseInfo);

    /**
     * 删除上传基础信息
     * @param id 上传基础信息ID
     */
    UploadBaseInfo deleteUploadBaseInfo(Long id);

    /**
     * 启用上传基础信息
     * @param id 上传基础信息ID
     */
    UploadBaseInfo enableUploadBaseInfo(Long id);

    Boolean checkUploadBaseInfo(String type, Long id);

    /**
     * 根据类型获取上传基础信息
     * @param fileUploadType 上传类型
     * @return 上传基础信息
     */
    UploadBaseInfo getInfoByType(String fileUploadType);

}
