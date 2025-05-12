/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:22:29
 * @ Description: 字典服务实现类 提供字典的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.dao.DictDao;
import com.mysiteforme.admin.entity.request.AddDictRequest;
import com.mysiteforme.admin.entity.request.PageListDictRequest;
import com.mysiteforme.admin.entity.request.UpdateDictRequest;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.DictService;
import com.mysiteforme.admin.util.MessageConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class DictServiceImpl extends ServiceImpl<DictDao, Dict> implements DictService {

    @Override
    public IPage<Dict> selectPageDict(PageListDictRequest request) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDelFlag,false);
        wrapper.orderBy(true,true,Dict::getType);
        if(request != null){
            if(StringUtils.isNotBlank(request.getType())) {
                wrapper.eq(Dict::getType, request.getType());
            }
            if(StringUtils.isNotBlank(request.getLabel())){
                wrapper.like(Dict::getLabel,request.getLabel());
            }
            if(StringUtils.isNotBlank(request.getValue())){
                wrapper.eq(Dict::getValue,request.getValue());
            }
            if(StringUtils.isNotBlank(request.getDescription())){
                wrapper.like(Dict::getDescription,request.getDescription());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), Dict::getCreateDate);
            wrapper.orderBy(request.getSortBySortAsc() != null, request.getSortBySortAsc() != null && request.getSortBySortAsc(), Dict::getSort);
        } else {
            request = new PageListDictRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

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
    public Integer getMaxSortByType(String type,Long id) {
        return baseMapper.getMaxSortByType(type,id);
    }

    /**
     * 根据类型、标签、值获取字典数量
     * @param type 字典类型
     * @param label 字典标签
     * @param value 字典值
     * @return 字典数量
     */
    @Override
    public Integer getCountByAll(String type, String label, String value,Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        if(StringUtils.isNotBlank(label)){
            wrapper.eq("label",label);
        }
        if(StringUtils.isNotBlank(value)){
            wrapper.eq("value",value);
        }
        if(null != id && 0L != id){
            wrapper.eq("id",id);
        }
        wrapper.eq("del_flag",false);
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public void saveOrUpdateDict(Dict dict) {
        saveOrUpdate(dict);
    }

    @CacheEvict(value = "dictCache",key = "#request.type",condition = "#request.type ne null ")
    @Override
    public void saveDict(AddDictRequest request) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(request,dict);
        saveOrUpdate(dict);
    }

    @CacheEvict(value = "dictCache",key = "#request.type",condition = "#request.type ne null ")
    @Override
    public void updateDict(UpdateDictRequest request) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(request,dict);
        saveOrUpdate(dict);
    }

    /**
     * 删除字典
     * 同时清除对应类型的缓存
     * @param id 字典ID
     */
    @CacheEvict(value = "dictCache",key = "#result", condition = "#id ne  null ")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDict(Long id) {
        Dict dict = baseMapper.selectById(id);
        if(dict == null){
            throw MyException.builder().businessError(MessageConstants.Dict.DICT_NOT_EXIST).build();
        }
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

    @Override
    public List<Dict> getDictTypeList() {
        return lambdaQuery().select(Dict::getType).eq(Dict::getDelFlag,false).groupBy(Dict::getType).list();
    }
}
