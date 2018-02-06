package com.mysiteforme.admin.controller.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.entity.BlogComment;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.lucene.LuceneSearch;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.http.HTMLFilter;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2018/1/21.
 * todo:
 */
@RequestMapping("showBlog")
@Controller
public class BlogPageController extends BaseController{

    private static Log log = LogFactory.get();

    @Autowired
    private LuceneSearch luceneSearch;

    @PostMapping("click")
    @ResponseBody
    public RestResponse changeClicks(@RequestParam(value = "articleId",required = false) Long articleId){
        if(articleId == null){
            return RestResponse.failure("文章ID不能为空");
        }
        return RestResponse.success().setData(blogArticleService.flashArticleClick(articleId));
    }

    @GetMapping("test")
    @ResponseBody
    public RestResponse test(Long channelId){
        return RestResponse.success().setData(blogChannelService.getParentsChannel(channelId));
    }

    /**
     * 跳转首页
     * @param httpServletRequest
     * @param model
     * @return
     */
    @GetMapping(value = {"index","","/"})
    public String index(HttpServletRequest httpServletRequest,Model model){
        String href = httpServletRequest.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        model.addAttribute("channel",blogChannel);
        return "blog/index";
    }

    /**
     * 跳转文章专栏列表页
     * @param httpServletRequest
     * @param model
     * @return
     */
    @GetMapping(value = {"/wzzl","/wzzl/**"})
    public String articleParttener(HttpServletRequest httpServletRequest,Model model) {
        String href = httpServletRequest.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        if(href.endsWith("/")){
            href = href.substring(0,href.length()-1);
        }
        Map<String,Object> map = Maps.newHashMap();
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        if(blogChannel == null){
            throw new MyException("地址没找到",404);
        }
        if(blogChannel.getParentId() == null){
            map.put("rootId",blogChannel.getParentIds());
        }else {
            map.put("channelId",blogChannel.getId());
        }
        model.addAttribute("channel",blogChannel);
        Page<BlogArticle> pageList = blogArticleService.selectDetailArticle(map,new Page<BlogArticle>(1,10));
        model.addAttribute("pagelist",pageList);
        return "blog/article";
    }

    /**
     * 文章搜索
     * @param page
     * @param limit
     * @param key
     * @return
     * @throws Exception
     */
    @PostMapping("search")
    @ResponseBody
    public RestResponse searchArticle(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      @RequestParam(value = "keywords",required = false)String key) throws Exception {
        if(StringUtils.isBlank(key)){
            return RestResponse.failure("查询关键词不能为空");
        }
        String[] field = {"title","text"};
        Map<String,Object> data = luceneSearch.search(field,key,new Page<>(page,limit));
        return RestResponse.success().setData(data);
    }

    /**
     * 跳转文章详情
     * @param articleId
     * @param model
     * @return
     */
    @GetMapping("articleContent/{articleId}")
    public String articleContent(@PathVariable(value = "articleId",required = false)Long articleId,
                                 Model model){
        if(articleId == null || articleId <= 0){
            throw new MyException("文章ID不能为空");
        }
        BlogArticle article = blogArticleService.selectOneDetailById(articleId);
        if(article == null){
            throw new MyException("文章ID不存在");
        }
        model.addAttribute("article",article);
        return "blog/articleContent";
    }

    /**
     * 文章评论
     * @param blogComment
     * @param request
     * @return
     */
    @PostMapping("saveComment")
    @ResponseBody
    public RestResponse add(BlogComment blogComment, HttpServletRequest request){
        if(StringUtils.isBlank(blogComment.getContent())){
            return RestResponse.failure("评论内容不能为空");
        }
        if(blogComment.getArticleId() == null) {
            return RestResponse.failure("评论文章ID不能为空");
        }
        if(blogComment.getChannelId() == null){
            return RestResponse.failure("文章所在栏目ID不能为空");
        }
        if(blogComment.getIp() != null){
            return RestResponse.failure("非法请求");
        }
        if(StringUtils.isNotBlank(blogComment.getIp())){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getFloor() != null){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getAdminReply() != null){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getDelFlag()){
            return RestResponse.failure("非法请求");
        }
        if(StringUtils.isNotBlank(blogComment.getRemarks())){
            return RestResponse.failure("非法请求");
        }
        //类型隶属于文章评论
        blogComment.setType(Constants.COMMENT_TYPE_ARTICLE_COMMENT);
        String content = new HTMLFilter().filter(blogComment.getContent());
        content.replace("\"", "'");
        if(content.length()>1000){
            return RestResponse.failure("您的评论内容太多啦！系统装不下啦！");
        }
        blogComment.setFloor(blogCommentService.getMaxFloor(blogComment.getArticleId())+1);
        Map<String,String> map = ToolUtil.getOsAndBrowserInfo(request);
        blogComment.setSystem(map.get("os"));
        blogComment.setBrowser(map.get("browser"));
        String ip = ToolUtil.getClientIp(request);
        if("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)){
            ip = "内网地址";
        }
        blogComment.setIp(ip);
        blogCommentService.insert(blogComment);
        return RestResponse.success();
    }

