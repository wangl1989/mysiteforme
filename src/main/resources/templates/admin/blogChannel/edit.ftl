<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>博客栏目编辑--${site.name}</title>
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
        .pre-href{
            text-align: right;
            text-align: end;
            font-weight: 900;
            font-size: 25px;
        }
        .href{
            font-weight: 900;
            font-size: 25px;
        }
    </style>
</head>
<body class="childrenBody">
<form class="layui-form" style="width:80%;">
    <input value="${blogChannel.id}" name="id" type="hidden">
    <div class="layui-form-item">
        <label class="layui-form-label">名称</label>
        <div class="layui-input-block">
                <input  type="text" class="layui-input" value = "${blogChannel.name}" name="name" lay-verify="required" placeholder="请输入名称">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">栏目图标</label>
        <div class="layui-input-block">
            <input type="hidden" class="layui-input" name="logo" value = "${blogChannel.logo}" id="iconValue"  placeholder="请选择菜单图标">
            <div class="layui-input-inline" style="width: 100px;">
                <#if (blogChannel.logo)??>
                    <i class="layui-icon" id="realIcon" style="font-size: 50px">${blogChannel.logo}</i>
                <#else>
                    <i class="layui-icon" id="realIcon" style="display: none;font-size: 50px"></i>
                </#if>

            </div>
            <div class="layui-input-inline" style="width: 100px;">
                <a class="layui-btn layui-btn-normal" id="selectIcon">来选择一个图标</a>
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属站点</label>
        <div class="layui-input-block">
            <select name="siteId">
            <#list siteList as item>
                <option value="${item.id}">${item.name}</option>
            </#list>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">栏目地址</label>
        <div class="layui-input-block">
            <#assign hrefIndex = blogChannel.href?last_index_of("/")>
            <#assign hreflength = blogChannel.href?length>
            <div class="layui-input-inline" style="width: 50%;margin-right: 0">
                <input type="text" name="preHref" value="${blogChannel.href?substring(0,hrefIndex+1)}" disabled   class="layui-input layui-disabled pre-href">
            </div>
            <div class="layui-input-inline" style="width: 50%;margin-right: 0">
                <input type="text" name="href"  class="layui-input href" value="${blogChannel.href?substring(hrefIndex+1,hreflength)}" lay-verify="required|blogHref" lay-verType="tips">
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否在首页展示</label>
        <div class="layui-input-block">
                <input type="checkbox" name="baseChannel"  lay-skin="switch" lay-text="是|否" value="1" <#if (blogChannel.baseChannel == true)> checked </#if> >

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否能够评论</label>
        <div class="layui-input-block">
                <input type="checkbox" name="canComment"  lay-skin="switch" lay-text="是|否" value="1" <#if (blogChannel.canComment == true)> checked </#if> >

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否匿名</label>
        <div class="layui-input-block">
                <input type="checkbox" name="noName"  lay-skin="switch" lay-text="是|否" value="1" <#if (blogChannel.noName == true)> checked </#if> >

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否开启审核</label>
        <div class="layui-input-block">
                <input type="checkbox" name="canAduit"  lay-skin="switch" lay-text="是|否" value="1" <#if (blogChannel.canAduit == true)> checked </#if> >

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">排序值</label>
        <div class="layui-input-block">
            <input  type="text" class="layui-input" value = "${blogChannel.sort}" name="sort"  placeholder="请输入排序值">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">网页title(seo)</label>
        <div class="layui-input-block">
                <input  type="text" class="layui-input" value = "${blogChannel.seoTitle}" name="seoTitle"  placeholder="请输入网页title(seo)">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">网页关键字(seo) </label>
        <div class="layui-input-block">
                <input  type="text" class="layui-input" value = "${blogChannel.seoKeywords}" name="seoKeywords"  placeholder="请输入网页关键字(seo) ">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">网页描述(seo)</label>
        <div class="layui-input-block">
                <textarea name="seoDescription"   placeholder="请输入网页描述(seo)" class="layui-textarea">${blogChannel.seoDescription}</textarea>

        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addBlogChannel">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/pinyin.js"></script>
<script>
    var iconShow,$;
    layui.use(['form','jquery','layer'],function(){
        var form      = layui.form,
                layer = layui.layer;
        $     = layui.jquery;
        //选择图标
        $("#selectIcon").on("click",function () {
            iconShow =layer.open({
                type: 2,
                title: '选择图标',
                shadeClose: true,
                content: '${base}/static/page/icon.html'
            });
            layer.full(iconShow);
        });
        //根据中文生成英文字母
        $("input[name='name']").on('blur',function () {
            var channelName = $(this).val(),
                    reg = new RegExp("^[A-Za-z0-9\u4e00-\u9fa5]+$");
            if(channelName != null && channelName !==""){
                if(!reg.test(channelName)){
                    layer.tips('只能输入中文英文跟数字', $(this), {
                        tips: [1, '#0FA6D8'] //还可配置颜色
                    });
                    $(this).val("");
                }else{
                    $("input[name='href']").val(pinyin.getCamelChars(channelName).toLowerCase());
                }
            }else{
                $("input[name='href']").val("");
            }
        });

        form.verify({
            blogHref: function(value, item){ //value：表单的值、item：表单的DOM对象
                if(!new RegExp("^[a-z0-9]+$").test(value)){
                    return '栏目地址只能是数字或者小写字母';
                }
                var href= $("input[name='preHref']").val()+value,
                        oldHref = '${blogChannel.href}',
                        flag = true;
                if(href.trim() !== oldHref){
                    $.ajax({
                        url:'${base}/admin/blogChannel/checkHref',
                        type:'POST',
                        data:{parentId:<#if parentChannel??>${parentChannel.id}<#else> null</#if>,href:href},
                        async: false,
                        success:function (res) {
                            if(!res.success){
                                flag = false;
                            }
                        }
                    });
                    if(!flag){
                        return "栏目地址已存在";
                    }
                }
            }

        });

        form.on("submit(addBlogChannel)",function(data){
            if(undefined === data.field.baseChannel || '0' === data.field.baseChannel || null === data.field.baseChannel){
                data.field.baseChannel = false;
            }else{
                data.field.baseChannel = true;
            }
            if(undefined === data.field.canComment || '0' === data.field.canComment || null === data.field.canComment){
                data.field.canComment = false;
            }else{
                data.field.canComment = true;
            }
            if(undefined === data.field.noName || '0' === data.field.noName || null === data.field.noName){
                data.field.noName = false;
            }else{
                data.field.noName = true;
            }
            if(undefined === data.field.canAduit || '0' === data.field.canAduit || null === data.field.canAduit){
                data.field.canAduit = false;
            }else{
                data.field.canAduit = true;
            }
            if(data.field.preHref != null && data.field.preHref !== undefined && data.field.preHref !==""){
                data.field.href = data.field.preHref + data.field.href;
            }
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            //给角色赋值
            $.post("${base}/admin/blogChannel/edit",data.field,function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("博客栏目编辑成功！",{time:1000},function(){
                        parent.layer.close(parent.editIndex);
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