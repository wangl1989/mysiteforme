<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!--本页样式表-->
    <link href="${base}/static/blog/css/article.css?t=${.now?long}" rel="stylesheet" />
</head>
<body>
<!-- 导航 -->
<#include "${base}/blog/common/nav.ftl">
<!-- 主体（一般只改变这里的内容） -->
<div class="blog-body">
    <div class="blog-container">
        <blockquote class="layui-elem-quote sitemap layui-breadcrumb shadow" style="border-left: 5px solid #37C6C0;">
            <a class="blog-logo" href="${base}/showBlog/index">${site.name}</a>
            <@articleChannelList cid="${channel.id}">
                <#list result as item>
                    <a href="${base}/showBlog${item.href}"><cite>${item.name}</cite></a>
                </#list>
            </@articleChannelList>
        </blockquote>
        <div class="blog-main">
            <div class="blog-main-left">
                <#if pagelist??>
                    <#list pagelist.records as item>
                    <div class="article shadow">
                        <div class="article-left">
                            <#if item.showPic??>
                                <img src="${item.showPic}" alt="${item.title}" />
                            <#else>
                                        <img src="${base}/static/images/timg_meitu_2.jpg" alt="${item.title}" />
                            </#if>
                        </div>
                        <div class="article-right">
                            <div class="article-title">
                                <a href="${base+"/showBlog/articleContent/"+item.id}" target="_blank"><#if (item.title?length>50)>${item.title?substring(0,25)}<#else>${item.title}</#if></a>
                            </div>
                            <div class="article-abstract">
                                ${item.marks}
                            </div>
                        </div>
                        <div class="clear"></div>
                        <div class="article-footer">
                            <span><i class="fa fa-clock-o"></i>&nbsp;&nbsp;${item.publistTime?string("yyyy-MM-dd")}</span>
                            <span class="article-author"><i class="fa fa-user"></i>&nbsp;&nbsp;<#if (sysuser(item.createId).nickName??)>${sysuser(item.createId).nickName}<#else>${sysuser(item.createId).loginName}</#if></span>
                            <span><i class="fa fa-tag"></i>&nbsp;&nbsp;<a href="${base+"/showBlog"+item.blogChannel.href}">${item.blogChannel.name}</a></span>
                            <span class="article-viewinfo"><i class="fa fa-eye"></i>&nbsp;${clickNumber(item.id)}</span>
                            <span class="article-viewinfo"><i class="fa fa-commenting"></i>&nbsp;${item.commentCount}</span>
                        </div>
                    </div>
                    </#list>
                    <hr class="layui-bg-blue" style="margin-top: 40px">
                    <div id="pageDemo" style="text-align: center;"></div>
                </#if>
            </div>
            <div class="blog-main-right">
                <div class="blog-search">
                    <form class="layui-form" action="">
                        <div class="layui-form-item">
                            <div class="search-keywords  shadow">
                                <input type="text" name="keywords" lay-verify="required" placeholder="输入关键词搜索" autocomplete="off" class="layui-input">
                            </div>
                            <div class="search-submit  shadow">
                                <a class="search-btn" lay-submit="formSearch" lay-filter="formSearch"><i class="fa fa-search"></i></a>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="article-category shadow">
                    <div class="article-category-title">分类导航</div>
                    <@mychannel limit="6" pid="2">
                        <#if (result?size>0)>
                            <#list result as item>
                                <#assign myhref = base+"/showBlog"+item.href/>
                            <a href="${myhref}"><i class="layui-icon" style="font-size: 20px; color: white;">${item.logo}</i>&nbsp;${item.name}</a>
                            </#list>
                        </#if>
                    </@mychannel>
                    <div class="clear"></div>
                </div>
                <div class="blog-module shadow">
                    <div class="blog-module-title">作者推荐</div>
                    <ul class="fa-ul blog-module-ul">
                        <@myindex limit="8" order="recommend">
                            <#if (result?size>0)>
                                <#list result as item>
                        <li><i class="fa-li fa fa-angle-double-right" style="margin: unset"></i><a href="${base+"/showBlog/articleContent/"+item.id}" title="${item.title}" target="_blank">
                        <#if item.title?length lt 18>
                            ${item.title}
                        <#else>
                            ${item.title[0..19]}...
                        </#if></a></li>
                                </#list>
                            </#if>
                        </@myindex>
                    </ul>
                </div>
                <#--<div class="blog-module shadow">-->
                    <#--<div class="blog-module-title">随便看看</div>-->
                    <#--<ul class="fa-ul blog-module-ul">-->
                        <#--<@myindex limit="8" order="sort">-->
                            <#--<#if (result?size>0)>-->
                                <#--<#list result as item>-->
                        <#--<li><i class="fa-li fa fa-angle-double-right" style="margin: unset"></i><a href="${base+"/showBlog/articleContent/"+item.id}" target="_blank" title="${item.title}">-->
                        <#--<#if item.title?length lt 18>-->
                            <#--${item.title}-->
                        <#--<#else>-->
                            <#--${item.title[0..19]}...-->
                        <#--</#if></a></li>-->
                                <#--</#list>-->
                            <#--</#if>-->
                        <#--</@myindex>-->
                    <#--</ul>-->
                <#--</div>-->
                <!--右边悬浮 平板或手机设备显示-->
                <div class="category-toggle"><i class="fa fa-chevron-left"></i></div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">