    /**
     * 系统留言
     * @param blogComment
     * @param request
     * @return
     */
    @PostMapping("saveMessage")
    @ResponseBody
    public RestResponse saveMessage(BlogComment blogComment, HttpServletRequest request){
        if(StringUtils.isBlank(blogComment.getContent())){
            return RestResponse.failure("评论内容不能为空");
        }
        if(blogComment.getArticleId() != null) {
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getChannelId() != null){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getIp() != null){
            return RestResponse.failure("非法请求");
        }
        if(StringUtils.isNotBlank(blogComment.getIp())){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getFloor() != null){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getAdminReply() != null){
            return RestResponse.failure("非法请求");
        }
        if(blogComment.getDelFlag()){
            return RestResponse.failure("非法请求");
        }
        if(StringUtils.isNotBlank(blogComment.getRemarks())){
            return RestResponse.failure("非法请求");
        }
        //隶属于系统留言
        blogComment.setType(Constants.COMMENT_TYPE_LEVING_A_MESSAGE);
        String content = new HTMLFilter().filter(blogComment.getContent());
        content.replace("\"", "'");
        if(content.length()>1000){
            return RestResponse.failure("您的留言内容太多啦！系统装不下啦！");
        }
        blogComment.setFloor(blogCommentService.getMaxFloor(blogComment.getArticleId())+1);
        Map<String,String> map = ToolUtil.getOsAndBrowserInfo(request);
        blogComment.setSystem(map.get("os"));
        blogComment.setBrowser(map.get("browser"));
        String ip = ToolUtil.getClientIp(request);
        if("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)){
            ip = "内网地址";
        }
        blogComment.setIp(ip);
        blogCommentService.insert(blogComment);
        return RestResponse.success();
    }

    /**
     * 获取文章评论
     * @param page
     * @param limit
     * @param articleId
     * @return
     */
    @PostMapping("articleCommentList")
    @ResponseBody
    public RestResponse articleCommentList(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                           @RequestParam(value = "limit",defaultValue = "5")Integer limit,
                                           @RequestParam(value = "articleId",required = false)Long articleId,
                                           @RequestParam(value = "type",required = false)Integer type){
        if(type == null){
            return RestResponse.failure("请求类型不能为空");
        }
        if(type != 1 && type != 2){
            return RestResponse.failure("请求类型错误");
        }
        Page<BlogComment> pageData = blogCommentService.getArticleComments(articleId,type,new Page<BlogComment>(page,limit));
        return RestResponse.success().setData(pageData);
    }

    /**
     * 关于本站 跳转到 他的第一个子栏目
     * @return
     */
    @GetMapping(value = {"/about","/about"})
    public String redictSunChannel(){
        return "redirect:/showBlog/about/blog";
    }
    /**
     * 关于博客
     * @return
     */
    @GetMapping(value = {"/about/**"})
    public String toAbout(HttpServletRequest request,Model model){
        String href = request.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        if(href.endsWith("/")){
            href = href.substring(0,href.length()-1);
        }
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        if(blogChannel == null){
            throw new MyException("地址没找到",404);
        }
        model.addAttribute("channel",blogChannel);
        EntityWrapper<BlogArticle> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("channel_id",blogChannel.getId());
        wrapper.orderBy("is_top",false).orderBy("is_recommend",false);
        List<BlogArticle> list = blogArticleService.selectList(wrapper);
        if(list.size() >0){
            model.addAttribute("oneArticle",list.get(0));
        }
        if(list.size()>1){
            list.remove(0);
            model.addAttribute("friendlink",list);
        }

        return "blog"+href;
    }

    @GetMapping(value = {"/dddd","/dddd/"})
    public String dddd(HttpServletRequest request,Model model){
        String href = request.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        if(href.endsWith("/")){
            href = href.substring(0,href.length()-1);
        }
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        if(blogChannel == null){
            throw new MyException("地址没找到",404);
        }
        model.addAttribute("channel",blogChannel);
        List<BlogArticle> list = blogArticleService.selectTimeLineList(blogChannel.getId());
        if(list.size()>0){
            Map<Object,Object> yearMap = Maps.newLinkedHashMap();
            for(BlogArticle blogArticle : list){
                Date  d= blogArticle.getCreateDate();
                Integer year = DateUtil.year(d);
                Integer monthe = DateUtil.month(d)+1;
                if(yearMap.containsKey(year.toString())){
                    Map<String,List<BlogArticle>> monthMap = (Map<String,List<BlogArticle>>) yearMap.get(year.toString());
                    if(monthMap.containsKey(monthe.toString())){
                        List<BlogArticle> blogArticles = monthMap.get(monthe.toString());
                        blogArticles.add(blogArticle);
                    }else{
                        List<BlogArticle> blogArticles = Lists.newArrayList();
                        blogArticles.add(blogArticle);
                        monthMap.put(monthe.toString(),blogArticles);
                    }
                }else{
                    Map<String,List<BlogArticle>> monthMap = Maps.newLinkedHashMap();
                    List<BlogArticle> blogArticles = Lists.newArrayList();
                    blogArticles.add(blogArticle);
                    monthMap.put(monthe.toString(),blogArticles);
                    yearMap.put(year.toString(),monthMap);
                }
            }
            model.addAttribute("year",yearMap);
        }
        return "blog/timeline";
    }

    @GetMapping(value = {"/share","/share/"})
    public String rescourceShare(HttpServletRequest request,Model model){
        String href = request.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        if(href.endsWith("/")){
            href = href.substring(0,href.length()-1);
        }
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        if(blogChannel == null){
            throw new MyException("地址没找到",404);
        }
        model.addAttribute("channel",blogChannel);
        return "blog/share";
    }

}
