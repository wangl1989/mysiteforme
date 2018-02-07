<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/about.css?t=${.now?long}" rel="stylesheet" />
    <style type="text/css">
        /**管理员回复的图片样式**/
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
                        <div class="aboutinfo-figure">
                            <img src="${base}/static/blog/images/messagewall.png" alt="留言墙" />
                        </div>
                        <p class="aboutinfo-nickname">留言墙</p>
                        <p class="aboutinfo-introduce">本页面可留言、吐槽、提问。欢迎灌水，杜绝广告！</p>
                        <p class="aboutinfo-location"><i class="fa fa-link"></i>&nbsp;&nbsp;<span id="time"></span></p>
                        <hr />
                        <div class="aboutinfo-contact">
                            <p style="font-size:2em;">沟通交流，拉近你我！</p>
                        </div>

                        <fieldset class="layui-elem-field layui-field-title">
                            <legend>Leave a message</legend>
                            <#if (site.openMessage == true)>
                                <div class="layui-field-box">
                                    <div class="leavemessage" style="text-align:initial">
                                        <form class="layui-form blog-editor" action="">
                                            <div class="layui-form-item">
                                                <div id="toolbar" class="toolbar"></div>
                                                <div id="content" class="text"></div>
                                            </div>
                                            <div class="layui-form-item">
                                                <button class="layui-btn" lay-submit="formLeaveMessage" lay-filter="formLeaveMessage">提交留言</button>
                                            </div>
                                        </form>
                                        <ul class="blog-comment" id="commentList">
                                        </ul>
                                    </div>
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
                                            <p class="info info-footer"><span class="time">{{ layui.laytpl.toDateString(item.createDate) }}</span><a class="btn-reply" href="javascript:;" onclick="btnReplyClick(this)">回复</a></p>
                                        </div>
                                        {{# if(item.replayList.length>0){ }}
                                        <hr />
                                        {{#  layui.each(item.replayList, function(index, replyItem){ }}
                                        <div class="comment-child">
                                            <img src="${base}/static/blog/images/Absolutely.jpg" alt="Absolutely" />
                                            <div class="info">
                                                <span class="username">Absolutely</span><span>{{ replyItem.content }}</span>
                                            </div>
                                            <p class="info"><span class="time">{{ layui.laytpl.timeago(replyItem.createDate) }}</span></p>
                                        </div>
                                        {{#  }); }}

                                        {{# } }}

                                        <!-- 回复表单默认隐藏 -->
                                        <div class="replycontainer">
                                            <form class="layui-form" action="">
                                                <input name="replyId" type="hidden" value="{{ item.id }}">
                                                <div class="layui-form-item">
                                                    <textarea name="content" lay-verify="replyContent" placeholder="请输入回复内容" class="layui-textarea" style="min-height:80px;"></textarea>
                                                </div>
                                                <div class="layui-form-item">
                                                    <button class="layui-btn layui-btn-mini" lay-submit="formReply" lay-filter="formReply">提交</button>
                                                </div>
                                            </form>
                                        </div>
                                    </li>
                                    {{#  }); }}
                                </script>
                            </#if>
                        </fieldset>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">
<!-- 本页脚本 -->

<script src="${base}/static/blog/js/about.js?v=1.0"></script>
<script type="text/javascript" src="${base}/static/js/wangEditor.min.js"></script>
<script type="text/javascript" src="${base}/static/js/xss.min.js"  ></script>
<script type="text/javascript" src="${base}/static/js/tools.js?v=${.now?long}"  ></script>
<script>
    function btnReplyClick(elem) {
        var $ = layui.jquery;
        $(elem).parent('p').parent('.comment-parent').siblings('.replycontainer').slideToggle("slow");
        if ($(elem).text() === '回复') {
            $(elem).text('收起')
        } else {
            $(elem).text('回复')
        }
    }
    layui.use(['form','jquery','layer','flow','laytpl'],function(){
        var form      = layui.form,
                $     = layui.jquery,
                E = window.wangEditor,
                flow = layui.flow,
                laytpl    = layui.laytpl,
                layer     = layui.layer;

        <#if (site.openMessage == true)>
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
        // 关闭粘贴样式的过滤
        content_editor.customConfig.pasteFilterStyle = false;
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
                $.post("${base}/showBlog/articleCommentList",{type:2,page:page},function (res) {
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
        form.on('submit(formLeaveMessage)', function (data) {
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
            content = content.replace(/\"/g, "'");
            if(content.length>1000){
                layer.msg("您的留言内容太多啦！系统装不下啦！");
                return false;
            }
            data.field.content = filterXSS(content);
            $.post("${base}/showBlog/saveMessage",data.field,function(res){
                if (res.success){
                    layer.msg("留言成功", { icon: 1 });
                    content_editor.txt.clear();
                    $("#commentList").html("");
                    flow.load(f);
                }else{
                    layer.msg(res.message);
                }
            });
            return false;
        });

        //监听留言回复提交
        form.on('submit(formReply)', function (data) {
            var c = data.field.content;
            if(c === undefined || null == c ||  c === ""){
                layer.msg("写点什么再回复吧");
                return false;
            }
            if(c.length>500){
                layer.msg("回复的太多啦,请精简一下吧");
                return false;
            }
            data.field.content = filterXSS(c);
            $.post("${base}/showBlog/replyMessage",data.field,function(res){
                if (res.success){
                    layer.msg("留言成功", { icon: 1 });
                    $(data.form).find('textarea').val('');
                    var html = '<div class="comment-child"><img src="${base}/static/blog/images/Absolutely.jpg"alt="Absolutely"/><div class="info"><span class="username">Absolutely</span><span>' + data.field.content + '</span></div><p class="info"><span class="time">'+layui.laytpl.toDateString(res.data.createDate)+'</span></p></div>';
                    $(data.form).parent('.replycontainer').siblings('hr').after(html).siblings('.comment-parent').children('p').children('a').click();
                    flow.load(f);
                }else{
                    layer.msg(res.message);
                }
            });

            // var index = layer.load(1);
            // //模拟留言回复
            // setTimeout(function () {
            //     layer.close(index);
            //     var content = data.field.replyContent;
            //     var html = '<div class="comment-child"><img src="../images/Absolutely.jpg"alt="Absolutely"/><div class="info"><span class="username">模拟回复</span><span>' + content + '</span></div><p class="info"><span class="time">2017-03-18 18:26</span></p></div>';
            //     $(data.form).find('textarea').val('');
            //     $(data.form).parent('.replycontainer').before(html).siblings('.comment-parent').children('p').children('a').click();
            //     layer.msg("回复成功", { icon: 1 });
            // }, 500);
            return false;
        });
        </#if>
    });
</script>
</body>
</html>