<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/resource.css" rel="stylesheet" />
</head>
<body>
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
            <div class="blog-main">
                <div class="child-nav shadow">
                    <span class="child-nav-btn child-nav-btn-this">全部</span>
                    <span class="child-nav-btn">源码</span>
                    <span class="child-nav-btn">工具</span>
                    <span class="child-nav-btn">电子书</span>
                </div>
                <div class="resource-main">
                    <div class="resource shadow">
                        <div class="resource-cover">
                            <a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">
                                <img src="${base}/static/images/timg_meitu_2.jpg" alt="${item.title}" />
                            </a>
                        </div>
                        <h1 class="resource-title"><a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">时光轴</a></h1>
                        <p class="resource-abstract">本博客使用的时光轴的源码，手工打造！</p>
                        <div class="resource-info">
                            <span class="category"><a href="javascript:layer.msg(&#39;这里跳转到相应分类&#39;)"><i class="fa fa-tags fa-fw"></i>&nbsp;源码</a></span>
                            <span class="author"><i class="fa fa-user fa-fw"></i>Absolutely</span>
                            <div class="clear"></div>
                        </div>
                        <div class="resource-footer">
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank"><i class="fa fa-eye fa-fw"></i>演示</a>
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填下载地址&#39;)" target="_blank"><i class="fa fa-download fa-fw"></i>下载</a>
                        </div>
                    </div>
                    <div class="resource shadow">
                        <div class="resource-cover">
                            <a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">
                                <img src="${base}/static/images/timg_meitu_2.jpg" alt="${item.title}" />
                            </a>
                        </div>
                        <h1 class="resource-title"><a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">时光轴</a></h1>
                        <p class="resource-abstract">本博客使用的时光轴的源码，手工打造！</p>
                        <div class="resource-info">
                            <span class="category"><a href="javascript:layer.msg(&#39;这里跳转到相应分类&#39;)"><i class="fa fa-tags fa-fw"></i>&nbsp;源码</a></span>
                            <span class="author"><i class="fa fa-user fa-fw"></i>Absolutely</span>
                            <div class="clear"></div>
                        </div>
                        <div class="resource-footer">
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank"><i class="fa fa-eye fa-fw"></i>演示</a>
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填下载地址&#39;)" target="_blank"><i class="fa fa-download fa-fw"></i>下载</a>
                        </div>
                    </div>
                    <div class="resource shadow">
                        <div class="resource-cover">
                            <a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">
                                <img src="${base}/static/images/timg_meitu_2.jpg" alt="${item.title}" />
                            </a>
                        </div>
                        <h1 class="resource-title"><a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">时光轴</a></h1>
                        <p class="resource-abstract">本博客使用的时光轴的源码，手工打造！</p>
                        <div class="resource-info">
                            <span class="category"><a href="javascript:layer.msg(&#39;这里跳转到相应分类&#39;)"><i class="fa fa-tags fa-fw"></i>&nbsp;源码</a></span>
                            <span class="author"><i class="fa fa-user fa-fw"></i>Absolutely</span>
                            <div class="clear"></div>
                        </div>
                        <div class="resource-footer">
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank"><i class="fa fa-eye fa-fw"></i>演示</a>
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填下载地址&#39;)" target="_blank"><i class="fa fa-download fa-fw"></i>下载</a>
                        </div>
                    </div>
                    <div class="resource shadow">
                        <div class="resource-cover">
                            <a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">
                                <img src="${base}/static/images/timg_meitu_2.jpg" alt="${item.title}" />
                            </a>
                        </div>
                        <h1 class="resource-title"><a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">时光轴</a></h1>
                        <p class="resource-abstract">本博客使用的时光轴的源码，手工打造！</p>
                        <div class="resource-info">
                            <span class="category"><a href="javascript:layer.msg(&#39;这里跳转到相应分类&#39;)"><i class="fa fa-tags fa-fw"></i>&nbsp;源码</a></span>
                            <span class="author"><i class="fa fa-user fa-fw"></i>Absolutely</span>
                            <div class="clear"></div>
                        </div>
                        <div class="resource-footer">
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank"><i class="fa fa-eye fa-fw"></i>演示</a>
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填下载地址&#39;)" target="_blank"><i class="fa fa-download fa-fw"></i>下载</a>
                        </div>
                    </div>
                    <div class="resource shadow">
                        <div class="resource-cover">
                            <a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">
                                <img src="${base}/static/images/timg_meitu_2.jpg" alt="${item.title}" />
                            </a>
                        </div>
                        <h1 class="resource-title"><a href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank">时光轴</a></h1>
                        <p class="resource-abstract">本博客使用的时光轴的源码，手工打造！</p>
                        <div class="resource-info">
                            <span class="category"><a href="javascript:layer.msg(&#39;这里跳转到相应分类&#39;)"><i class="fa fa-tags fa-fw"></i>&nbsp;源码</a></span>
                            <span class="author"><i class="fa fa-user fa-fw"></i>Absolutely</span>
                            <div class="clear"></div>
                        </div>
                        <div class="resource-footer">
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填演示地址&#39;)" target="_blank"><i class="fa fa-eye fa-fw"></i>演示</a>
                            <a class="layui-btn layui-btn-small layui-btn-primary" href="javascript:layer.msg(&#39;这里填下载地址&#39;)" target="_blank"><i class="fa fa-download fa-fw"></i>下载</a>
                        </div>
                    </div>
                    <!-- 清除浮动 -->
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<#include "${base}/blog/common/foot.ftl">
</body>
</html>