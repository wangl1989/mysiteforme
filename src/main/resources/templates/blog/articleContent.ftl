<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- 比较好用的代码着色插件 -->
    <link href="${base}/static/blog/css/prettify.css" rel="stylesheet" />
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/detail.css" rel="stylesheet" />
    <style type="text/css">
        .admincss img{
            vertical-align: middle;
            display: inline-block;
            border: none;
            width: auto;
            height: auto;
            margin:0;
            position: unset;
            border-radius: unset;
        }
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
<!-- 导航 -->
<!-- 导航 -->
<nav class="blog-nav layui-header">
    <div class="blog-container">
        <!-- QQ互联登陆 -->
        <a href="javascript:" class="blog-user">
            <i class="fa fa-qq"></i>
        </a>
        <a href="javascript:" class="blog-user layui-hide">
            <img src="${base}/static/blog/images/Absolutely.jpg" alt="Absolutely" title="Absolutely" />
        </a>
        <!-- 不落阁 -->
        <a class="blog-logo" href="${base}/showBlog/index">${site.name}</a>
        <!-- 导航菜单 -->
        <ul class="layui-nav" lay-filter="nav">
                <@mychannel limit="5">
                    <#list result as item>
                        <li class="layui-nav-item <#if (item.href?contains('wzzl'))> layui-this</#if>" >
                            <a href="${base}/showBlog${item.href}"><i class="layui-icon" style="font-size: 18px;">${item.logo}</i>&nbsp;${item.name}</a>
                        </li>
                    </#list>
                </@mychannel>
        </ul>
        <!-- 手机和平板的导航开关 -->
        <a class="blog-navicon" href="javascript:">
            <i class="fa fa-navicon"></i>
        </a>
    </div>
