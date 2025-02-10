package com.mysiteforme.admin.controller.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    private LuceneSearch luceneSearch;

    public BlogPageController() {
        super();
    }

    @Autowired
    public BlogPageController(LuceneSearch luceneSearch) {
        this.luceneSearch = luceneSearch;
    }

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
     */
    @GetMapping(value = {"/wzzl","/wzzl/**"})
    public String articleParttener(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                   @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                   HttpServletRequest httpServletRequest,Model model) {
        String href = httpServletRequest.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        if(href.endsWith("/")){
            href = href.substring(0,href.length()-1);
        }
        Map<String,Object> map = Maps.newHashMap();
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        if(blogChannel == null){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("请求地址不存在").build();
        }
        if(blogChannel.getParentId() == null){
            map.put("rootId",blogChannel.getParentIds());
        }else {
            map.put("channelId",blogChannel.getId());
        }
        model.addAttribute("channel",blogChannel);
        IPage<BlogArticle> pageList = blogArticleService.selectDetailArticle(map,new Page<>(page,limit));
        model.addAttribute("pagelist",pageList);
        return "blog/article";
    }

    /**
     * 文章搜索
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
     */
    @GetMapping("articleContent/{articleId}")
    public String articleContent(@PathVariable(value = "articleId",required = false)Long articleId,
                                 Model model){
        if(articleId == null || articleId <= 0){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("文章ID不能为空").build();
        }
        BlogArticle article = blogArticleService.selectOneDetailById(articleId);
        if(article == null){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("文章ID不存在").build();
        }
        model.addAttribute("article",article);
        return "blog/articleContent";
    }

    /**
     * 文章评论
     */
    @PostMapping("saveComment")
    @ResponseBody
    public RestResponse add(BlogComment blogComment, HttpServletRequest request){
        RestResponse restResponse = checkBlogComment(blogComment);
        if(restResponse.get("success") != null && !(Boolean)restResponse.get("success")){
            return restResponse;
        }
        //类型隶属于文章评论
        blogComment.setType(Constants.COMMENT_TYPE_ARTICLE_COMMENT);
        String content = blogComment.getContent();
        content = content.replace("\"", "'");
        if(content.length()>1000){
            return RestResponse.failure("您的评论内容太多啦！系统装不下啦！");
        }
        blogComment.setContent(content);
        blogComment.setFloor(blogCommentService.getMaxFloor(blogComment.getArticleId())+1);
        Map<String,String> map = ToolUtil.getOsAndBrowserInfo(request);
        blogComment.setSystem(map.get("os"));
        blogComment.setBrowser(map.get("browser"));
        String ip = ToolUtil.getClientIp(request);
        if("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)){
            ip = "内网地址";
        }
        blogComment.setIp(ip);
        blogCommentService.saveOrUpdateBlogComment(blogComment);
        return RestResponse.success();
    }

    private RestResponse checkBlogComment(BlogComment blogComment){
        if(StringUtils.isBlank(blogComment.getContent())){
            return RestResponse.failure("评论内容不能为空");
        }
        if(StringUtils.isNotEmpty(blogComment.getIp())){
            return RestResponse.failure("非法请求:IP地址为空");
        }
        if(blogComment.getArticleId() == null) {
            return RestResponse.failure("评论文章ID不能为空");
        }
        if(blogComment.getChannelId() == null){
            return RestResponse.failure("文章所在栏目ID不能为空");
        }
        if(blogComment.getFloor() != null && blogComment.getFloor()<=0){
            return RestResponse.failure("非法请求：文章所在楼层错误");
        }
        if(blogComment.getAdminReply() != null){
            return RestResponse.failure("非法请求:管理员是否回复字段为空");
        }
        return RestResponse.success();
    }

    /**
     * 系统留言
     */
    @PostMapping("saveMessage")
    @ResponseBody
    public RestResponse saveMessage(BlogComment blogComment, HttpServletRequest request){
        RestResponse restResponse = checkBlogComment(blogComment);
        if(restResponse.get("success") != null && !(Boolean)restResponse.get("success")){
            return restResponse;
        }
        //隶属于系统留言
        blogComment.setType(Constants.COMMENT_TYPE_LEVING_A_MESSAGE);
        String content = blogComment.getContent();
        content = content.replace("\"", "'");
        if(content.length()>1000){
            return RestResponse.failure("您的留言内容太多啦！系统装不下啦！");
        }
        blogComment.setContent(content);
        blogComment.setFloor(blogCommentService.getMaxFloor(blogComment.getArticleId())+1);
        Map<String,String> map = ToolUtil.getOsAndBrowserInfo(request);
        blogComment.setSystem(map.get("os"));
        blogComment.setBrowser(map.get("browser"));
        String ip = ToolUtil.getClientIp(request);
        if("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)){
            ip = "内网地址";
        }
        blogComment.setIp(ip);
        blogCommentService.save(blogComment);
        return RestResponse.success();
    }

    /**
     * 回复留言
     */
    @PostMapping("replyMessage")
    @ResponseBody
    public RestResponse replyMessage(BlogComment blogComment, HttpServletRequest request){
        RestResponse restResponse = checkBlogComment(blogComment);
        if(restResponse.get("success") != null && !(Boolean)restResponse.get("success")){
            return restResponse;
        }
        if(blogComment.getType() != null){
            return RestResponse.failure("非法请求");
        }

        if(blogComment.getReplyId() == null){
            return RestResponse.failure("回复ID不能为空");
        }
        BlogComment targetComment = blogCommentService.getById(blogComment.getReplyId());
        if(targetComment == null){
            return RestResponse.failure("非法请求");
        }
        //隶属于系统留言
        blogComment.setType(targetComment.getType());
        String content = blogComment.getContent();
        content = content.replace("\"", "'");
        if(content.length()>1000){
            return RestResponse.failure("您的回复内容太多啦！系统装不下啦！");
        }
        blogComment.setContent(content);
        blogComment.setFloor(blogCommentService.getMaxFloorByReply(blogComment.getReplyId())+1);
        Map<String,String> map = ToolUtil.getOsAndBrowserInfo(request);
        blogComment.setSystem(map.get("os"));
        blogComment.setBrowser(map.get("browser"));
        String ip = ToolUtil.getClientIp(request);
        if("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)){
            ip = "内网地址";
        }
        blogComment.setIp(ip);
        blogCommentService.saveOrUpdateBlogComment(blogComment);
        return RestResponse.success().setData(blogComment);
    }

    /**
     * 获取文章评论
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
        IPage<BlogComment> pageData = blogCommentService.getArticleComments(articleId,type,new Page<>(page,limit));
        return RestResponse.success().setData(pageData);
    }

    /**
     * 关于本站 跳转到 他的第一个子栏目
     */
    @GetMapping(value = {"/about","/about"})
    public String redictSunChannel(){
        return "redirect:/showBlog/about/blog";
    }
    /**
     * 关于博客
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
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("请求地址不存在").build();

        }
        model.addAttribute("channel",blogChannel);
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("channel_id",blogChannel.getId());
        wrapper.orderBy(false,false,"is_top","is_recommend");
        List<BlogArticle> list = blogArticleService.list(wrapper);
        if(!list.isEmpty()){
            model.addAttribute("oneArticle",list.get(0));
        }
        if(list.size()>1){
            list.remove(0);
            model.addAttribute("friendlink",list);
        }

        return "blog"+href;
    }

    @GetMapping(value = {"/dddd","/dddd/"})
    @SuppressWarnings("unchecked")
    public String dddd(HttpServletRequest request,Model model){
        String href = request.getRequestURI();
        href = href.replaceFirst("/showBlog","");
        if(href.endsWith("/")){
            href = href.substring(0,href.length()-1);
        }
        BlogChannel blogChannel = blogChannelService.getChannelByHref(href);
        if(blogChannel == null){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("请求地址不存在").build();
        }
        model.addAttribute("channel",blogChannel);
        List<BlogArticle> list = blogArticleService.selectTimeLineList(blogChannel.getId());
        if(!list.isEmpty()){
            Map<Object,Object> yearMap = Maps.newLinkedHashMap();
            for(BlogArticle blogArticle : list){
                Date  d= blogArticle.getCreateDate();
                int year = DateUtil.year(d);
                int monthe = DateUtil.month(d)+1;
                if(yearMap.containsKey(Integer.toString(year))){
                    Map<String,List<BlogArticle>> monthMap = (Map<String,List<BlogArticle>>) yearMap.get(Integer.toString(year));
                    if(monthMap.containsKey(Integer.toString(monthe))){
                        List<BlogArticle> blogArticles = monthMap.get(Integer.toString(monthe));
                        blogArticles.add(blogArticle);
                    }else{
                        List<BlogArticle> blogArticles = Lists.newArrayList();
                        blogArticles.add(blogArticle);
                        monthMap.put(Integer.toString(monthe),blogArticles);
                    }
                }else{
                    Map<String,List<BlogArticle>> monthMap = Maps.newLinkedHashMap();
                    List<BlogArticle> blogArticles = Lists.newArrayList();
                    blogArticles.add(blogArticle);
                    monthMap.put(Integer.toString(monthe),blogArticles);
                    yearMap.put(Integer.toString(year),monthMap);
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
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("请求地址不存在").build();
        }
        model.addAttribute("channel",blogChannel);
        return "blog/share";
    }

}
