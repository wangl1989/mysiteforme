package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.dao.DictDao;
import com.mysiteforme.admin.service.DictService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("del_flag",false);
        wrapper.orderBy("sort");
        return selectList(wrapper);
    }

    @Override
    public Integer getCountByType(String type) {
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("del_flag",false);
        return selectCount(wrapper);
    }

    @Override
    public Integer getMaxSortByType(String type) {
        Object o = selectObj(Condition.create().setSqlSelect("max(sort)").eq("type",type));
        int sort = 0;
        if(o != null){
            sort =  (Integer)o + 1;
        }
        return sort;
    }

    @Override
    public Integer getCountByAll(String type, String label, String value) {
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        wrapper.eq("type",type);
        if(StringUtils.isNotBlank(label)){
            wrapper.eq("label",label);
        }
        if(StringUtils.isNotBlank(value)){
            wrapper.eq("value",value);
        }
        wrapper.eq("del_flag",false);
        return selectCount(wrapper);
    }

    @CacheEvict(value = "dictCache",key = "#dict.type",condition = "#dict.type ne null ")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateDict(Dict dict) {
        insertOrUpdate(dict);
    }

    @CacheEvict(value = "dictCache",key = "#result",beforeInvocation = false,condition = "#result ne  null ")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public String deleteDict(Long id) {
        Dict dict = baseMapper.selectById(id);
        baseMapper.deleteById(id);
        return dict.getType();
    }

    @CacheEvict(value = "dictCache",key = "#type",beforeInvocation = false)
    @Override
    public List<Dict> saveDictList(String type, List<Dict> list) {
        insertBatch(list);
        return list;
    }

    @CacheEvict(value = "dictCache",key = "#type",beforeInvocation = false)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void deleteByType(String type) {
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        wrapper.eq("type",type);
        delete(wrapper);
    }

    @CacheEvict(value = "dictCache",allEntries=true)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void deleteByTableName(String tableName) {
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        wrapper.like("description","数据表【"+tableName+"】");
        delete(wrapper);
    }

    @CacheEvict(value = "dictCache",allEntries=true)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void updateByType(String oldType,String newType) {
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        wrapper.eq("type",oldType);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        for (Dict dict : dicts){
            dict.setType(newType);
        }
        updateBatchById(dicts);
    }
}
