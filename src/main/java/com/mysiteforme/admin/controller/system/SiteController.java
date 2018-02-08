package com.mysiteforme.admin.controller.system;


import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2017-12-30
 */
@Controller
@RequestMapping("/admin/system/site")
public class SiteController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteController.class);

    @RequiresPermissions("sys:site:list")
    @GetMapping("show")
    @SysLog("跳转网站展示页面")
    public String show(Model model){
        Site site = siteService.getCurrentSite();
        model.addAttribute("site",site);
        return "admin/system/site/show";
    }

    @RequiresPermissions("sys:site:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存网站基本数据")
    public RestResponse edit(Site site){
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
            site.setRemarks(site.getRemarks().replace("\"", "\'"));
        }
        siteService.updateSite(site);
        return RestResponse.success();
    }
}
