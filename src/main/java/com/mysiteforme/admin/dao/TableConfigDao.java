package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.DTO.TableFieldDTO;
import com.mysiteforme.admin.entity.TableConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.request.PageListTableConfigRequest;
import com.mysiteforme.admin.entity.response.PageListTableConfigResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Mapper
public interface TableConfigDao extends BaseMapper<TableConfig> {

    IPage<PageListTableConfigResponse>  selectPageTableConfig(IPage<PageListTableConfigResponse> page, @Param("request") PageListTableConfigRequest request);

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @param schemaName 模式名
     * @return 存在返回1，不存在返回0
     */
    Integer existTable(@Param("tableName")String tableName,@Param("schemaName")String schemaName);

    /**
     * 获取模式名列表
     * @return 模式名列表
     */
    List<String> getSchemaNameList();

    /**
     * 获取表名列表
     * @return 表名列表
     */
    List<String> getTableNameList(@Param("schemaName") String schemaName);

    /**
     * 获取表字段列表
     * @return 表字段列表
     */
    List<TableFieldDTO> getFiledList(@Param("schemaName") String schemaName,
                                          @Param("tableName") String tableName,
                                          @Param("tableType") Integer tableType);

    /**
     * 获取表字段列表
     * @return 表字段列表
     */
    List<TableFieldDTO> getSimpleFiledList(@Param("schemaName") String schemaName,
                                     @Param("tableName") String tableName);

    /**
     * 获取字段详情
     * @return 字段详情
     */
    TableFieldDTO getFieldDetail(@Param("schemaName") String schemaName,
                                 @Param("tableName") String tableName,
                                 @Param("columnName") String columnName);

}
