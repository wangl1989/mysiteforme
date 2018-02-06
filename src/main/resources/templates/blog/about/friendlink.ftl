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
        <blockquote class="layui-elem-quote sitemap layui-breadcrumb shadow" style="border-left: 5px solid #37C6C0;">
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
                        <p class="aboutinfo-location">
                            <i class="fa fa-close"></i>经常宕机&nbsp;
                            <i class="fa fa-close"></i>不合法规&nbsp;
                            <i class="fa fa-close"></i>插边球站&nbsp;
                            <i class="fa fa-close"></i>红标报毒&nbsp;
                            <i class="fa fa-check"></i>原创优先&nbsp;
                            <i class="fa fa-check"></i>技术优先
                        </p>
                        <hr />
                        <div class="aboutinfo-contact">
                            <p style="font-size:2em;">互换友链，携手并进！</p>
                        </div>
                        </#if>
                        <fieldset class="layui-elem-field layui-field-title">
                            <legend>Friend Link</legend>
                            <div class="layui-field-box">
                                <ul class="friendlink">
                                    <#if (friendlink?? && friendlink?size>0)>
                                        <#list friendlink as item>
                                        <li>
                                            <a target="_blank" href="${item.outLinkUrl}" title="${item.title}" class="friendlink-item">
                                                <p class="friendlink-item-pic"><img src="${item.showPic}" alt="${item.title}" /></p>
                                                <p class="friendlink-item-title">${item.title}</p>
                                                <p class="friendlink-item-domain">${item.subTitle}</p>
                                            </a>
                                        </li>
                                        </#list>
                                    </#if>
                                </ul>
                            </div>
                        </fieldset>
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