<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>阿里云信息--${site.name}</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <!-- 页面描述 -->
    <meta name="description" content="${site.description}"/>
    <!-- 页面关键词 -->
    <meta name="keywords" content="${site.keywords}"/>
    <!-- 网页作者 -->
    <meta name="author" content="${site.author}"/>
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
        .layui-form[wid100] .layui-form-label {
            width: 110px;
        }
        .layui-form[wid100] .layui-input-block {
            margin-left: 140px;
        }
    </style>
</head>
<body class="childrenBody">
<fieldset class="layui-elem-field">
    <legend>阿里云上传</legend>
    <div class="layui-field-box">
        这里保存的是你阿里云上传的一些关键信息<br/>
        <span class="layui-badge-dot"></span>为必填项
    </div>
</fieldset>
<form class="layui-form" wid100>
    <input type="hidden" value="${info.id}" name = "id">
    <input type="hidden" value="${info.qiniuAccessKey}" name = "qiniuAccessKey" id="qiniuAccessKey">
    <div class="layui-form-item">
        <label class="layui-form-label">Bucket域名<span class="layui-badge-dot"></span></label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="ossBasePath" value="${info.ossBasePath}" lay-verify="required" placeholder="请输入阿里云Bucket 域名或者自有自有域名">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">Bucket目录<span class="layui-badge-dot"></span></label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="ossBucketName" value="${info.ossBucketName}" lay-verify="required" placeholder="请输入阿里云Bucket目录">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">存储目录</label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="ossDir" value="${info.ossDir}"  placeholder="请输入存储目录">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">KEY_ID<span class="layui-badge-dot"></span></label>
        <div class="layui-input-block">
            <input type="password" class="layui-input" name="ossKeyId" value="${info.ossKeyId}" lay-verify="required" placeholder="请输入阿里云ACCESS_KEY_ID">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">KEY_SECRET<span class="layui-badge-dot"></span></label>
        <div class="layui-input-block">
            <input type="password" class="layui-input" name="ossKeySecret" value="${info.ossKeySecret}" lay-verify="required" placeholder="请输入阿里云ACCESS_KEY_SECRET">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">ENDPOINT<span class="layui-badge-dot"></span></label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="ossEndpoint" value="${info.ossEndpoint}" lay-verify="required" placeholder="请输入阿里云ENDPOINT">
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addUser">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    layui.use(['form','jquery','layer'],function(){
       var form = layui.form,
           $    = layui.jquery,
           layer = layui.layer,
           delFlage = false;    //默认启用用户

        form.on("submit(addUser)",function(data){
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.ajax({
                type:"POST",
                url:"${base}/admin/system/site/editOss",
                data:data.field,
                success:function(res){
                    layer.close(loadIndex);
                    if(res.success){
                        parent.layer.msg("阿里云信息更新成功!",{time:1500},function(){
                            //刷新父页面
                            parent.layer.closeAll();
                        });
                    }else{
                        parent.layer.msg(res.message,{time:1500},function(){
                            //刷新父页面
                            parent.location.reload();
                        });
                    }
                }
            });
            return false;
        });

    });
</script>
</body>
</html>