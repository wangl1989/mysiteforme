package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.DTO.TableFieldDTO;
import com.mysiteforme.admin.entity.TableFieldConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.TableFieldConfigDTO;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.BaseTableFieldConfigResponse;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
public interface TableFieldConfigService extends IService<TableFieldConfig> {

    /**
     * 根据表名获取字段配置列表
     * @param tableConfigId 请求参数
     * @return 字段配置列表
     */
    List<BaseTableFieldConfigResponse> getFieldsByTableConfigId(Long tableConfigId);

    /**
     * 根据表配置ID获取字段配置列表
     * @param request 请求参数
     * @return 字段配置列表
     */
    List<TableFieldDTO> getSimpleTableField(GetSimpleFieldRequest request);

    /**
     * 同步表字段数据
     * @param request 请求参数
     * @return 同步结果
     */
    boolean syncTableFieldConfig(SyncTableFieldConfigRequest request);

    /**
     * 更新字段配置
     * @param request 请求参数
     * @return 更新结果
     */
    boolean updateTableFieldConfig(UpdateTableFieldConfigRequest request);

    /**
     * 排序字段
     * @param ids id集合参数
     */
    void sortFields(List<Long> ids);
}
