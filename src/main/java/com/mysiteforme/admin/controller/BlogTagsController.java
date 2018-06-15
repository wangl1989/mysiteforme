package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.mapper.Condition;
import com.mysiteforme.admin.base.BaseController;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.http.HTMLFilter;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.BlogTags;
import com.mysiteforme.admin.service.BlogTagsService;
import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客标签  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@Controller
@RequestMapping("/admin/blogTags")
public class BlogTagsController extends BaseController{
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogTagsController.class);

    @GetMapping("list")
    @SysLog("跳转博客标签列表")
    public String list(){
        return "/admin/blogTags/list";
    }

    @RequiresPermissions("blog:tags:list")
    @PostMapping("list")
    @ResponseBody
    public LayerData<BlogTags> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<BlogTags> layerData = new LayerData<>();
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isBlank(name)) {
                map.remove("name");
            }
        }
        Page<BlogTags> pageData = blogTagsService.selectTagsPage(map,new Page<BlogTags>(page,limit));
        layerData.setData(pageData.getRecords());
        layerData.setCount(pageData.getTotal());
        return layerData;
    }

    @GetMapping("add")
    public String add(){
        return "/admin/blogTags/add";
    }

    @RequiresPermissions("blog:tags:add")
    @PostMapping("add")
    @SysLog("保存新增博客标签数据")
    @ResponseBody
    public RestResponse add(BlogTags blogTags){
        blogTagsService.insert(blogTags);
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        BlogTags blogTags = blogTagsService.selectById(id);
        model.addAttribute("blogTags",blogTags);
        return "/admin/blogTags/edit";
    }

    @RequiresPermissions("blog:tags:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑博客标签数据")
    public RestResponse edit(BlogTags blogTags){
        if(null == blogTags.getId() || 0 == blogTags.getId()){
            return RestResponse.failure("ID不能为空");
        }
        blogTagsService.updateById(blogTags);
        return RestResponse.success();
    }

    @RequiresPermissions("blog:tags:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除博客标签数据")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return RestResponse.failure("ID不能为空");
        }
        blogTagsService.deleteThisTag(id);
        return RestResponse.success();
    }

    @PostMapping("checkTagName")
    @ResponseBody
    public RestResponse checkTagName(@RequestParam(value = "name",required = false)String name){
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("标签名称不能为空");
        }
        if(blogTagsService.getCountByName(name)>0){
            return RestResponse.failure("标签名称已存在,请重新输入");
        }
        BlogTags blogTags = new BlogTags();
        blogTags.setName(new HTMLFilter().filter(name));
        blogTagsService.saveTag(blogTags);
        return RestResponse.success().setData(blogTags);
    }

}