<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>角色修改--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <style type="text/css">
        .layui-form-item .layui-inline{ min-width:15%; float:left; margin-right:0 }
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
            <li style="margin-top: 5px;margin-left: 1.5em"><input type="checkbox" title="${child.name}" value="${child.id}" data-parentIds = "${child.parentIds}"  lay-filter="roleMenu" <#if menuIds?contains(child.id?string)>checked</#if> />
                <ul>
                    <@bpTree children=child.children />
                </ul>
            </li>
            <#else>
            <li style="margin-top: 5px;margin-left: 1.5em"><input type="checkbox" title="${child.name}" value="${child.id}" data-parentIds = "${child.parentIds}"  lay-filter="roleMenu"  <#if menuIds?contains(child.id?string)>checked</#if> /></li>
            </#if>
        </#list>
    </#if>
</#macro>
<form class="layui-form">
    <input name="id" type="hidden" value="${role.id}">
    <div class="layui-form-item">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;margin-left: 10px;">
            <legend>角色名称(<span style="color:red">*</span>)</legend>
        </fieldset>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block" style="width: 80%">
            <input type="text" class="layui-input" name="name" style="margin-top: 30px;" value="${role.name}" lay-verify="required" placeholder="请想一个角色名称">
        </div>
    </div>
    <div class="layui-form-item">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;margin-left: 10px;">
            <legend>备注</legend>
        </fieldset>
    </div>
    <div class="layui-form-item layui-form-text">
        <div class="layui-input-block" style="width: 80%">
            <textarea placeholder="一些可有可无的备注罢了.." name="remarks" class="layui-textarea">${role.remarks}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;margin-left: 10px;">
            <legend>请分配角色权限</legend>
        </fieldset>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
        <#if menuList?? && menuList?size gt 0>
            <#list menuList as menu>
                <div class="layui-inline">
                    <fieldset class="layui-elem-field">
                        <legend><input type="checkbox"  value="${menu.id}" data-parentIds = "${menu.parentIds}" title="${menu.name}" lay-filter="roleMenu" <#if menuIds?contains(menu.id?string)>checked</#if> /></legend>
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
            <button class="layui-btn" lay-submit="" lay-filter="editRole">我要修改</button>
            <button class="layui-btn layui-btn-primary">我不改了</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    layui.use(['form','jquery','layer'],function(){
        var form = layui.form,
                $    = layui.jquery,
                layer = layui.layer;

        form.on('checkbox(roleMenu)',function(data){
            var v = data.elem.getAttribute("data-parentIds");
            var child = $(data.elem).parents('form').find('input[type="checkbox"]');
            if(data.elem.checked){//勾选的时候的动作,父栏目层级全部勾选
                child.each(function(index, item){
                    if(v.indexOf(item.value)>=0){
                        item.checked = data.elem.checked;
                    }
                });
            }else{ //取消选择的时候，子栏目层级全部取消选择
                child.each(function(index, item){
                    //获取每一个checkbox的 父栏目ID组
                    var r = item.getAttribute("data-parentIds");
                    if(r.indexOf(data.value)>=0){
                        item.checked = data.elem.checked;
                    }
                });
            }
            form.render('checkbox');
        });
        form.on("submit(editRole)",function(data){
            if(data.field.id == null){
                layer.msg("用户ID不存在");
                return false;
            }
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
                url:"${base}/admin/system/role/edit",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(data.field),
                success:function(res){
                    layer.close(loadIndex);
                    if(res.success){
                        parent.layer.msg("角色编辑成功！",{time:1000},function(){
                            //刷新父页面
                            parent.location.reload();
                        });
                    }else{
                        layer.msg(res.message,{time:1000},function(){
                            //刷新本页面
                            location.reload();
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