<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>管理员回复--${site.name}</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <meta name="description" content="${site.description}"/>
    <meta name="keywords" content="${site.keywords}"/>
    <meta name="author" content="${site.author}"/>
    <link rel="icon" href="${site.logo}">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <style type="text/css">
        .layui-form-item .layui-inline{ width:33.333%; float:left; margin-right:0; }
        @media(max-width:1240px){
            .layui-form-item .layui-inline{ width:100%; float:none; }
        }
        .layui-form-item .role-box {
            position: relative;
        }
        .layui-form-item .role-box .jq-role-inline {
            height: 100%;
            overflow: auto;
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
<body class="childrenBody">
<form class="layui-form" style="width:80%;">
    <input value="${blogComment.id}" name="id" type="hidden">
    <div class="layui-form-item">
        <label class="layui-form-label">评论内容</label>
        <div class="layui-input-block">
            ${blogComment.content}
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">ip</label>
            <div class="layui-input-inline">
            <#if (blogComment.ip?? && blogComment.ip != "")>
                <span class="layui-badge layui-bg-blue">${blogComment.ip}</span>
            </#if>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">操作系统</label>
            <div class="layui-input-inline">
                <span class="layui-badge layui-bg-green">${blogComment.system}</span>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">浏览器</label>
            <div class="layui-input-inline">
                <span class="layui-badge layui-bg-cyan">${blogComment.browser}</span>
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">管理员回复内容</label>
        <div class="layui-input-block">
            <div id="content">${blogComment.replyContent}</div>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addBlogComment">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/xss.min.js"  ></script>
<script type="text/javascript" src="${base}/static/js/wangEditor.min.js"></script>
<script>
    layui.use(['form','jquery','layer'],function(){
        var form      = layui.form,
                $     = layui.jquery,
                E = window.wangEditor,
                layer = layui.layer;

        var content_editor = new E('#content');
        //图片上传
        content_editor.customConfig.uploadImgServer = '${base}/file/uploadWang';
        content_editor.customConfig.uploadFileName = 'test';
        // 自定义处理粘贴的文本内容(这里处理图片抓取)
        content_editor.customConfig.pasteTextHandle = function (content) {
            if(undefined === content){
                return content;
            }
            if(content.indexOf("src=")<=0){
                return content;
            }
            var loadContent = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.ajax({
                url: "${base}/file/doContent/",
                type: "POST",
                async: false,
                data:{"content":content},
                dataType: "json",
                success:function(res){
                    layer.close(loadContent);
                    content = res.data;
                }
            });
            return content;
        };
        // 关闭粘贴样式的过滤
        content_editor.customConfig.pasteFilterStyle = false;
        content_editor.customConfig.customAlert = function (info) {
            // info 是需要提示的内容
            layer.msg(info);
        };
        content_editor.create();
        form.on("submit(addBlogComment)",function(data){
            //编辑器数据
            var c = content_editor.txt.html(),
                    ct=content_editor.txt.text();
            if(null === ct || "" === ct || undefined === ct){
                layer.msg("回复内容不能为空");
                return false;
            }
            if(null === c || "" === c || undefined === c){
                layer.msg("回复内容不能为空");
                return false;
            }
            data.field.replyContent = filterXSS(c).replace(/\"/g, "'");
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            //给角色赋值
            $.post("${base}/admin/blogComment/adminReplay",data.field,function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("回复成功！",{time:1000},function(){
                        parent.layer.closeAll();
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message);
                }
            });
            return false;
        });

    });
</script>
</body>
</html>