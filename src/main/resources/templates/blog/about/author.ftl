<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/about.css?t=${.now?long}" rel="stylesheet" />
</head>
<body>
<!-- 导航 -->
<#include "${base}/blog/common/nav.ftl">
<!-- 主体（一般只改变这里的内容） -->
<div class="blog-body">
    <div class="blog-container">
        <blockquote class="layui-elem-quote sitemap layui-breadcrumb shadow">
            <a class="blog-logo" href="${base}/showBlog/index">${site.name}</a>
            <@articleChannelList cid="${channel.id}">
                <#list result as item>
                    <a href="${base}/showBlog${item.href}"><cite>${item.name}</cite></a>
                </#list>
            </@articleChannelList>
        </blockquote>
        <div class="blog-main">
            <div class="layui-tab layui-tab-brief shadow" lay-filter="tabAbout">
                <ul class="layui-tab-title">
                    <@mychannel limit="4" pid="10">
                        <#if (result?size>0)>
                            <#list result as item>
                                <#assign myhref = base+"/showBlog"+item.href/>
                                <li <#if (item.id == channel.id)>class="layui-this" </#if>><a href="${myhref}">${item.name}</a></li>
                            </#list>
                        </#if>
                    </@mychannel>
                </ul>
                <div class="layui-tab-content">
                    <div class="aboutinfo">
                        <#if oneArticle??>
                        <div class="aboutinfo-figure">
                            <img src="${oneArticle.showPic}" alt="${site.name}" />
                        </div>
                        <p class="aboutinfo-nickname">${oneArticle.title}</p>
                        <p class="aboutinfo-introduce">${oneArticle.subTitle}</p>
                        <p class="aboutinfo-location"><i class="fa fa-location-arrow"></i>&nbsp;${site.address}</p>
                        <hr />
                        <div class="aboutinfo-contact">
                            <a target="_blank" title="QQ交流" href="javascript:layer.msg('启动QQ会话窗口')"><i class="fa fa-qq fa-2x"></i></a>
                            <a target="_blank" title="给我写信" href="javascript:layer.msg('启动邮我窗口')"><i class="fa fa-envelope fa-2x"></i></a>
                            <a target="_blank" title="新浪微博" href="javascript:layer.msg('转到你的微博主页')"><i class="fa fa-weibo fa-2x"></i></a>
                            <a target="_blank" title="码云" href="javascript:layer.msg('转到你的github主页')"><i class="fa fa-git fa-2x"></i></a>
                        </div>

                        <fieldset class="layui-elem-field layui-field-title">
                            <legend>简介</legend>
                            <div class="layui-field-box aboutinfo-abstract">
                            ${oneArticle.content}
                            </div>
                        </fieldset>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">
</body>
</html>