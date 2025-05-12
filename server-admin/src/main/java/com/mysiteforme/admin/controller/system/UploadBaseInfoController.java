package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.entity.request.AddUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.PageListUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.UpdateUploadBaseInfoRequest;
import com.mysiteforme.admin.util.*;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.config.UploadServiceFactory;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.service.UploadService;

import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/admin/uploadBaseInfo")
@RequiredArgsConstructor
public class UploadBaseInfoController {

    private final UploadBaseInfoService uploadBaseInfoService;

    private final UploadServiceFactory uploadServiceFactory;

    @GetMapping("list")
    public Result list(PageListUploadBaseInfoRequest request) {
        return Result.success(uploadBaseInfoService.selectPageInfo(request));
    }

    @SysLog(MessageConstants.SysLog.UPLOAD_BASE_INFO_ADD)
    @PostMapping("add")
    public Result add(@RequestBody @Valid AddUploadBaseInfoRequest request) {
        if(request == null){
            return Result.objectNotNull();
        }
        if(!uploadBaseInfoService.checkUploadBaseInfo(request.getType(),null)){
            return Result.businessMsgError(MessageConstants.UploadBaseInfo.TYPE_EXISTS);
        }
        UploadBaseInfo uploadBaseInfo = new UploadBaseInfo();
        BeanUtils.copyProperties(request,uploadBaseInfo);
        return saveOrUpdateUploadBaseInfo(uploadBaseInfo);
    }  

    @SysLog(MessageConstants.SysLog.UPLOAD_BASE_INFO_UPDATE)
    @PutMapping("edit")
    public Result edit(@RequestBody @Valid UpdateUploadBaseInfoRequest request) {
        if(request == null){
            return Result.objectNotNull();
        }
        if(!uploadBaseInfoService.checkUploadBaseInfo(request.getType(),request.getId())){
            return Result.businessMsgError(MessageConstants.UploadBaseInfo.TYPE_EXISTS);
        }
        UploadBaseInfo uploadBaseInfo = new UploadBaseInfo();
        BeanUtils.copyProperties(request,uploadBaseInfo);
        return saveOrUpdateUploadBaseInfo(uploadBaseInfo);
    }

    @SysLog(MessageConstants.SysLog.UPLOAD_BASE_INFO_DELETE)
    @DeleteMapping("delete")
    public Result delete(Long id) {
        if(id == null || id <= 0){
            return Result.idIsNullError();
        }
        uploadBaseInfoService.deleteUploadBaseInfo(id);
        return Result.success();
    }

    @SysLog(MessageConstants.SysLog.UPLOAD_BASE_INFO_ENABLE)
    @PutMapping("enable")
    public Result enableUploadBaseInfo(Long id){
        if(id == null || id <= 0){
            return Result.idIsNullError();
        }
        uploadBaseInfoService.enableUploadBaseInfo(id);
        return Result.success();
    }

    private Result saveOrUpdateUploadBaseInfo(UploadBaseInfo uploadBaseInfo) {
        try {
            // 校验上传类型
            UploadType.getByCode(uploadBaseInfo.getType().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Result.paramMsgError(MessageConstants.UploadBaseInfo.TYPE_INVALID);
        }

        if (!UploadType.LOCAL.getCode().equals(uploadBaseInfo.getType())) {
            if(StringUtils.isBlank(uploadBaseInfo.getBucketName())){
                return Result.paramMsgError(MessageConstants.UploadBaseInfo.BUCKET_NAME_EMPTY);
            }
            if(uploadBaseInfo.getId() == null || uploadBaseInfo.getId() == 0) {
                if (StringUtils.isBlank(uploadBaseInfo.getAccessKey())) {
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.ACCESS_KEY_EMPTY);
                }
                if (StringUtils.isBlank(uploadBaseInfo.getSecretKey())) {
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.SECRET_KEY_EMPTY);
                }
            }
        }

        // 特定云存储校验
        switch (Objects.requireNonNull(UploadType.getByCode(uploadBaseInfo.getType()))) {
            case OSS:
                if (StringUtils.isBlank(uploadBaseInfo.getEndpoint())) {
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.ENDPOINT_OSS_EMPTY);
                }
                break;
            case COS:
                if (StringUtils.isBlank(uploadBaseInfo.getRegion())) {
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.REGION_COS_EMPTY);
                }
                break;
            case BITIFUL:
                if (StringUtils.isBlank(uploadBaseInfo.getEndpoint())) {
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.ENDPOINT_BITIFUL_EMPTY);
                }
                if (StringUtils.isBlank(uploadBaseInfo.getRegion())) {
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.REGION_BITIFUL_EMPTY);
                }
                break;
            default:
                break;
        }

        UploadService uploadService = uploadServiceFactory.getUploadService(uploadBaseInfo.getType());
        if (uploadService == null) {
            return Result.paramMsgError(MessageConstants.UploadBaseInfo.TYPE_NOT_USED);
        }
        if (uploadBaseInfo.getId() == null || uploadBaseInfo.getId() == 0) {
            if (!uploadService.testBaseInfoAccess(uploadBaseInfo)) {
                return Result.paramMsgError(MessageConstants.UploadBaseInfo.TEST_ACCESS_FAILED);
            } else {
                uploadBaseInfo.setTestAccess(true);
            }
        } else {
            UploadBaseInfo oldInfo = uploadBaseInfoService.getById(uploadBaseInfo.getId());
            if (oldInfo == null) {
                return Result.paramMsgError(MessageConstants.UploadBaseInfo.ID_NOT_EXIST);
            }
            if (StringUtils.isNotBlank(uploadBaseInfo.getAccessKey())) {
                oldInfo.setAccessKey(uploadBaseInfo.getAccessKey());
            }
            if (StringUtils.isNotBlank(uploadBaseInfo.getSecretKey())) {
                oldInfo.setSecretKey(uploadBaseInfo.getSecretKey());
            }
            // 只有在测试图片地址为空的时候才会自动测试
            if(StringUtils.isBlank(uploadBaseInfo.getTestWebUrl())) {
                if (!uploadService.testBaseInfoAccess(oldInfo)) {
                    CompletableFuture.supplyAsync(() -> {
                        // 这里是异步操作
                        oldInfo.setTestAccess(false);
                        oldInfo.setTestWebUrl(null);
                        return uploadBaseInfoService.saveOrUpdateBaseInfo(oldInfo);
                    });
                    return Result.paramMsgError(MessageConstants.UploadBaseInfo.TEST_ACCESS_FAILED);
                } else {
                    if (StringUtils.isNotBlank(oldInfo.getTestWebUrl())) {
                        uploadBaseInfo.setTestWebUrl(oldInfo.getTestWebUrl());
                    }
                    uploadBaseInfo.setTestAccess(true);
                }
            }
        }
        uploadBaseInfoService.saveOrUpdateBaseInfo(uploadBaseInfo);
        return Result.success();
    }



}
