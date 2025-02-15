/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:36:35
 * @ Description: 网站控制器 提供网站的增删改查功能(这里可以设置系统通用功能)
 */

package com.mysiteforme.admin.controller.system;


import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.service.UploadInfoService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/system/site")
public class SiteController extends BaseController{

    public SiteController(
            @Qualifier("qiniuService") UploadService qiniuService,
            @Qualifier("ossService") UploadService ossService,
            SiteService siteService,
            UploadInfoService uploadInfoService
    ) {
        this.qiniuService = qiniuService;
        this.ossService = ossService;
        this.siteService = siteService;
        this.uploadInfoService = uploadInfoService;
    }

    @GetMapping("show")
    @SysLog("跳转网站展示页面")
    public String show(Model model){
        Site site = siteService.getCurrentSite();
        model.addAttribute("site",site);
        return "admin/system/site/show";
    }

    @GetMapping("oss")
    public String oss(Model model){
        UploadInfo uploadInfo = uploadInfoService.getOneInfo();
        model.addAttribute("info",uploadInfo);
        return "admin/system/site/oss";
    }

    @PostMapping("editOss")
    @ResponseBody
    @Transactional
    public RestResponse editOss(UploadInfo uploadInfo,HttpServletRequest httpServletRequest){
        Site site = (Site)httpServletRequest.getAttribute("site");
        if(site == null){
            return RestResponse.failure("加载信息错误");
        }
        if(null == uploadInfo.getId() || 0 == uploadInfo.getId()){
            return RestResponse.failure("ID不能为空");
        }
        if(!ossService.testAccess(uploadInfo)){
            return RestResponse.failure("阿里云上传测试检测失败");
        }
        UploadInfo oldInfo = uploadInfoService.getById(uploadInfo.getId());
        uploadInfo.setQiniuDir(oldInfo.getQiniuDir());
        uploadInfoService.updateInfo(uploadInfo);
        site.setFileUploadType("oss");
        siteService.updateSite(site);
        return RestResponse.success();
    }

    @GetMapping("qiniu")
    public String qiniu(Model model){
        UploadInfo uploadInfo = uploadInfoService.getOneInfo();
        model.addAttribute("info",uploadInfo);
        return "admin/system/site/qiniu";
    }

    @PostMapping("editQiniu")
    @ResponseBody
    @Transactional
    public RestResponse editQiniu(UploadInfo uploadInfo,HttpServletRequest httpServletRequest){
        Site site = (Site)httpServletRequest.getAttribute("site");
        if(site == null){
            return RestResponse.failure("加载信息错误");
        }
        if(null == uploadInfo.getId() || 0 == uploadInfo.getId()){
            return RestResponse.failure("ID不能为空");
        }
        if(!qiniuService.testAccess(uploadInfo)){
            return RestResponse.failure("七牛上传测试检测失败");
        }
        UploadInfo oldInfo = uploadInfoService.getById(uploadInfo.getId());
        uploadInfo.setOssDir(oldInfo.getOssDir());
        uploadInfoService.updateInfo(uploadInfo);
        site.setFileUploadType("qiniu");
        siteService.updateSite(site);
        return RestResponse.success();
    }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存网站基本数据")
    public RestResponse edit(Site site,HttpServletRequest httpServletRequest){
        if(site.getId() == null){
            return RestResponse.failure("ID不能为空");
        }
        if(site.getId() != 1){
            return RestResponse.failure("ID不正确");
        }
        if(StringUtils.isBlank(site.getName())){
            return RestResponse.failure("站点名称不能为空");
        }
        if(StringUtils.isNotBlank(site.getRemarks())){
            site.setRemarks(site.getRemarks().replace("\"", "'"));
        }
        if("oss".equals(site.getFileUploadType())){
            UploadInfo uploadInfo = uploadInfoService.getOneInfo();
            if(StringUtils.isBlank(uploadInfo.getOssKeySecret()) ||
               StringUtils.isBlank(uploadInfo.getOssKeyId()) ||
               StringUtils.isBlank(uploadInfo.getOssEndpoint())){
                return RestResponse.failure("阿里云存储信息不能为空");
            }
        }
        if("qiniu".equals(site.getFileUploadType())){
            UploadInfo uploadInfo = uploadInfoService.getOneInfo();
            if(StringUtils.isBlank(uploadInfo.getQiniuAccessKey()) ||
                    StringUtils.isBlank(uploadInfo.getQiniuSecretKey()) ||
                    StringUtils.isBlank(uploadInfo.getQiniuBucketName())){
                return RestResponse.failure("七牛云存储信息不能为空");
            }
        }
        siteService.updateSite(site);
        return RestResponse.success();
    }
}
