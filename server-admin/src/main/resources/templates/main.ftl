<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>首页--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <link rel="stylesheet" href="//at.alicdn.com/t/font_tnyc012u2rlwstt9.css" media="all" />
    <link rel="stylesheet" href="${base}/static/css/main.css" media="all" />
</head>
<body class="childrenBody">
<div class="panel_box row">
    <#if (userMenu?size>0)>
        <#list userMenu as items>
            <div class="panel col <#if (!items_has_next)>max_panel</#if> ">
                <a href="javascript:" data-url="${base}${items.href}">
                    <div class="panel_icon" <#if (items.bgColor != "")>style="background-color: ${items.bgColor}"<#else>style="background-color: #54ade8"</#if>>
                        <i class="layui-icon" data-icon="${items.icon}">${items.icon}</i>
                    </div>
                    <div class="panel_word newMessage">
                        <span>${items.dataCount}</span>
                        <cite>${items.name}</cite>
                    </div>
                </a>
            </div>
        </#list>
    </#if>
</div>
<div class="row">
    <div id="container" style="height: 500px"></div>
    <script type="text/javascript" src="${base}/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="${base}/static/js/echarts.min.js"></script>
    <script type="text/javascript">
        var dom = document.getElementById("container");
        var myChart = echarts.init(dom);
        var dateArray = [];
        function getDay(day){
            var today = new Date();

            var targetday_milliseconds=today.getTime() + 1000*60*60*24*day;

            today.setTime(targetday_milliseconds); //注意，这行是关键代码

            var tYear = today.getFullYear();
            var tMonth = today.getMonth();
            var tDate = today.getDate();
            tMonth = doHandleMonth(tMonth + 1);
            tDate = doHandleMonth(tDate);
            return tYear+"-"+tMonth+"-"+tDate;
        }
        function doHandleMonth(month){
            var m = month;
            if(month.toString().length === 1){
                m = "0" + month;
            }
            return m;
        }
        for(i=-14; i<=0;i++){
            console.log(getDay(i));
            dateArray.push(getDay(i));
        }
        $.get('${base}/admin/system/log/pvs').done(function (res) {
            myChart.setOption({
                tooltip : {
                    show: true,
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                xAxis: {
                    type: 'category',
                    data: dateArray
                },
                yAxis: {
                    type: 'value'
                },
                series: [{
                    data: res.data,
                    type: 'bar',
                    name: '日流量',
                    markPoint : {
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    }
                }]
            });
        });
    </script>
</div>
<div class="row">
    <div class="sysNotice col">
        <blockquote class="layui-elem-quote title">更新日志</blockquote>
        <div class="layui-elem-quote layui-quote-nm">
            <@ar channelid="7">
                <#if (result?size>0)>
                    <#list result as item>
                        ${item.content}
                    </#list>
                </#if>
            </@ar>
        </div>
    </div>
    <div class="sysNotice col">
        <blockquote class="layui-elem-quote title">系统基本参数</blockquote>
        <table class="layui-table">
            <colgroup>
                <col width="150">
                <col>
            </colgroup>
            <tbody>
            <tr>
                <td>网站名称</td>
                <td class="version">${site.name}</td>
            </tr>
            <tr>
                <td>开发作者</td>
                <td class="author">${site.author}</td>
            </tr>
            <tr>
                <td>当前版本</td>
                <td class="homePage">${site.version}</td>
            </tr>
            <tr>
                <td>服务器环境</td>
                <td class="server">${site.server}</td>
            </tr>
            <tr>
                <td>数据库版本</td>
                <td class="dataBase">${site.database}</td>
            </tr>
            <tr>
                <td>最大上传限制</td>
                <td class="maxUpload">${site.maxUpload}</td>
            </tr>
            <tr>
                <td>当前用户角色</td>
                <td class="userRights">
                    <#if (currentUser.roleLists?? && currentUser.roleLists?size>0)>
                        <#list currentUser.roleLists as items>
                            <span class="layui-badge layui-bg-green">${items.name}</span>
                        </#list>
                    </#if>
                </td>
            </tr>
            </tbody>
        </table>
        <blockquote class="layui-elem-quote title">最新文章<i class="iconfont icon-new1"></i></blockquote>
        <table class="layui-table" lay-skin="line">
            <colgroup>
                <col>
                <col width="110">
            </colgroup>
            <tbody class="hot_news">
                <@myindex limit = "5">
                    <#if (result?size>0)>
                        <#list result as items>
                            <tr><td align="left">${items.title}</td><td>${items.publistTime?string("yyyy-MM-dd")}</td></tr>
                        </#list>
                    </#if>
                </@myindex>
                <#--<@ar channelId = "5">-->
                    <#--<#assign articleList = result["articleList"]>-->
                    <#--<#if (articleList?size>0)>-->
                        <#--<#list articleList as items>-->
                                <#--<tr><td align="left">${items.title}</td><td>${items.publistTime?string("yyyy-MM-dd")}</td></tr>-->
                        <#--</#list>-->
                    <#--</#if>-->
                <#--</@ar>-->
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    layui.use(['layer','jquery','form'],function(){
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form;

        $(".panel a").on("click",function(){
            window.parent.addTab($(this));
        });
    });
</script>
</body>
</html>