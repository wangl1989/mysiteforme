package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.VO.TableField;
import com.mysiteforme.admin.entity.VO.TableVO;

import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2017/12/25.
 * todo: 数据表service
 */
public interface TableService {

    /**
     * 获取所有数据表信息
     * @return 数据表列表
     */
    List<TableVO> listAll();

    /**
     * 获取数据表总数
     * @return 数据表数量
     */
    Integer getTableCount();

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 存在返回1，不存在返回0
     */
    Integer existTable(String tableName);

    /**
     * 检查表字段是否存在
     * @param map 包含表名和字段名的参数Map
     * @return 存在返回1，不存在返回0
     */
    Integer existTableField(Map<String, Object> map);

    /**
     * 创建数据表
     * @param tableVO 表结构信息对象
     */
    void creatTable(TableVO tableVO);

    /**
     * 添加表字段
     * @param tableField 字段信息对象
     */
    void addColumn(TableField tableField);

    /**
     * 更新表字段
     * @param tableField 字段信息对象
     */
    void updateColumn(TableField tableField);

    /**
     * 删除表字段
     * @param fieldName 字段名
     * @param tableName 表名
     */
    void dropTableField(String fieldName, String tableName);

    /**
     * 删除数据表
     * @param tableName 表名
     */
    void dropTable(String tableName);

    /**
     * 分页查询数据表
     * @param objectPage 分页对象
     * @param map 查询条件
     * @return 分页结果
     */
    IPage<TableVO> selectTablePage(IPage<TableVO> objectPage, Map<String,Object> map);

    /**
     * 查询表字段列表
     * @param map 查询条件
     * @return 字段列表
     */
    List<TableField> selectFields(Map<String,Object> map);

    /**
     * 分页查询表字段
     * @param objectPage 分页对象
     * @param map 查询条件
     * @return 分页结果
     */
    IPage<TableField> selectTableFieldPage(IPage<TableField> objectPage, Map<String,Object> map);

    /**
     * 获取表详细信息
     * @param name 表名
     * @return 表详细信息
     */
    TableVO detailTable(String name);

    /**
     * 修改表名
     * @param name 新表名
     * @param rename 旧表名
     * @param comment 表注释
     * @param tableType 表类型
     */
    void changeTableName(String name, String rename, String comment, Integer tableType);

    /**
     * 修改表注释
     * @param name 表名
     * @param comment 表注释
     * @param tableType 表类型
     */
    void changeTableComment(String name, String comment, Integer tableType);
}
