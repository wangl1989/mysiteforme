/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:30:44
 * @ Description: 数据表Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.BaseTableFieldResponse;
import com.mysiteforme.admin.entity.response.BaseTableResponse;

import java.util.List;

public interface TableService {

    IPage<BaseTableResponse> selectPageTable(PageListTableRequest request);

    /**
     * 检查表是否存在
     * @param schemaName schemaName
     * @param tableName 表名
     * @return 存在返回1，不存在返回0
     */
    Integer existTable(String schemaName, String tableName);

    /**
     * 创建数据表
     * @param request 表结构信息对象
     */
    void creatTable(AddTableRequest request);

    /**
     * 修改表名
     * @param request 表结构信息对象
     */
    void changeTableName(UpdateTableRequest request);

    /**
     * 修改表注释
     * @param request 表结构信息对象
     */
    void changeTableComment(UpdateTableRequest request);


    IPage<BaseTableFieldResponse> selectPageTableField(PageListTableFieldRequest request);

    List<BaseTableFieldResponse> selectListTableField(ShowTableFieldRequest request);

    /**
     * 添加表字段
     * @param request 字段信息对象
     */
    void addColumn(AddTableFieldRequest request);

    /**
     * 更新表字段
     * @param request 字段信息对象
     */
    void updateColumn(UpdateTableFieldRequest request);

    /**
     * 检查表字段是否存在
     * @param schemaName schemaName
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 存在返回1，不存在返回0
     */
    Integer existTableField(String schemaName, String tableName, String fieldName);

    /**
     * 删除表字段
     * @param request 参数
     */
    void dropTableField(DeleteFieldRequest request);

    /**
     * 删除数据表
     * @param tableName 表名
     */
    void dropTable(String tableName);

}
