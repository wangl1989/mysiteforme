package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.entity.request.AddUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.PageListUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.UpdateUploadBaseInfoRequest;
import com.mysiteforme.admin.entity.request.UploadBaseInfoRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.config.UploadServiceFactory;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.UploadType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system/uploadBaseInfo")   
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
    public Result add(@RequestBody AddUploadBaseInfoRequest request) {
        if(request == null){
            return Result.objectNotNull();
        }
        UploadBaseInfo uploadBaseInfo = new UploadBaseInfo();
        BeanUtils.copyProperties(request,uploadBaseInfo);
        Result result = checkUploadBaseInfo(uploadBaseInfo);
        if(result !=null && !result.isSuccess()){
            return result;
        }
        uploadBaseInfoService.saveOrUpdateBaseInfo(uploadBaseInfo);
        return Result.success();
    }  

    @SysLog(MessageConstants.SysLog.UPLOAD_BASE_INFO_UPDATE)
    @PutMapping("update")
    public Result update(@RequestBody UpdateUploadBaseInfoRequest request) {
        if(request == null){
            return Result.objectNotNull();
        }
        UploadBaseInfo uploadBaseInfo = new UploadBaseInfo();
        BeanUtils.copyProperties(request,uploadBaseInfo);
        Result result = checkUploadBaseInfo(uploadBaseInfo);
        if(result !=null && !result.isSuccess()){
            return result;
        }
        uploadBaseInfoService.saveOrUpdateBaseInfo(uploadBaseInfo);
        return Result.success();
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
    private Result checkUploadBaseInfo(UploadBaseInfo uploadBaseInfo) {
        try {
            UploadType.valueOf(uploadBaseInfo.getType());
        } catch (IllegalArgumentException e) {
            return Result.paramMsgError(MessageConstants.UploadBaseInfo.TYPE_INVALID);
        }

        if (UploadType.LOCAL.getCode().equals(uploadBaseInfo.getType())) {
            if (StringUtils.isBlank(uploadBaseInfo.getLocalWindowUrl())) {
                return Result.paramMsgError(MessageConstants.UploadBaseInfo.LOCAL_WINDOW_URL_EMPTY);
            }
            if (StringUtils.isBlank(uploadBaseInfo.getLocalLinuxUrl())) {
                return Result.paramMsgError(MessageConstants.UploadBaseInfo.LOCAL_LINUX_URL_EMPTY);
            }
            return Result.success();
        }
        // 特定云存储校验
        switch (UploadType.valueOf(uploadBaseInfo.getType())) {
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
        if (!uploadService.testBaseInfoAccess(uploadBaseInfo)) {
            return Result.paramMsgError(MessageConstants.UploadBaseInfo.TEST_ACCESS_FAILED);
        }

        return Result.success();
    }



}
