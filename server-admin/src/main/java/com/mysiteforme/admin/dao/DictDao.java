/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:41:16
 * @ Description: 字典表数据层接口 提供字典表的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Dict;
import org.apache.ibatis.annotations.Param;


public interface DictDao extends BaseMapper<Dict> {
    /**
     * 获取指定类型的字典最大排序值
     * @param type 字典类型
     * @return 最大排序值
     */
    Integer getMaxSortByType(@Param("type") String type, @Param("id") Long id);
}