<script id="demo" type="text/html">
    {{# if(d.count>0){ }}
    <div class="shadow" style="text-align:center;font-size:16px;padding:40px 15px;background:#fff;margin-bottom:15px;">
        已搜索到<span style="color: #FF5722;">{{ d.count }}</span>篇与【<span style="color: #FF5722;">{{ d.key }}</span>】有关的文章，用时<span style="color: #FF5722;">{{ d.time }}</span>
    </div>
    {{# }else{ }}
    <div class="shadow" style="text-align:center;font-size:16px;padding:40px 15px;background:#fff;margin-bottom:15px;">
        未搜索到与【<span style="color: #FF5722;">{{ d.key }}</span>】有关的文章，用时<span style="color: #FF5722;">{{ d.time }}</span>
    </div>
    {{# } }}
    {{#  layui.each(d.data, function(index, item){ }}
    <div class="article shadow">
        {{# if(item.showPic != null && item.showPic !=""){ }}
        <div class="article-left">
            <img src="{{ item.showPic }}" alt="{{ item.title }}" />
        </div>
        <div class="article-right">
            <div class="article-title">
                {{#  if(item.title.length > 100){ }}
                <a href='${base}/showBlog/articleContent/{{item.id}}'>{{ item.title.substring(0,100) }}</a>
                {{# }else{ }}
                <a href='${base}/showBlog/articleContent/{{item.id}}'>{{ item.title }}</a>
                {{# } }}
            </div>
            <div class="article-abstract">
                {{item.text}}
            </div>
        </div>
        <div class="clear"></div>
        <div class="article-footer">
            <span><i class="fa fa-clock-o"></i>&nbsp;&nbsp;{{ d.percent }}</span>
        </div>
        {{#  }else{ }}
        <div class="article-right" style="width:100%">
            <div class="article-title">
                {{#  if(item.title.length > 100){ }}
                <a href='${base}/showBlog/articleContent/{{item.id}}'>{{ item.title.substring(0,100) }}</a>
                {{# }else{ }}
                <a href='${base}/showBlog/articleContent/{{item.id}}'>{{ item.title }}</a>
                {{# } }}
            </div>
            <div class="article-abstract" style="text-indent:0em">
                <a>{{item.text}}</a>
            </div>
        </div>
        <div class="clear"></div>
        <div class="article-footer">
            <span>匹配度<i class="fa fa-star" style="margin-left: 5px"></i>&nbsp;&nbsp;{{ item.percent }}</span>
        </div>
        {{# } }}
    </div>
    {{#  }); }}
</script>

<script>
    layui.use(['form','jquery','layer','laytpl','laypage'],function(){
        var form      = layui.form,
                $     = layui.jquery,
            layer     = layui.layer,
            laypage = layui.laypage,
            laytpl    = layui.laytpl;

        laypage.render({
            elem: 'pageDemo'
            ,count: ${pagelist.total}
            ,curr:${pagelist.current}
            ,theme: '#1DB0B8'
            ,limit:10
            ,jump: function(obj, first){
                //obj包含了当前分页的所有参数，比如：
                console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                console.log(obj.limit); //得到每页显示的条数
                if(!first){
                    location.href="${base+"/showBlog"+channel.href}?page="+obj.curr+"&limit="+obj.limit;
                }

            }
        });
        form.on("submit(formSearch)",function(data){
            if(data.field.keywords === undefined || data.field.keywords == null || data.field.keywords === ""){
                layer.msg("搜索关键词不能为空");
                return false;
            }
            $.post("${base}/showBlog/search",data.field,function (res) {
                if(res.success){
                    $(".blog-main-left").html("");
                    var getTpl = demo.innerHTML;
                        laytpl(getTpl).render(res.data, function(html){
                            $(".blog-main-left").html(html);
                        });
                }else {
                    layer.msg(res.mssage);
                }
            });
        });
    });
</script>
</body>
</html>