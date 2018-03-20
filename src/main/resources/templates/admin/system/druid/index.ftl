<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>SQL监控</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
</head>
<body layout:fragment="content">
<iframe id="druidIframe" onload="reinitIframe()"  width="100%" height="100%" src="${base}/druid/index.html" frameborder="0"></iframe>
<script th:inline="javascript">
    function reinitIframe() {
        var iframe = document.getElementById("druidIframe");
        try {
            var bHeight = iframe.contentWindow.document.body.scrollHeight;
            var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
            var height = Math.max(bHeight, dHeight);
            iframe.height = height;
            console.log(height);
        } catch(ex) {}
    }
    window.setInterval("reinitIframe()", 200);
</script>

</body>
</html>