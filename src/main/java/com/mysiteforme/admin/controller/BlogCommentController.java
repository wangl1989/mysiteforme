package com.mysiteforme.admin.controller;

import com.mysiteforme.admin.util.ToolUtil;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.BlogComment;
import com.mysiteforme.admin.service.BlogCommentService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客评论  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2018-01-18
 */
@Controller
@RequestMapping("/admin/blogComment")
public class BlogCommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogCommentController.class);

    @Autowired
    private BlogCommentService blogCommentService;

    @GetMapping("list")
    @SysLog("跳转博客评论列表")
    public String list(){
        return "/admin/blogComment/list";
    }

    @PostMapping("list")
    @ResponseBody
    @SysLog("请求博客评论列表数据")
    public LayerData<BlogComment> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<BlogComment> layerData = new LayerData<>();
        EntityWrapper<BlogComment> wrapper = new EntityWrapper<>();
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
        Page<BlogComment> pageData = blogCommentService.selectPage(new Page<>(page,limit),wrapper);
        layerData.setData(pageData.getRecords());
        layerData.setCount(pageData.getTotal());
        return layerData;
    }

    @GetMapping("add")
    @SysLog("跳转新增博客评论页面")
    public String add(){
        return "/admin/blogComment/add";
    }

    @PostMapping("add")
    @ResponseBody
    public RestResponse add(BlogComment blogComment, HttpServletRequest request){
        if(StringUtils.isBlank(blogComment.getContent())){
            return RestResponse.failure("评论内容不能为空");
        }
        blogCommentService.insert(blogComment);
        return RestResponse.success();
    }

    @GetMapping("edit")
    @SysLog("跳转编辑博客评论页面")
    public String edit(Long id,Model model){
        BlogComment blogComment = blogCommentService.selectById(id);
        model.addAttribute("blogComment",blogComment);
        return "/admin/blogComment/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑博客评论数据")
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

    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除博客评论数据")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return RestResponse.failure("ID不能为空");
        }
        BlogComment blogComment = blogCommentService.selectById(id);
        blogComment.setDelFlag(true);
        blogCommentService.updateById(blogComment);
        return RestResponse.success();
    }

}