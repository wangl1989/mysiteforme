<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
<!-- 本页样式表 -->
<link href="${base}/static/blog/css/about.css?t=${.now?long}" rel="stylesheet" />
<style type="text/css">
    /**编辑器样式**/
    .toolbar {
        border: 1px solid #ccc;
        border-bottom: none;
    }
    .text {
        border: 1px solid #ccc;
        height: 200px;
    }
    /* table 样式 */
    table {
        border-top: 1px solid #ccc;
        border-left: 1px solid #ccc;
    }
    table td,
    table th {
        border-bottom: 1px solid #ccc;
        border-right: 1px solid #ccc;
        padding: 3px 5px;
    }
    table th {
        border-bottom: 2px solid #ccc;
        text-align: center;
    }

    /* blockquote 样式 */
    blockquote {
        display: block;
        border-left: 8px solid #d0e5f2;
        padding: 5px 10px;
        margin: 10px 0;
        line-height: 1.4;
        font-size: 100%;
        background-color: #f1f1f1;
    }

    /* code 样式 */
    code {
        display: inline-block;
        *display: inline;
        *zoom: 1;
        background-color: #f1f1f1;
        border-radius: 3px;
        padding: 3px 5px;
        margin: 0 3px;
    }
    pre code {
        display: block;
    }

</style>
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
            <div class="layui-tab layui-tab-brief shadow" lay-filter="tabAbout">
                <ul class="layui-tab-title">
                    <@mychannel limit="4" pid="${channel.parentId}">
                        <#if (result?size>0)>
                            <#list result as item>
                                <#assign myhref = base+"/showBlog"+item.href/>
                                <li <#if (item.id == channel.id)>class="layui-this" </#if>><a href="${myhref}">${item.name}</a></li>
                            </#list>
                        </#if>
                    </@mychannel>
                </ul>
                <div class="layui-tab-content">
                    <div class="layui-tab-item" style="display: block">
                        <#if oneArticle??>
                        <div class="aboutinfo">
                            <div class="aboutinfo-figure">
                                <img style="border-radius: unset;" src="${oneArticle.showPic}" alt="${site.name}" />
                            </div>
                            <p class="aboutinfo-nickname">${oneArticle.title}</p>
                            <p class="aboutinfo-introduce">${oneArticle.subTitle}</p>
                            <p class="aboutinfo-location"><i class="fa fa-link"></i>&nbsp;&nbsp;<a target="_blank" href="${site.url}">${site.url}</a></p>
                            <hr />
                            <div class="aboutinfo-contact">
                                <@mychannel limit="5">
                                    <#list result as item>
                                <a target="_blank" title="${item.name}" href="${base}/showBlog${item.href}"><i class="layui-icon" style="font-size:2.5em;position:relative;top:3px">${item.logo}</i></a>
                                    </#list>
                                </@mychannel>
                            </div>
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