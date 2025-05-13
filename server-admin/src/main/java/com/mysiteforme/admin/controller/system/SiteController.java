/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:36:35
 * @ Description: 网站控制器 提供网站的增删改查功能(这里可以设置系统通用功能)
 */

package com.mysiteforme.admin.controller.system;


import cn.hutool.core.util.DesensitizedUtil;
import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.util.LimitType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.config.UploadServiceFactory;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/site")
@RequiredArgsConstructor
@RateLimit(limit = 10, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class SiteController{

    private final SiteService siteService;

    private final UploadServiceFactory uploadServiceFactory;

    private final UploadBaseInfoService uploadBaseInfoService;

    @GetMapping("current")
    public Result current(){
        Site site = siteService.getCurrentSite();
        if(StringUtils.isNotBlank(site.getPhone())){
            site.setPhone(DesensitizedUtil.mobilePhone(site.getPhone()));
        }
        if (StringUtils.isNotBlank(site.getEmail())) {
            site.setEmail(DesensitizedUtil.email(site.getEmail()));
        }
        if (StringUtils.isNotBlank(site.getWebServicekey())) {
            site.setWebServicekey(DesensitizedUtil.password(site.getWebServicekey()));
        }
        return Result.success(site);
    }

    @SysLog(MessageConstants.SysLog.SITE_EDIT)
    @PutMapping("edit")
    public Result edit(@RequestBody Site site){
        if(site.getId() == null){
            return Result.idIsNullError();
        }
        if(site.getId() != 1L){
            return Result.businessMsgError(MessageConstants.Site.SITE_ID_NOT_CORRECT) ;
        }
        if(StringUtils.isBlank(site.getName())){
            return Result.businessMsgError(MessageConstants.Site.SITE_NAME_EMPTY);
        }
        if(StringUtils.isNotBlank(site.getRemarks())){
            site.setRemarks(site.getRemarks().replace("\"", "'"));
        }
        if(StringUtils.isBlank(site.getFileUploadType())){
            return Result.businessMsgError(MessageConstants.Site.FILE_UPLOAD_TYPE_EMPTY);
        }
        if(StringUtils.isNotBlank(site.getWebServicekey())) {
            if(site.getWebServicekey().contains("*")) {
                site.setWebServicekey(null);
            } else {
                if (!siteService.checkWebService(site.getWebServicekey())) {
                    return Result.businessMsgError(MessageConstants.Site.SITE_CHECK_WEB_SERVICE_KEY_ERROR);
                }
            }
        }
        if(StringUtils.isNotBlank(site.getEmail())){
            if(site.getEmail().contains("*")){
                site.setEmail(null);
            }
        }
        if(StringUtils.isNotBlank(site.getPhone())){
            if(site.getPhone().contains("*")){
                site.setPhone(null);
            }
        }
        UploadService uploadService = uploadServiceFactory.getUploadService(site.getFileUploadType());
        if(uploadService == null){
            return Result.businessMsgError(MessageConstants.Site.FILE_UPLOAD_TYPE_NOT_CORRECT);
        }
        UploadBaseInfo uploadBaseInfo = uploadBaseInfoService.getInfoByType(site.getFileUploadType());
        if(uploadBaseInfo == null){
            return Result.businessMsgError(MessageConstants.Site.FILE_UPLOAD_TYPE_NOT_CORRECT);
        }
        Site oldSite = siteService.getCurrentSite();
        // 如果原先的网站设置里的上传方式和现在的不一致，才会对新的上传方式进行校验
        if(!oldSite.getFileUploadType().equalsIgnoreCase(site.getFileUploadType())) {
            if (!uploadService.testBaseInfoAccess(uploadBaseInfo)) {
                return Result.businessMsgError(MessageConstants.Site.FILE_UPLOAD_TYPE_NOT_CORRECT);
            }
        }

        siteService.updateSite(site);
        return Result.success();
    }

    @GetMapping("uploadTypeList")
    public Result uploadTypeList(){
        return Result.success(siteService.getSiteUploadTypeList());
    }
}
