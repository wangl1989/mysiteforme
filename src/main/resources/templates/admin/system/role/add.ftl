<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>layui</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->
    <style type="text/css">
        .layui-form-item .layui-inline{ min-width:15%; float:left; margin-right:0; }
        .layui-form-item .role-box {
            position: relative;
        }

    </style>
</head>
<body class="childrenBody">

<!-- 递归  宏定义 -->
<#macro bpTree children>
    <#if children?? && children?size gt 0>
        <#list children as child>
            <#if child.children?? && child.children?size gt 0>
            <li style="margin-top: 5px;margin-left: 1.5em"><input type="checkbox" title="${child.name}" value="${child.id}" data-parentIds = "${child.parentIds}"  lay-filter="roleMenu" />
                <ul>
                    <@bpTree children=child.children />
                </ul>
            </li>
            <#else>
            <li style="margin-top: 5px;margin-left: 1.5em"><input type="checkbox" title="${child.name}" value="${child.id}" data-parentIds = "${child.parentIds}"  lay-filter="roleMenu" /></li>
            </#if>
        </#list>
    </#if>
</#macro>
<form class="layui-form">
    <div class="layui-form-item">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;margin-left: 10px;">
            <legend>角色名称(<span style="color:red">*</span>)</legend>
        </fieldset>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block" style="width: 80%">
            <input type="text" class="layui-input" name="name" style="margin-top: 30px;" lay-verify="required" placeholder="请想一个角色名称">
        </div>
    </div>
    <div class="layui-form-item">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;margin-left: 10px;">
            <legend>备注</legend>
        </fieldset>
    </div>
    <div class="layui-form-item layui-form-text">
        <div class="layui-input-block" style="width: 80%">
            <textarea placeholder="一些可有可无的备注罢了.." name="remarks" class="layui-textarea"></textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;margin-left: 10px;">
            <legend>请分配角色权限</legend>
        </fieldset>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block" style="width: 80%">
            <#if menuList?? && menuList?size gt 0>
                <#list menuList as menu>
            <div class="layui-inline">
                <fieldset class="layui-elem-field">
                    <legend><input type="checkbox"  value="${menu.id}" data-parentIds = "${menu.parentIds}"   title="${menu.name}" lay-filter="roleMenu" /></legend>
                    <div class="layui-field-box">
                        <ul style="padding: 0 15px">
                            <@bpTree children=menu.children />
                        </ul>
                    </div>
                </fieldset>
                </div>
                </#list>
            </#if>
        </div>
    </div>
    <div class="layui-form-item" style="padding-left: 30%;">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addRole">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>
    Array.prototype.contains = function ( needle ) {
        for (i in this) {
            if (this[i] == needle) return true;
        }
        return false;
    };
    layui.use(['form','layer','jquery'], function(){
        var form = layui.form,
            layer = layui.layer,
                $ = layui.jquery;
        form.on('checkbox(roleMenu)',function(data){
            var v = data.elem.getAttribute("data-parentIds"),
            myarr=v.split(",");
            var child = $(data.elem).parents('form').find('input[type="checkbox"]');
            if(data.elem.checked){//勾选的时候的动作,父栏目层级全部勾选
                child.each(function(index, item){
                    if(myarr.contains(item.value)){
                        item.checked = data.elem.checked;
                    }
                });
            }else{ //取消选择的时候，子栏目层级全部取消选择
                child.each(function(index, item){
                    //获取每一个checkbox的 父栏目ID组
                    var r = item.getAttribute("data-parentIds"),
                    noarr = r.split(",");
                    if(noarr.contains(data.value)){
                        item.checked = data.elem.checked;
                    }
                });
            }
            form.render('checkbox');
        });
        form.on('submit(addRole)',function(data){
            var menus = [];
            var c = $('form').find('input[type="checkbox"]');
            c.each(function(index, item){
                var m = {};
                if(item.checked){
                    m.id = parseInt(item.value);
                    menus.push(m);
                }
            });
            data.field.menuSet = menus;
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.ajax({
                type:"POST",
                url:"${base}/admin/system/role/add",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(data.field),
                success:function(res){
                    layer.close(loadIndex);
                    if(res.success){
                        parent.layer.msg("用户添加成功！",{time:1000},function(){
                            //刷新父页面
                            parent.location.reload();
                        });
                    }else{
                        layer.msg(res.message);
                    }
                }
            });
            return false;
        });
    });
</script>
</body>
</html>