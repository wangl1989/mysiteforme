<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- animate.css -->
    <link href="${base}/static/blog/css/animate.min.css" rel="stylesheet" />
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/timeline.css" rel="stylesheet" />
</head>
<body>
<!-- 导航 -->
<#include "${base}/blog/common/nav.ftl">
<!-- 主体（一般只改变这里的内容） -->
<div class="blog-body">
    <div class="blog-container">
        <blockquote class="layui-elem-quote sitemap layui-breadcrumb shadow" style="border-left: 5px solid #37C6C0;">
            <a href="home.html" title="网站首页">网站首页</a>
            <@articleChannelList cid="${channel.id}">
                <#list result as item>
                    <a href="${base}/showBlog${item.href}"><cite>${item.name}</cite></a>
                </#list>
            </@articleChannelList>
        </blockquote>
        <div class="blog-main">
            <div class="child-nav shadow">
                <span class="child-nav-btn child-nav-btn-this">时光轴</span>
                <span class="child-nav-btn">笔记墙</span>
            </div>
            <div class="timeline-box shadow">
                <div class="timeline-main">
                    <h1><i class="fa fa-clock-o"></i>时光如水,岁月静好</h1>
                    <div class="timeline-line"></div>
                    <#if year??>
                        <#assign keys = year?keys />
                        <#if (keys?? && keys?size>0)>
                            <#list keys as key>
                                <div class="timeline-year">
                                    <h2><a class="yearToggle" href="javascript:">${key}年</a><i class="fa fa-caret-down fa-fw"></i></h2>
                                    <#assign monthsMap = year["${key}"] />
                                    <#assign monthsKeys = monthsMap?keys />
                                    <#if (monthsKeys?? && monthsKeys?size>0)>
                                        <#list monthsKeys as month>
                                            <div class="timeline-month">
                                                <h3 class=" animated fadeInLeft"><a class="monthToggle" href="javascript:">${month}月</a><i class="fa fa-caret-down fa-fw"></i></h3>
                                                <ul>
                                                    <#assign daylist = monthsMap["${month}"] />
                                                    <#if (daylist?? && daylist?size>0)>
                                                        <#list daylist as day>
                                                            <li class=" ">
                                                                <div class="h4  animated fadeInLeft">
                                                                    <p class="date">${day.createDate?string("MM月dd日 hh:mm:ss")}</p>
                                                                </div>
                                                                <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                                                <div class="content animated fadeInRight">${day.title}</div>
                                                                <div class="clear"></div>
                                                            </li>
                                                        </#list>
                                                    </#if>
                                                </ul>
                                            </div>
                                        </#list>
                                    </#if>
                                </div>
                            </#list>
                        </#if>
                    </#if>
                    <h1 style="padding-top:4px;padding-bottom:2px;margin-top:40px;"><i class="fa fa-hourglass-end"></i>起航,远方</h1>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">
<!-- 本页脚本 -->
<script type="text/javascript">
    layui.use('jquery', function () {
        var $ = layui.jquery;
        $(function () {
            $('.monthToggle').click(function () {
                $(this).parent('h3').siblings('ul').slideToggle('slow');
                $(this).siblings('i').toggleClass('fa-caret-down fa-caret-right');
            });
            $('.yearToggle').click(function () {
                $(this).parent('h2').siblings('.timeline-month').slideToggle('slow');
                $(this).siblings('i').toggleClass('fa-caret-down fa-caret-right');
            });
        });
    });
</script>
</body>
</html>