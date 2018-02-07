<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/home.css?t=${.now?long}" rel="stylesheet" />
</head>
<body>
<!-- 导航 -->
<#include "${base}/blog/common/nav.ftl">
<!-- 主体（一般只改变这里的内容） -->
<div class="blog-body">
    <!--全屏滚动-->
    <div class="layui-carousel" id="carousel">
        <div carousel-item>
            <@ar channelId="19">
                <#if (result?size>0)>
                    <#list result as items>
                    <div><img src="${items.showPic}"></div>
                    </#list>
                </#if>
            </@ar>
        </div>
    </div>
    <!--end 全屏滚动-->
    <#--<!--为了及时效果需要立即设置canvas宽高，否则就在home.js中设置&ndash;&gt;-->
    <#--<script type="text/javascript">-->
        <#--var canvas = document.getElementById('canvas-banner');-->
        <#--canvas.width = window.document.body.clientWidth - 10;//减去滚动条的宽度-->
        <#--if (screen.width >= 992) {-->
            <#--canvas.height = window.innerHeight * 1 / 3;-->
        <#--} else {-->
            <#--canvas.height = window.innerHeight * 2 / 7;-->
        <#--}-->
    <#--</script>-->
    <!-- 这个一般才是真正的主体内容 -->
    <div class="blog-container">
        <div class="blog-main">
            <!-- 网站公告提示 -->
            <div class="home-tips shadow">
                <i style="float:left;line-height:17px;" class="fa fa-volume-up"></i>
                <div class="home-tips-container">
                    <@ar channelId="6">
                        <#if (result?size>0)>
                            <#list result as items>
                            <span style="color: #37C6C0"><#if (items.length >100)> ${items.content?substring(0,100)} <#else> ${items.content} </#if>></span>
                            </#list>
                        </#if>
                    </@ar>
                </div>
            </div>
            <!--左边文章列表-->
            <div class="layui-anim layui-anim-scaleSpring blog-main-left">
                <@myindex>
                    <#if (result?size>0)>
                        <#list result as item>
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
                                        <a href="${base+"/showBlog/articleContent/"+item.id}"><#if (item.title?length>50)>${item.title?substring(0,25)}<#else>${item.title}</#if></a>
                                    </div>
                                    <div class="article-abstract">
                                        ${item.marks}
                                    </div>
                                </div>
                                <div class="clear"></div>
                                <div class="article-footer">
                                    <span><i class="fa fa-clock-o"></i>&nbsp;&nbsp;${item.createDate?string("yyyy-MM-dd")}</span>
                                    <span class="article-author"><i class="fa fa-user"></i>&nbsp;&nbsp;<#if (sysuser(item.createId).nickName??)>${sysuser(item.createId).nickName}<#else>${sysuser(item.createId).loginName}</#if></span>
                                    <span><i class="fa fa-tag"></i>&nbsp;&nbsp;<a href="${base+"/showBlog"+item.blogChannel.href}">${item.blogChannel.name}</a></span>
                                    <span class="article-viewinfo"><i class="fa fa-eye"></i>&nbsp;${clickNumber(item.id)}</span>
                                    <span class="article-viewinfo"><i class="fa fa-commenting"></i>&nbsp;${commentNumber(item.id)}</span>
                                </div>
                            </div>
                        </#list>
                    </#if>
                </@myindex>

            </div>
            <!--右边小栏目-->
            <div class="blog-main-right">
                <div class="blogerinfo shadow">
                    <div class="layui-anim layui-anim-up blogerinfo-figure">
                        <img src="${site.authorIcon}" alt="${site.author}" style="width: 100px;height: 100px" />
                    </div>
                    <p class="blogerinfo-nickname">${site.author}</p>
                    <p class="blogerinfo-introduce">${site.remarks}</p>
                    <p class="blogerinfo-location"><i class="fa fa-location-arrow"></i>&nbsp;${site.address}</p>
                    <hr />
                    <div class="blogerinfo-contact">
                        <a target="_blank" title="QQ交流" href="javascript:layer.msg('启动QQ会话窗口')"><i class="fa fa-qq fa-2x"></i></a>
                        <a target="_blank" title="给我写信" href="javascript:layer.msg('启动邮我窗口')"><i class="fa fa-envelope fa-2x"></i></a>
                        <a target="_blank" title="新浪微博" href="javascript:layer.msg('转到你的微博主页')"><i class="fa fa-weibo fa-2x"></i></a>
                        <a target="_blank" title="码云" href="javascript:layer.msg('转到你的github主页')"><i class="fa fa-git fa-2x"></i></a>
                    </div>
                </div>
                <div></div><!--占位-->
                <div class="blog-module shadow">
                    <div class="blog-module-title">热文排行</div>
                    <ul class="fa-ul blog-module-ul">
                        <@myindex order="view,publish" limit="6">
                        <#if (result?? && result?size>0)>
                            <#list result as item>
                                <li><i class="fa-li fa fa-angle-double-right" style="margin: unset"></i><a title="${item.title}" href="${base+"/showBlog/articleContent/"+item.id}">
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
                <div class="blog-module shadow">
                    <div class="blog-module-title">最新评论的文章</div>
                    <ul class="fa-ul blog-module-ul">
                        <@nca>
                            <#if (result?? && result?size>0)>
                                <#list result as item>
                                    <li><i class="fa-li fa fa-angle-double-right" style="margin: unset"></i><a href="${base+"/showBlog/articleContent/"+item.id}" target="_blank" title="${item.title}">
                                        <#if item.title?length lt 18>
                                            ${item.title}
                                        <#else>
                                            ${item.title[0..19]}...
                                        </#if></a></li>
                                </#list>
                            </#if>
                        </@nca>
                    </ul>
                </div>
                <#--<div class="blog-module shadow">-->
                    <#--<div class="blog-module-title">一路走来</div>-->
                    <#--<dl class="footprint">-->
                        <#--<dt>2017年03月12日</dt>-->
                        <#--<dd>新增留言回复功能！人人都可参与回复！</dd>-->
                        <#--<dt>2017年03月10日</dt>-->
                        <#--<dd>不落阁2.0基本功能完成，正式上线！</dd>-->
                        <#--<dt>2017年03月09日</dt>-->
                        <#--<dd>新增文章搜索功能！</dd>-->
                        <#--<dt>2017年02月25日</dt>-->
                        <#--<dd>QQ互联接入网站，可QQ登陆发表评论与留言！</dd>-->
                    <#--</dl>-->
                <#--</div>-->
                <#--<div class="blog-module shadow">-->
                    <#--<div class="blog-module-title">后台记录</div>-->
                    <#--<dl class="footprint">-->
                        <#--<dt>2017年03月16日</dt>-->
                        <#--<dd>分页新增页容量控制</dd>-->
                        <#--<dt>2017年03月12日</dt>-->
                        <#--<dd>新增管家提醒功能</dd>-->
                        <#--<dt>2017年03月10日</dt>-->
                        <#--<dd>新增Win10快捷菜单</dd>-->
                    <#--</dl>-->
                <#--</div>-->
                <div class="blog-module shadow">
                    <div class="blog-module-title">友情链接</div>
                    <ul class="blogroll">
                        <@ar channelid = "16" limit="6">
                            <#if (result?? && result?size>1)>
                                <#list result as item>
                                    <#if (item_index>0)>
                                        <li><a target="_blank" href="${item.outLinkUrl}" title="${item.title}">${item.title}</a></li>
                                    </#if>
                                </#list>
                            </#if>
                        </@ar>
                    </ul>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">
<!-- 本页脚本 -->
<script src="${base}/static/blog/js/home.js?t=${.now?long}"></script>
</body>
</html>