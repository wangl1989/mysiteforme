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

    @Cacheable(value = "dictCache",key = "#type",unless = "#result == null or #result.size() == 0")
    @Override
    public List<Dict> getDictByType(String type) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("del_flag",false);
        wrapper.orderByDesc("sort");
        return list(wrapper);
    }

    @Override
    public Integer getCountByType(String type) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("del_flag",false);
        return Math.toIntExact(count(wrapper));
    }

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

    @CacheEvict(value = "dictCache",key = "#dict.type",condition = "#dict.type ne null ")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateDict(Dict dict) {
        saveOrUpdate(dict);
    }

    @CacheEvict(value = "dictCache",key = "#result", condition = "#result ne  null ")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDict(Long id) {
        baseMapper.selectById(id);
        baseMapper.deleteById(id);
    }

    @CacheEvict(value = "dictCache",key = "#type")
    @Override
    public void saveDictList(String type, List<Dict> list) {
        saveOrUpdateBatch(list);
    }

    @CacheEvict(value = "dictCache",key = "#type")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByType(String type) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        remove(wrapper);
    }

    @CacheEvict(value = "dictCache",allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByTableName(String tableName) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.like("description","数据表【"+tableName+"】");
        remove(wrapper);
    }

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
