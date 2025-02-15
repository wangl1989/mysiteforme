/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:38:15
 * @ Description: 博客标签控制器 提供博客标签的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.base.BaseController;
import com.xiaoleilu.hutool.http.HTMLFilter;

import jakarta.servlet.ServletRequest;

import org.springframework.stereotype.Controller;
import com.mysiteforme.admin.entity.BlogTags;
import com.mysiteforme.admin.service.BlogTagsService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@Controller
@RequestMapping("/admin/blogTags")
public class BlogTagsController extends BaseController{

    public BlogTagsController(BlogTagsService blogTagsService) {
        this.blogTagsService = blogTagsService;
    }   

    /**
     * 显示标签列表页面
     * @return 列表页面路径
     */
    @GetMapping("list")
    public String list(){
        return "/admin/blogTags/list";
    }

    /**
     * 获取标签列表数据
     * @param limit 每页条数
     * @param page 分页参数
     * @return 分页数据
     */
    @PostMapping("list")
    @ResponseBody
    public LayerData<BlogTags> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<BlogTags> layerData = new LayerData<>();
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isBlank(name)) {
                map.remove("name");
            }
        }
        IPage<BlogTags> pageData = blogTagsService.selectTagsPage(map,new Page<>(page,limit));
        layerData.setData(pageData.getRecords());
        layerData.setCount((int)pageData.getTotal());
        return layerData;
    }

    /**
     * 显示标签添加页面
     * @return 添加页面路径
     */
    @GetMapping("add")
    public String add(){
        return "/admin/blogTags/add";
    }

    /**
     * 保存标签
     * @param blogTags 标签对象
     * @return 操作结果
     */
    @PostMapping("add")
    @SysLog("保存新增博客标签数据")
    @ResponseBody
    public RestResponse add(BlogTags blogTags){
        blogTagsService.save(blogTags);
        return RestResponse.success();
    }

    /**
     * 编辑标签
     * @param id 标签ID
     * @param model 模型对象
     * @return 编辑页面路径
     */
    @GetMapping("edit")
    public String edit(Long id,Model model){
        BlogTags blogTags = blogTagsService.getById(id);
        model.addAttribute("blogTags",blogTags);
        return "/admin/blogTags/edit";
    }

    /**
     * 更新标签
     * @param blogTags 标签对象
     * @return 操作结果
     */
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

    /**
     * 删除标签
     * @param id 标签ID
     * @return 操作结果
     */
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