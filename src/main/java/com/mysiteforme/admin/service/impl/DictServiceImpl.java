package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.dao.DictDao;
import com.mysiteforme.admin.service.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2017-11-26
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DictServiceImpl extends ServiceImpl<DictDao, Dict> implements DictService {

    /**
     * 根据字典类型获取字典列表
     * 结果会被缓存
     * @param type 字典类型
     * @return 字典列表
     */
    @Cacheable(value = "dictCache",key = "#type",unless = "#result == null or #result.size() == 0")
    @Override
    public List<Dict> getDictByType(String type) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("del_flag",false);
        wrapper.orderByDesc("sort");
        return list(wrapper);
    }

    /**
     * 获取指定类型的字典数量
     * @param type 字典类型
     * @return 字典数量
     */
    @Override
    public Integer getCountByType(String type) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("del_flag",false);
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 获取指定类型下的最大排序值
     * @param type 字典类型
     * @return 最大排序值加1
     */
    @Override
    public Integer getMaxSortByType(String type) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eqSql("sort","select max(sort) from dict ")
                .eq("type",type)
                .eq("del_flag",false);
        Dict dict = getOne(queryWrapper);
        int sort = 0;
        if(dict != null){
            sort =  dict.getSort() + 1;
        }
        return sort;
    }

    /**
     * 根据类型、标签、值获取字典数量
     * @param type 字典类型
     * @param label 字典标签
     * @param value 字典值
     * @return 字典数量
     */
    @Override
    public Integer getCountByAll(String type, String label, String value) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        if(StringUtils.isNotBlank(label)){
            wrapper.eq("label",label);
        }
        if(StringUtils.isNotBlank(value)){
            wrapper.eq("value",value);
        }
        wrapper.eq("del_flag",false);
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 保存或更新字典
     * 同时清除对应类型的缓存
     * @param dict 字典对象
     */
    @CacheEvict(value = "dictCache",key = "#dict.type",condition = "#dict.type ne null ")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateDict(Dict dict) {
        saveOrUpdate(dict);
    }

    /**
     * 删除字典
     * 同时清除对应类型的缓存
     * @param id 字典ID
     */
    @CacheEvict(value = "dictCache",key = "#result", condition = "#result ne  null ")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDict(Long id) {
        baseMapper.selectById(id);
        baseMapper.deleteById(id);
    }

    /**
     * 批量保存字典列表
     * 同时清除对应类型的缓存
     * @param type 字典类型
     * @param list 字典列表
     */
    @CacheEvict(value = "dictCache",key = "#type")
    @Override
    public void saveDictList(String type, List<Dict> list) {
        saveOrUpdateBatch(list);
    }

    /**
     * 根据类型删除字典
     * 同时清除对应类型的缓存
     * @param type 字典类型
     */
    @CacheEvict(value = "dictCache",key = "#type")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByType(String type) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        remove(wrapper);
    }

    /**
     * 根据表名删除相关字典
     * 同时清除所有字典缓存
     * @param tableName 表名
     */
    @CacheEvict(value = "dictCache",allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByTableName(String tableName) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.like("description","数据表【"+tableName+"】");
        remove(wrapper);
    }

    /**
     * 更新字典类型
     * 同时清除所有字典缓存
     * @param oldType 原字典类型
     * @param newType 新字典类型
     */
    @CacheEvict(value = "dictCache",allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateByType(String oldType,String newType) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",oldType);
        List<Dict> dits = baseMapper.selectList(wrapper);
        for (Dict dict : dits){
            dict.setType(newType);
        }
        updateBatchById(dits);
    }
}
