package com.mysiteforme.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.entity.request.AddUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.PageListUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.UpdateUploadBaseInfoRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.entity.DTO.UploadBaseInfoDTO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.dao.UploadBaseInfoDao;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.UploadType;

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
        return baseMapper.selectPage(new Page<>(request.getPage(), request.getLimit()), wrapper);
    }


    @Override
    public void saveOrUpdateBaseInfo(UploadBaseInfo uploadBaseInfo) {
        if(uploadBaseInfo.getId() != null && uploadBaseInfo.getId() > 0){
            UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectById(uploadBaseInfo.getId());
            if(uploadBaseInfoEntity == null || !uploadBaseInfoEntity.getDelFlag()){
                throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.ID_NOT_EXIST).build();
            }
        }
        checkUploadBaseInfo(uploadBaseInfo.getType(), uploadBaseInfo.getId());
        UploadBaseInfo uploadBaseInfoEntity = new UploadBaseInfo();
        BeanUtils.copyProperties(uploadBaseInfo, uploadBaseInfoEntity);
        uploadBaseInfoEntity.setEnable(true);
        baseMapper.insertOrUpdate(uploadBaseInfoEntity);
        
    }

    @Override
    public void deleteUploadBaseInfo(Long id) {
        UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectById(id);
        if(uploadBaseInfoEntity == null || !uploadBaseInfoEntity.getDelFlag()){
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
    }

    @Override
    public void enableUploadBaseInfo(Long id) {
        UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectById(id);
        if(uploadBaseInfoEntity == null || !uploadBaseInfoEntity.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.ID_NOT_EXIST).build();
        }
        uploadBaseInfoEntity.setEnable(!uploadBaseInfoEntity.getEnable());
        baseMapper.updateById(uploadBaseInfoEntity);
    }

    /**
     * 检查该类型的基础信息是否存在
     * @param type 上传类型
     * @param id 上传id
     */
    private void checkUploadBaseInfo(String type, Long id){
        QueryWrapper<UploadBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        wrapper.eq("del_flag", false);
        if(id != null && id > 0){
            wrapper.ne("id", id);
        }
        UploadBaseInfo uploadBaseInfoEntity = baseMapper.selectOne(wrapper);
        if(uploadBaseInfoEntity != null){
            throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.TYPE_EXISTS).build();
        }
    }

    @Override
    public UploadBaseInfo getInfoByType(String fileUploadType) {
        QueryWrapper<UploadBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", fileUploadType);
        wrapper.eq("del_flag", false);
        return baseMapper.selectOne(wrapper);
    }

    // private void enableUploadBaseInfo(UploadBaseInfo uploadBaseInfoEntity, List<UploadBaseInfo> uploadBaseInfoList){
    //     if(uploadBaseInfoEntity.getEnable()){
    //         // 如果设置状态为启用，则其余类型状态设置为未启用
    //         uploadBaseInfoList.stream().forEach(info -> {
    //             if(info.getType().equalsIgnoreCase(uploadBaseInfoEntity.getType())){
    //                 info.setEnable(true);
    //             }else{
    //                 info.setEnable(false);
    //             }
    //         });
    //     }else{
    //         // 如果设置状态为未启用，则需要判断当前类型是否为本地上传
    //         // 如果类型是本地上传，则需要判断是否有其他可用上传方式
    //         // 如果类型不是本地上传，则需要将本地上传方式启用。
    //         // （当禁用本地上传的方式时，则需要找一个可用的其他上传方式）
    //         if(UploadType.LOCAL.getCode().equalsIgnoreCase(uploadBaseInfoEntity.getType())){
    //             boolean hasOtherUploadService = uploadBaseInfoList.stream()
    //                 .filter(info -> !info.getType().equalsIgnoreCase(UploadType.LOCAL.getCode()))
    //                 .anyMatch(info -> {
    //                     UploadService uploadService = uploadServiceFactory.getUploadService(info.getType());
    //                     if(uploadService == null){
    //                         return false;
    //                     }
    //                     if(uploadService.testBaseInfoAccess(uploadBaseInfoEntity)) {
    //                         info.setEnable(true);
    //                         return true; // 找到符合条件的元素，立即返回true并停止遍历
    //                     }
    //                     return false; // 当前元素不符合条件，继续检查下一个
    //                 });
                
    //             if(!hasOtherUploadService){
    //                 throw MyException.builder().businessError(MessageConstants.UploadBaseInfo.LOCAL_UPLOAD_CAN_NOT_DISABLE).build();
    //             }
    //         }else{
    //             // 如果类型不是本地上传，则只需要将本地上传方式启用。其他方式禁用（当禁用某个上传方式时，则启用本地上传方式）
    //             uploadBaseInfoList.stream().forEach(info -> {
    //                 if(info.getType().equalsIgnoreCase(UploadType.LOCAL.getCode())){
    //                     info.setEnable(true);
    //                 }else{
    //                     info.setEnable(false);
    //                 }
    //             });
    //         }
    //     }

    // }

}