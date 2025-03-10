package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Dict;

import java.util.List;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author wangl
 * @since 2017-11-26
 */
public interface DictService extends IService<Dict> {

    /**
     * 根据字典类型获取字典列表
     * @param type 字典类型
     * @return 字典列表
     */
    List<Dict> getDictByType(String type);

    /**
     * 获取指定类型的字典数量
     * @param type 字典类型
     * @return 字典数量
     */
    Integer getCountByType(String type);

    /**
     * 获取指定类型下的最大排序值
     * @param type 字典类型
     * @return 最大排序值
     */
    Integer getMaxSortByType(String type);

    /**
     * 根据类型、标签、值获取字典数量
     * @param type 字典类型
     * @param label 字典标签
     * @param value 字典值
     * @return 字典数量
     */
    Integer getCountByAll(String type, String label, String value);

    /**
     * 保存或更新字典
     * @param dict 字典对象
     */
    void saveOrUpdateDict(Dict dict);

    /**
     * 删除字典
     * @param id 字典ID
     */
    void deleteDict(Long id);

    /**
     * 批量保存指定类型的字典列表
     * @param type 字典类型
     * @param list 字典列表
     */
    void saveDictList(String type, List<Dict> list);

    /**
     * 删除指定类型的所有字典
     * @param type 字典类型
     */
    void deleteByType(String type);

    /**
     * 删除指定表名相关的所有字典
     * @param tableName 表名
     */
    void deleteByTableName(String tableName);

    /**
     * 更新字典类型
     * @param oldType 原字典类型
     * @param newType 新字典类型
     */
    void updateByType(String oldType, String newType);
}
