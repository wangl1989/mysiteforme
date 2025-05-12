/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:38:00
 * @ Description: 博客评论控制器 提供博客评论的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.mysiteforme.admin.entity.BlogComment;
import com.mysiteforme.admin.service.BlogCommentService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

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
@RequestMapping("/admin/blogComment")
public class BlogCommentController {

    private BlogCommentService blogCommentService;

    public BlogCommentController() {}

    @Autowired
    public BlogCommentController(BlogCommentService blogCommentService) {
        this.blogCommentService = blogCommentService;
    }

    /**
     * 显示评论列表页面
     * @return 列表页面路径
     */
    @GetMapping("list")
    @SysLog("跳转博客评论列表")
    public String list(){
        return "/admin/blogComment/list";
    }

    /**
     * 获取评论列表数据
     * @param page 分页参数
     * @param limit 每页记录数
     * @param request 请求对象
     * @return 分页数据
     */
    @PostMapping("list")
    @ResponseBody
    public LayerData<BlogComment> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<BlogComment> layerData = new LayerData<>();
        QueryWrapper<BlogComment> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String content = (String) map.get("content");
            if(StringUtils.isNotBlank(content)) {
                wrapper.like("content",content);
            }else{
                map.remove("content");
            }

            String ip = (String) map.get("ip");
            if(StringUtils.isNotBlank(ip)) {
                wrapper.like("ip",ip);
            }else{
                map.remove("ip");
            }

            String isAdminReply = (String) map.get("isAdminReply");
            if(StringUtils.isNotBlank(isAdminReply)) {
                if(isAdminReply.equalsIgnoreCase("true")){
                    wrapper.eq("is_admin_reply",true);
                }else if(isAdminReply.equalsIgnoreCase("false")){
                    wrapper.eq("is_admin_reply",false);
                }else{
                    map.remove("isAdminReply");
                }
            }else{
                map.remove("isAdminReply");
            }

        }
        IPage<BlogComment> pageData = blogCommentService.page(new Page<>(page,limit),wrapper);
        layerData.setData(pageData.getRecords());
        layerData.setCount((int)pageData.getTotal());
        return layerData;
    }

    @GetMapping("add")
    public String add(){
        return "/admin/blogComment/add";
    }

    @PostMapping("add")
    @ResponseBody
    public RestResponse add(BlogComment blogComment, HttpServletRequest request){
        if(StringUtils.isBlank(blogComment.getContent())){
            return RestResponse.failure("评论内容不能为空");
        }
        blogCommentService.save(blogComment);
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        BlogComment blogComment = blogCommentService.getById(id);
        model.addAttribute("blogComment",blogComment);
        return "/admin/blogComment/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    public RestResponse edit(BlogComment blogComment){
        if(null == blogComment.getId() || 0 == blogComment.getId()){
            return RestResponse.failure("ID不能为空");
        }
        if(StringUtils.isBlank(blogComment.getContent())){
            return RestResponse.failure("评论内容不能为空");
        }
        blogCommentService.updateById(blogComment);
        return RestResponse.success();
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return 操作结果
     */
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除博客评论数据")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return RestResponse.failure("ID不能为空");
        }
        BlogComment blogComment = blogCommentService.getById(id);
        blogComment.setDelFlag(true);
        blogCommentService.updateById(blogComment);
        return RestResponse.success();
    }

    @PostMapping("adminReplay")
    @ResponseBody
    @SysLog("管理员回复")
    public RestResponse adminReplay(BlogComment blogComment){
        if(null == blogComment.getId() || 0 == blogComment.getId()){
            return RestResponse.failure("ID不能为空");
        }
        if(StringUtils.isBlank(blogComment.getReplyContent())){
            return RestResponse.failure("回复内容不能为空");
        }
        String content = blogComment.getReplyContent().replace("\"", "'");
        blogComment.setReplyContent(content);
        blogComment.setAdminReply(true);
        blogCommentService.saveOrUpdateBlogComment(blogComment);
        return RestResponse.success();
    }

}