<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>用户修改--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <link rel="stylesheet" href="${base}/static/colorpicker/colpick.css" media="all" />
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
        .color-box {
            float:left;
            width:30px;
            height:30px;
            margin:5px;
            border: 1px solid white;
        }
    </style>
</head>
<body class="childrenBody">
<form class="layui-form" style="width:80%;">
    <input class="layui-hide" name="id" value="${menu.id}"/>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单名称</label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="name" lay-verify="required" value="${menu.name}" placeholder="请输入菜单名称">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单背景色</label>
        <div class="layui-input-block">
            <input type="hidden" class="layui-input" name="bgColor" value="${menu.bgColor}">
            <div class="color-box"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单地址</label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="href" value="${menu.href}"  placeholder="请输入菜单地址">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单权限</label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="permission" value="${menu.permission}"  placeholder="菜单权限">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单图标</label>
        <div class="layui-input-block">
            <input type="hidden" class="layui-input" name="icon" id="iconValue" value="${menu.icon}"  placeholder="请选择菜单图标">
            <div class="layui-input-inline" style="width: 100px;">
                <i class="layui-icon" id="realIcon" style="<#if (menu.icon == null || menu.icon == '')>display: none;</#if>font-size: 50px"><#if (menu.icon)??>${menu.icon}</#if></i>
            </div>
            <div class="layui-input-inline" style="width: 100px;">
                <a class="layui-btn layui-btn-normal" id="selectIcon">我来选择一个图标</a>
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">左侧菜单栏是否显示</label>
        <div class="layui-input-block">
            <input type="checkbox" name="isShow" lay-skin="switch"    lay-text="是|否" <#if (menu.isShow == true)>checked</#if>>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">排序值</label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" name="sort" value="${menu.sort}" lay-verify="required|number"  placeholder="菜单权限">
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addUser">我要修改</button>
            <button class="layui-btn"   class="layui-btn layui-btn-primary">我不改了</button>
        </div>
    </div>
</form>
<script src="https://code.jquery.com/jquery-2.2.4.min.js"
        integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44="
        crossorigin="anonymous"></script>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/colorpicker/colpick.js"></script>
<script>
    var iconShow,$;
    layui.use(['form','jquery','layer'],function(){
        var form = layui.form,
                layer = layui.layer;
        $    = layui.jquery;
        <#assign color = "">
        <#if (menu.bgColor != null && menu.bgColor !="")>
            <#assign color = menu.bgColor?substring(1)>
        <#else>
            <#assign color = "ff8800">
        </#if>
        $('.color-box').colpick({
            colorScheme:'dark',
            layout:'rgbhex',
            color:'${color}',
            onSubmit:function(hsb,hex,rgb,el) {
                $(el).css('background-color', '#'+hex);
                $(el).colpickHide();
                $("input[name='bgColor']").val("#"+hex);
            }
        }).css('background-color', '#${color}');

        $("#selectIcon").on("click",function () {
            iconShow =layer.open({
                type: 2,
                title: '选择图标',
                shadeClose: true,
                content: '${base}/static/page/icon.html'
            });
            layer.full(iconShow);
        });

        form.on("submit(addUser)",function(data){
            if(data.field.id == null){
                layer.msg("菜单ID不存在",{time:1000});
                return false;
            }
            if(data.field.sort<0){
                layer.msg("排序值不能为负数",{time:1000});
                return false;
            }
            if(undefined !== data.field.isShow && null != data.field.isShow){
                data.field.isShow = true;
            }else{
                data.field.isShow = false;
            }
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.post("${base}/admin/system/menu/edit",data.field,function (res) {
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("菜单编辑成功!",{time:1500},function(){
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