</nav>
<!-- 主体（一般只改变这里的内容） -->
<div class="blog-body">
    <div class="blog-container">
        <blockquote class="layui-elem-quote sitemap layui-breadcrumb shadow" style="border-left: 5px solid #37C6C0;">
            <a class="blog-logo" href="${base}/showBlog/index">${site.name}</a>
            <@articleChannelList cid="${article.channelId}">
                <#list result as item>
                    <a href="${base}/showBlog${item.href}"><cite>${item.name}</cite></a>
                </#list>
            </@articleChannelList>
        </blockquote>
        <div class="blog-main">
            <div class="blog-main-left">
                <!-- 文章内容（使用Kingeditor富文本编辑器发表的） -->
                <div class="article-detail shadow">
                    <div class="article-detail-title">
                        ${article.title}
                    </div>
                    <div class="article-detail-info">
                        <span>创建时间：${article.createDate?string("yyyy-MM-dd hh:mm:ss")}</span>
                        <span>作者：<#if (sysuser(article.createId).nickName??)>${sysuser(article.createId).nickName}<#else>${sysuser(article.createId).loginName}</#if></span>
                        <span>浏览量：${clickNumber(article.id)}</span>
                    </div>
                    <div class="article-detail-content">
                        ${article.content}
                        <@tags aid = "${article.id}">
                            <#if (result?? && result?size>0)>
                        <hr />
                        <p>
                            &nbsp; &nbsp;
                            <#list result as item>
                                    <a class="layui-btn layui-btn-sm">${item.name}</a>
                            </#list>
                        </p>
                            </#if>
                        </@tags>
                    </div>
                </div>
                <#if (article.blogChannel != null && article.blogChannel.canComment == true)>
                    <!-- 评论区域 -->
                <div class="blog-module shadow" style="box-shadow: 0 1px 8px #a6a6a6;">
                    <fieldset class="layui-elem-field layui-field-title" style="margin-bottom:0">
                        <legend>来说两句吧</legend>
                        <div class="layui-field-box">
                            <form class="layui-form blog-editor" action="">
                                <input type="hidden" name="channelId" value="${article.channelId}">
                                <input type="hidden" name="articleId" value="${article.id}">
                                <div class="layui-form-item">
                                    <div id="toolbar" class="toolbar"></div>
                                    <div id="content" class="text"></div>
                                </div>
                                <div class="layui-form-item">
                                    <button class="layui-btn" lay-submit="formRemark" lay-filter="formRemark">提交评论</button>
                                </div>
                            </form>
                        </div>
                    </fieldset>
                    <div class="blog-module-title">文章评论</div>
                    <ul class="blog-comment" id="commentList">
                    </ul>
                </div>
                <script id="demo" type="text/html">
                    {{#  layui.each(d.records, function(index, item){ }}
                    <li>
                        <div class="comment-parent">
                            <img src="${base}/static/blog/images/Absolutely.jpg" alt="absolutely" />
                            <div class="info">
                                <span class="username">Absolutely</span>
                                <span class="layui-badge layui-bg-orange">{{ item.system }}</span>
                                <span class="layui-badge layui-bg-blue">{{ item.browser }}</span>
                                <span class="layui-badge layui-bg-gray">{{ layui.laytpl.timeago(item.createDate) }}</span>
                                <span class="layui-badge layui-bg-black" style="float: right">{{! # !}}{{ item.floor }}</span>
                            </div>
                            <div class="content">
                                {{ item.content }}
                            </div>
                            {{# if(item.adminReply){ }}

                                    <div class="comment-child">
                                        <fieldset class="layui-elem-field" style="border-color:#D0E9FF">
                                            <legend>管理员回复</legend>
                                            <div class="layui-field-box">
                                                {{# if(item.updateUser.icon != ""){ }}
                                                <img style="width: 40px;height: 40px" src="{{ item.updateUser.icon }}" alt="{{# if(item.updateUser.nickName != ''){ }} {{ item.updateUser.nickName }} {{# }else{ }} {{ item.updateUser.loginName }} {{# } }}  " />
                                                {{# }else{ }}
                                                <img style="width: 40px;height: 40px" src="${base}/static/images/face.jpg" alt="{{# if(item.updateUser.nickName != ''){ }} {{ item.updateUser.nickName }} {{# }else{ }} {{ item.updateUser.loginName }} {{# } }}  "  />
                                                {{# } }}
                                                <div class="info">
                                                    <span class="username">{{# if(item.updateUser.nickName != ''){ }} {{ item.updateUser.nickName }} {{# }else{ }} {{ item.updateUser.loginName }} {{# } }} </span>
                                                    <span class="time">{{ layui.laytpl.timeago(item.updateDate) }}</span>
                                                    <span class="admincss">{{ item.replyContent }}</span>
                                                </div>
                                            </div>
                                        </fieldset>
                                    </div>


                            {{# } }}
                        </div>
                    </li>
                    {{#  }); }}
                </script>
                </#if>
            </div>
            <div class="blog-main-right">
                <!--右边悬浮 平板或手机设备显示-->
                <div class="category-toggle"><i class="fa fa-chevron-left"></i></div><!--这个div位置不能改，否则需要添加一个div来代替它或者修改样式表-->
                <div class="article-category shadow">
                    <div class="article-category-title">分类导航</div>
                    <!-- 点击分类后的页面和artile.html页面一样，只是数据是某一类别的 -->
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
                    <div class="blog-module-title">相似文章</div>
                    <ul class="fa-ul blog-module-ul">
                        <@same limit="8" articleid = "${article.id}">
                            <#if (result?size>0)>
                                <#list result as item>
                        <li><i class="fa-li fa fa-hand-o-right"></i><a href="${base+"/showBlog/articleContent/"+item.id}" title="${item.title}">
                            <#if item.title?length lt 18>
                                ${item.title}
                            <#else>
                                ${item.title[0..19]}...
                            </#if>
                        </a></li>
                                </#list>
                            </#if>
                        </@same>
                    </ul>
                </div>
                <div class="blog-module shadow">
                    <div class="blog-module-title">随便看看</div>
                    <ul class="fa-ul blog-module-ul">
                        <@myindex limit="8" order="sort">
                            <#if (result?size>0)>
                                <#list result as item>
                        <li><i class="fa-li fa fa-angle-double-right" style="margin: unset"></i><a href="${base+"/showBlog/articleContent/"+item.id}" title="${item.title}">
                            <#if item.title?length lt 18>
                                ${item.title}
                            <#else>
                                ${item.title[0..19]}...
                            </#if>
                        </a></li>
                                </#list>
                            </#if>
                        </@myindex>
                    </ul>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">
<!-- 比较好用的代码着色插件 -->
<script type="text/javascript" src="${base}/static/blog/js/prettify.js"></script>
<script type="text/javascript" src="${base}/static/js/wangEditor.min.js"></script>
<script type="text/javascript" src="${base}/static/js/xss.min.js"  ></script>
<script type="text/javascript" src="${base}/static/js/tools.js?v=${.now?long}"  ></script>
<script>
    layui.use(['form','jquery','layer','flow','laytpl'],function(){
        var form      = layui.form,
            $     = layui.jquery,
            E = window.wangEditor,
            flow = layui.flow,
            laytpl    = layui.laytpl,
            layer     = layui.layer;

        $(function(){
            $.post("${base}/showBlog/click",{articleId:${article.id}},function (res) {
               if(!res.success){
                   return layer.msg(res.message);
               }
            });
        });
        <#if (article.blogChannel != null && article.blogChannel.canComment == true)>
        var content_editor = new E('#toolbar','#content');
        content_editor.customConfig.menus = [
            'head',
            'bold',
            'italic',
            'underline',
            'strikeThrough',  // 删除线
            'foreColor',  // 文字颜色
            'backColor',  // 背景颜色
            'list',  // 列表
            'justify',  // 对齐方式
            'quote',  // 引用
            'code',  // 插入代码
            'undo',  // 撤销
            'redo'  // 重复
        ];
        content_editor.customConfig.zIndex = 100;
        content_editor.customConfig.customAlert = function (info) {
            // info 是需要提示的内容
            layer.msg(info);
        };
        content_editor.create();
        var f = {
            elem: '#commentList', //流加载容器
            isAuto: false,
            isLazyimg: true,
            done: function(page, next){ //加载下一页
                $.post("${base}/showBlog/articleCommentList",{articleId:${article.id},type:1,page:page},function (res) {
                    if(res.success){
                        var getTpl = demo.innerHTML;
                        laytpl(getTpl).render(res.data, function(html){
                            next(html, page < res.data.pages);
                        });
                    } else{
                        layer.msg(res.message);
                    }
                });
            }
        };
        flow.load(f);
        //监听评论提交
        form.on('submit(formRemark)', function (data) {
            var content = content_editor.txt.html(),
            ct=content_editor.txt.text();
            if(null === ct || "" === ct || undefined === ct){
                layer.msg("来说两句吧");
                return false;
            }
            if(null === content || "" === content || undefined === content){
                layer.msg("来说两句吧");
                return false;
            }
            if(content.length>1000){
                layer.msg("您的评论内容太多啦！系统装不下啦！");
                return false;
            }
            data.field.content = filterXSS(content).replace(/\"/g, "'");
            $.post("${base}/showBlog/saveComment",data.field,function(res){
                if (res.success){
                    layer.msg("评论成功", { icon: 1,time:1000 },function () {
                        content_editor.txt.clear();
                        $("#commentList").html("");
                        flow.load(f);
                    });
                }else{
                    layer.msg(res.message);
                }
            });
            return false;
        });
        </#if>
    });
</script>
</body>
</html>