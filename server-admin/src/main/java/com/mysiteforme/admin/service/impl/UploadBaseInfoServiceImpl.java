package com.mysiteforme.admin.service.impl;


import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.entity.request.PageListUploadBaseInfoRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.dao.UploadBaseInfoDao;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.UploadType;

import java.util.List;

@Service
public class UploadBaseInfoServiceImpl extends ServiceImpl<UploadBaseInfoDao, UploadBaseInfo> implements UploadBaseInfoService {

    @Override
    public IPage<UploadBaseInfo> selectPageInfo(PageListUploadBaseInfoRequest request) {
        if(request == null){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.QUERY_PARAMS_NULL).build();
        }
        LambdaQueryWrapper<UploadBaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UploadBaseInfo::getDelFlag,false);
        if(StringUtils.isNotBlank(request.getType())) {
            wrapper.eq(UploadBaseInfo::getType, request.getType());
        }
        IPage<UploadBaseInfo> pageData = baseMapper.selectPage(new Page<>(request.getPage(), request.getLimit()), wrapper);
        List<UploadBaseInfo> resultBaseInfoList = pageData.getRecords().stream().peek(info -> {
            if(StringUtils.isNotBlank(info.getSecretKey())){
                info.setSecretKey(DesensitizedUtil.password(info.getSecretKey()));
            }
            if(StringUtils.isNotBlank(info.getAccessKey())){
                info.setAccessKey(DesensitizedUtil.password(info.getAccessKey()));
            }
        }).toList();
        pageData.setRecords(resultBaseInfoList);
        return pageData;
    }


    @CacheEvict(value = "system::upload", key = "#uploadBaseInfo.type", condition = "#uploadBaseInfo.type ne null")
    @Override
    public UploadBaseInfo saveOrUpdateBaseInfo(UploadBaseInfo uploadBaseInfo) {
        if(uploadBaseInfo.getId() != null && uploadBaseInfo.getId() > 0){
            UploadBaseInfo oldInfo = baseMapper.selectById(uploadBaseInfo.getId());
            if(oldInfo == null || oldInfo.getDelFlag()){
                throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.ID_NOT_EXIST).build();
            }
        }
        checkUploadBaseInfo(uploadBaseInfo.getType(), uploadBaseInfo.getId());
        UploadBaseInfo uploadBaseInfoEntity = new UploadBaseInfo();
        BeanUtils.copyProperties(uploadBaseInfo, uploadBaseInfoEntity);
        baseMapper.insertOrUpdate(uploadBaseInfoEntity);
        return uploadBaseInfo;
    }

    @CacheEvict(value = "system::upload", key = "#result.type", condition = "#result.type ne null")
    @Override
    public UploadBaseInfo deleteUploadBaseInfo(Long id) {
        UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectById(id);
        if(uploadBaseInfoEntity == null || uploadBaseInfoEntity.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.ID_NOT_EXIST).build();
        }
        if(uploadBaseInfoEntity.getEnable()){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.UPLOAD_ENABLE_CAN_NOT_DELETE).build();
        }
        if(UploadType.LOCAL.getCode().equalsIgnoreCase(uploadBaseInfoEntity.getType())){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.LOCAL_UPLOAD_CAN_NOT_DELETE).build();
        }
        uploadBaseInfoEntity.setDelFlag(true);
        baseMapper.updateById(uploadBaseInfoEntity);
        return uploadBaseInfoEntity;
    }

    @CacheEvict(value = "system::upload", key = "#result.type", condition = "#result.type ne null")
    @Override
    public UploadBaseInfo enableUploadBaseInfo(Long id) {
        UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectById(id);
        if(uploadBaseInfoEntity == null || !uploadBaseInfoEntity.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.ID_NOT_EXIST).build();
        }
        if(UploadType.LOCAL.getCode().equalsIgnoreCase(uploadBaseInfoEntity.getType()) && uploadBaseInfoEntity.getEnable()){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.LOCAL_UPLOAD_CAN_NOT_DISABLE).build();
        }
        uploadBaseInfoEntity.setEnable(!uploadBaseInfoEntity.getEnable());
        baseMapper.updateById(uploadBaseInfoEntity);
        return uploadBaseInfoEntity;
    }

    /**
     * 检查该类型的基础信息是否存在
     * @param type 上传类型
     * @param id 上传id
     */
    @Override
    public Boolean checkUploadBaseInfo(String type, Long id){
        LambdaQueryWrapper<UploadBaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UploadBaseInfo::getType, type);
        wrapper.eq(UploadBaseInfo::getDelFlag, false);
        if(id != null && id > 0){
            wrapper.ne(UploadBaseInfo::getId, id);
        }
        UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectOne(wrapper);
        return uploadBaseInfoEntity == null;
    }

    @Cacheable(value = "system::upload", key = "#fileUploadType", unless = "#result == null")
    @Override
    public UploadBaseInfo getInfoByType(String fileUploadType) {
        LambdaQueryWrapper<UploadBaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UploadBaseInfo::getType, fileUploadType);
        wrapper.eq(UploadBaseInfo::getDelFlag, false);
        return baseMapper.selectOne(wrapper);
    }

}