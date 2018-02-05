<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>404--页面找不到了</title>
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
    <link rel="stylesheet" href="/static/layui/css/layui.css" media="all" />
</head>
<body class="childrenBody">
<div style="text-align: center; padding:11% 0;">
    <i class="layui-icon" style="line-height:20rem; font-size:20rem; color: #393D50;">&#xe61c;</i>
    <p style="font-size: 20px; font-weight: 300; color: #999;">页面没有找到~~</p>
    <p style="font-size: 20px; font-weight: 300; color: #999;">您的请求地址为:${url}</p>
    <#--<#if e??>-->
        <#--<pre class="layui-code">-->
            <#--${e.message}-->
        <#--</pre>-->
    <#--</#if>-->
</div>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    layui.use('code', function(){ //加载code模块
        layui.code({
            title:'Java Exception Message',
            encode:true
        }); //引用code方法
    });
</script>
</body>
</html>