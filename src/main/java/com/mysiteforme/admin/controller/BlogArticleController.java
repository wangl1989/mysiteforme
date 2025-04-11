/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:37:28
 * @ Description: 博客文章控制器 提供博客文章的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.entity.BlogTags;
import com.mysiteforme.admin.entity.VO.ZtreeVO;
import com.mysiteforme.admin.service.BlogArticleService;
import com.mysiteforme.admin.service.BlogChannelService;
import com.mysiteforme.admin.service.BlogTagsService;
import cn.hutool.core.date.DateUtil;

import jakarta.servlet.ServletRequest;

import org.springframework.stereotype.Controller;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin/blogArticle")
public class BlogArticleController extends BaseController{

    public BlogArticleController(BlogArticleService blogArticleService, BlogChannelService blogChannelService, BlogTagsService blogTagsService) {
        this.blogArticleService = blogArticleService;
        this.blogChannelService = blogChannelService;
        this.blogTagsService = blogTagsService;
    }

    /**
     * 显示文章列表页面
     * @return 列表页面路径
     */
    @GetMapping("list")
    public String list(){
        return "/admin/blogArticle/list";
    }

    /**
     * 获取文章列表数据
     * @param limit 每页条数
     * @param page 分页参数
     * @return 分页数据
     */
    @PostMapping("list")
    @ResponseBody
    public LayerData<BlogArticle> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<BlogArticle> layerData = new LayerData<>();
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String title = (String) map.get("title");
            if(StringUtils.isBlank(title)) {
                map.remove("title");
            }
            String category = (String) map.get("category");
            if(StringUtils.isBlank(category)) {
                map.remove("category");
            }
            String beginPublistTime = (String) map.get("beginPublistTime");
            String endPublistTime = (String) map.get("endPublistTime");
            if(StringUtils.isNotBlank(beginPublistTime)) {
                Date begin = DateUtil.parse(beginPublistTime);
                map.put("publist_time",begin);
            }else{
                map.remove("beginPublistTime");
            }
            if(StringUtils.isNotBlank(endPublistTime)) {
                Date end = DateUtil.parse(endPublistTime);
                map.put("publist_time",end);
            }else{
                map.remove("endPublistTime");
            }
            String content = (String) map.get("content");
            if(StringUtils.isBlank(content)) {
                map.remove("content");
            }
            String channelId = (String) map.get("channelId");
            if(StringUtils.isBlank(channelId)){
                map.remove("channelId");
            }

        }
        IPage<BlogArticle> pageData = blogArticleService.selectDetailArticle(map,new Page<>(page,limit));
        layerData.setData(pageData.getRecords());
        layerData.setCount((int)pageData.getTotal());
        return layerData;
    }

    /**
     * 显示文章添加页面
     * @param model 模型对象
     * @return 添加页面路径
     */
    @GetMapping("add")
    public String add(@RequestParam(value = "channelId",required = false)Long channelId, Model model){
        BlogChannel blogChannel = blogChannelService.getById(channelId);
        if(blogChannel != null){
            model.addAttribute("channel",blogChannel);
        }
        List<ZtreeVO> list = blogChannelService.selectZtreeData();
        model.addAttribute("ztreeData", JSONObject.toJSONString(list));
        List<BlogTags> blogTags = blogTagsService.listAll();
        model.addAttribute("taglist",blogTags);
        return "/admin/blogArticle/add";
    }

    /**
     * 保存文章
     * @param blogArticle 文章对象
     * @return 操作结果
     */
    @PostMapping("add")
    @SysLog("保存新增博客文章数据")
    @ResponseBody
    public RestResponse add(@RequestBody BlogArticle blogArticle){
        if(StringUtils.isBlank(blogArticle.getTitle())){
            return RestResponse.failure("标题不能为空");
        }
        if(StringUtils.isBlank(blogArticle.getContent())){
            return RestResponse.failure("内容不能为空");
        }
        if(blogArticle.getChannelId() == null){
            return RestResponse.failure("栏目ID不能为空");
        }
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eqSql("sort","select max(sort) from blog_article")
                .eq("channel_id",blogArticle.getChannelId())
                .eq("del_flag",false);
        List<BlogArticle> articles = blogArticleService.list(wrapper);
        int sort = 0;
        if(articles != null && !articles.isEmpty()){
            sort =  articles.get(0).getSort() +1;
        }
        blogArticle.setSort(sort);
        blogArticleService.saveOrUpdateArticle(blogArticle);
        if(blogArticle.getBlogTags() != null && !blogArticle.getBlogTags().isEmpty()){
            Map<String,Object> map = Maps.newHashMap();
            map.put("articleId",blogArticle.getId());
            map.put("tags",blogArticle.getBlogTags());
            blogArticleService.saveArticleTags(map);
        }
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        BlogArticle blogArticle = blogArticleService.selectOneDetailById(id);
        model.addAttribute("blogArticle",blogArticle);
        List<ZtreeVO> list = blogChannelService.selectZtreeData();
        model.addAttribute("ztreeData", JSONObject.toJSONString(list));
        List<BlogTags> blogTags = blogTagsService.listAll();
        model.addAttribute("taglist",blogTags);
        return "/admin/blogArticle/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑博客内容数据")
    public RestResponse edit(@RequestBody BlogArticle blogArticle){
        if(null == blogArticle.getId() || 0 == blogArticle.getId()){
            return RestResponse.failure("ID不能为空");
        }
        if(StringUtils.isBlank(blogArticle.getTitle())){
            return RestResponse.failure("标题不能为空");
        }
        if(StringUtils.isBlank(blogArticle.getContent())){
            return RestResponse.failure("内容不能为空");
        }
        if(blogArticle.getSort() == null){
            return RestResponse.failure("排序值不能为空");
        }
        blogArticleService.saveOrUpdateArticle(blogArticle);
        blogArticleService.removeArticleTags(blogArticle.getId());
        if(blogArticle.getBlogTags() != null && !blogArticle.getBlogTags().isEmpty()){
            Map<String,Object> map = Maps.newHashMap();
            map.put("articleId",blogArticle.getId());
            map.put("tags",blogArticle.getBlogTags());
            blogArticleService.saveArticleTags(map);
        }
        return RestResponse.success();
    }

    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除博客内容数据")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return RestResponse.failure("ID不能为空");
        }
        BlogArticle blogArticle = blogArticleService.getById(id);
        blogArticle.setDelFlag(true);
        blogArticleService.saveOrUpdateArticle(blogArticle);
        return RestResponse.success();
    }

    @GetMapping("createIndex")
    @ResponseBody
    public RestResponse createIndex() {
        blogArticleService.createArticlIndex();
        return RestResponse.success();
    }

}