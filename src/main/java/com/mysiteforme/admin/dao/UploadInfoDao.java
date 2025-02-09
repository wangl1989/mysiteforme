package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.UploadInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文件上传配置 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2018-07-06
 */
@Mapper
public interface UploadInfoDao extends BaseMapper<UploadInfo> {

}
