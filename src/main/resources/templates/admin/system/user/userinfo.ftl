<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>个人资料--${site.name}</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel = "shortcut icon" href="${site.logo}">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <link rel="stylesheet" href="${base}/static/css/user.css?v=2.0" media="all" />
</head>
<body class="childrenBody">
<form class="layui-form">
    <div class="user_left">
        <input class="layui-hide" name="id" value="${userinfo.id}"/>
        <div class="layui-form-item">
            <label class="layui-form-label">用户名</label>
            <div class="layui-input-block">
                <input type="text" value="${userinfo.loginName}" name="loginName" disabled class="layui-input layui-disabled">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">昵称</label>
            <div class="layui-input-block">
                <input type="text" value="${userinfo.nickName}" name="nickName" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">用户角色</label>
            <div class="layui-input-block">
                <#list userRole as item>
                    <input type="checkbox"  disabled title="${item.name}" checked>
                </#list>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">手机号码</label>
            <div class="layui-input-block">
                <input type="tel" value="${userinfo.tel}" name="tel" placeholder="请输入手机号码" lay-verify="phone" class="layui-input userPhone">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">邮箱</label>
            <div class="layui-input-block">
                <input type="text" value="${userinfo.email}" name="email" placeholder="请输入邮箱" lay-verify="email" class="layui-input userEmail">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">自我介绍</label>
            <div class="layui-input-block">
                <textarea placeholder="请输入内容" class="layui-textarea myself" name="remarks">${userinfo.remarks}</textarea>
            </div>
        </div>
    </div>
    <div class="user_right">
        <input type="hidden"  name="icon"  value="${userinfo.icon}" >
        <button type="button" class="layui-btn layui-btn-normal" id="test1"><i class="layui-icon"></i>来来来，换个脸怎么样</button>
        <img <#if (userinfo.icon??)>src="${userinfo.icon}" <#else> src="${base}/static/images/face.jpg " </#if> class="layui-circle" id="userFace">
    </div>
    <div class="layui-form-item" style="margin-left: 5%;">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="changeUser">立即提交</button>
            <button type="button" class="layui-btn layui-btn-primary restuserinfo">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    layui.use(['form','jquery','layer','upload','laydate'],function() {
        var form = layui.form,
                $ = layui.jquery,
                upload = layui.upload,
                layer = layui.layer;

        //普通图片上传
        upload.render({
            elem: '#test1',
            url: '${base}/file/upload/',
            field: 'test',
            before: function (obj) {
                //预读本地文件示例，不支持ie8
                obj.preview(function (index, file, result) {
                    $('#userFace').attr('src', result); //图片链接（base64）
                });
                layer.load(2, {
                    shade: [0.3, '#333']
                });
            },
            done: function (res) {
                layer.closeAll('loading');
                //如果上传失败
                if (res.success === false) {
                    return layer.msg('上传失败');
                }else{
                    layer.msg("上传成功",{time:1000},function () {
                        $("input[name='icon']").val(res.data.url);
                    })
                }
            }
        });

        form.on("submit(changeUser)",function (data) {
            if(data.field.id == null){
                layer.msg("用户ID不存在");
                return false;
            }
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.post("${base}/admin/system/user/saveUserinfo",data.field,function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("您的个人信息保存成功！",{time:1500},function(){
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message);
                }
            });
            return false;
        });

        $(".restuserinfo").on("click",function () {
            layer.confirm('确定重置个人信息么?', {icon: 3, title:'提示'}, function(index){
                window.location.reload();
                layer.close(index);
            });
        });

    });
</script>
</body>
</html>