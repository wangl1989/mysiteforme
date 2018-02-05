package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.Dict;
import com.baomidou.mybatisplus.service.IService;

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

    List<Dict>  getDictByType(String type);

    Integer getCountByType(String type);

    Integer getMaxSortByType(String type);

    Integer getCountByAll(String type,String label,String value);

    void saveOrUpdateDict(Dict dict);

    String deleteDict(Long id);

    List<Dict> saveDictList(String type, List<Dict> list);

    void deleteByType(String s);

    void deleteByTableName(String tableName);

    void updateByType(String oldType,String newType);
}
