package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.DTO.TableFieldDTO;
import com.mysiteforme.admin.entity.TableConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.BaseTableFieldResponse;
import com.mysiteforme.admin.entity.response.BaseTableResponse;
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

    /**
     * 表格分页
     * @param page 分页对象
     * @param request 参数对象
     * @return 分页结果
     */
    IPage<BaseTableResponse>  selectPageTable(IPage<BaseTableResponse> page, @Param("request") PageListTableRequest request);

    /**
     * 创建表格
     * @param request 参数对象
     */
    void creatTable(@Param("request") AddTableRequest request);

    /**
     * 修改表名
     * @param request 参数对象
     */
    void changeTableName(@Param("request") UpdateTableRequest request);

    /**
     * 修改表注释
     * @param request 参数对象
     */
    void changeTableComment(@Param("request") UpdateTableRequest request);

    /**
     * 表格字段分页
     * @param page 分页对象
     * @param request 参数对象
     * @return 分页结果
     */
    IPage<BaseTableFieldResponse>  selectPageTableField(IPage<BaseTableFieldResponse> page, @Param("request") PageListTableFieldRequest request);

    /**
     * 表格字段集合
     * @param request 参数集合
     * @return 字段集合
     */
    List<BaseTableFieldResponse>  selectPageTableField(@Param("request") ShowTableFieldRequest request);

    /**
     * 添加字段
     * @param request 参数对象
     */
    void addColumn(@Param("request") AddTableFieldRequest request);

    /**
     * 修改字段（不修改字段名）
     * @param request 参数对象
     */
    void updateColumnSameName(@Param("request") UpdateTableFieldRequest request);

    /**
     * 修改字段（同时修改字段名）
     * @param request 参数对象
     */
    void updateColumnDiffName(@Param("request") UpdateTableFieldRequest request);

    /**
     * 检查表字段是否存在
     * @param schemaName schemaName值
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 存在返回1，不存在返回0
     */
    Integer existTableField(@Param("schemaName") String schemaName,
                            @Param("tableName") String tableName,
                            @Param("fieldName") String fieldName);

    /**
     * 删除表字段
     * @param tableName 表名
     * @param fieldName 字段名
     */
    void dropTableField(@Param("tableName") String tableName,@Param("fieldName") String fieldName);

    /**
     * 删除表
     * @param tableName 表名
     */
    void dropTable(String tableName);
}
