<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>数据库表--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <link rel="stylesheet" href="${base}/static/css/user.css" media="all" />
</head>
<body class="childrenBody">
<fieldset class="layui-elem-field">
    <legend>数据库检索1</legend>
    <div class="layui-field-box">
    <form class="layui-form">
        <div class="layui-inline">
            <input type="text" value="" name="s_name" placeholder="可以输入表名" class="layui-input search_input">
        </div>
        <div class="layui-inline">
            <a class="layui-btn"  lay-submit="" lay-filter="searchForm">查询</a>
        </div>
        <div class="layui-inline">
            <a class="layui-btn layui-btn-normal" data-type="addUser">新增表</a>
        </div>
        <div class="layui-inline">
            <a class="layui-btn layui-btn-warm" data-type="getCheckData">下载源码</a>
        </div>
    </form>
    </div>
</fieldset>
<div class="layui-form users_list">
    <table class="layui-table" id="test" lay-filter="demo"></table>

    <script type="text/html" id="comment">
        <!-- 这里的 checked 的状态只是演示 -->
        {{#  if(d.comment != null && d.comment != ''){ }}
        {{#  layui.each(d.comment.split(','), function(index, item){ }}
        {{# if(item == '1'){ }}
        <span class="layui-badge layui-bg-blue">基本数据表</span>
        {{# }else if(item == '2'){ }}
        <span class="layui-badge layui-bg-blue">树结构数据表</span>
        {{# }else if(item == '3'){ }}
        <span class="layui-badge layui-bg-blue">辅助数据表</span>
        {{# }else if(item.indexOf('uploadImg')>=0){ }}
        <span class="layui-badge layui-bg-green">上传图片控件</span>
        {{# }else if(item.indexOf('uploadFile')>=0){ }}
        <span class="layui-badge layui-bg-green">上传文件控件</span>
        {{# }else if(item.indexOf('timer')>=0){ }}
        <span class="layui-badge layui-bg-green">时间控件</span>
        {{# }else if(item.indexOf('editor')>=0){ }}
        <span class="layui-badge layui-bg-green">编辑器控件</span>
        {{# }else if(item.indexOf('switch')>=0){ }}
        <span class="layui-badge layui-bg-green">开关控件</span>
        {{# } }}
        {{#  }); }}
        {{#  } }}
    </script>

    <script type="text/html" id="barDemo">
        {{# if(d.name.indexOf('sys_') < 0 && d.name.indexOf('qrtz_')<0){ }}
        <a class="layui-btn layui-btn-xs" href="javascript:" data-url="${base}/table/edit?name={{d.name}}" lay-event="edit"><cite>编辑</cite></a>
<#--  删除表影响重大，此功能暂时注解      <a class="layui-btn layui-btn-danger layui-btn-xs" href="javascript:" lay-event="del">删除</a>-->
        {{# } }}
    </script>
</div>
<div id="page"></div>
<script>
    var baseUrl = "${base}";
</script>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js?t=1"></script>
<script>
    layui.use(['layer','form','table','element'], function() {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                table = layui.table,
                t;                  //表格数据变量
        t = {
            elem: '#test',
            url:'${base}/admin/system/table/list',
            method:'post',
            page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
                //,curr: 5 //设定初始在第 5 页
                groups: 2, //只显示 1 个连续页码
                first: "首页", //显示首页
                last: "尾页", //显示尾页
                limits:[3,10, 20, 30]
            },
            width: $(parent.window).width()-223,
            cols: [[
                {type:'checkbox'},
                {field:'name', title: '表名'},
                {field:'tableRows',  title: '数据量',    width:'5%'},
                {field:'comment',     title: '备注', templet:'#comment' },
                {field:'createTime',  title: '创建时间',width:'18%',templet:'<div>{{ layui.laytpl.toDateString(d.createTime) }}</div>',unresize: true}, //单元格内容水平居中
                {field:'updateTime',  title: '更新时间',width:'18%',templet:'<div>{{ layui.laytpl.toDateString(d.updateTime) }}</div>',unresize: true}, //单元格内容水平居中
                {fixed: 'right',    title: '操作',width: '15%', align: 'center',toolbar: '#barDemo'}
            ]]
        };
        table.render(t);

        //监听工具条
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'edit'){
                // window.parent.myaddTab($(this));
                var resizeFun;
                var editIndex = layer.open({
                    title : "编辑数据表",
                    type : 2,
                    area: ['1000px', '1000px'],
                    content : "${base}/admin/system/table/edit?name="+data.name,
                    success : function(layero, index){
                        $(window).bind("resize", resizeFun = function () {
                            layer.full(index);
                            layer.iframeAuto(index);
                        });
                        setTimeout(function(){
                            layer.tips('点击此处返回数据表列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    },
                    cancel: function (index) {
                        $(window).unbind("resize", resizeFun);
                    }
                });
                //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                $(window).resize(function(){
                    layer.full(editIndex);
                });
                layer.full(editIndex);
                layer.iframeAuto(editIndex);
            }
            if(obj.event === "del"){
                layer.confirm("你确定要删除该数据表么,删除之后不可恢复？",{btn:['是的,我确定','我再想想']},
                        function(){
                            $.post("${base}/admin/system/table/delete",{"tableName":data.name},function (res){
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('test', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }

                            });
                        }
                )
            }
        });

        //功能按钮
        var active={
            addUser : function(){
                var addIndex = layer.open({
                    title : "新增数据表",
                    type : 2,
                    content : "${base}/admin/system/table/add",
                    success : function(layero, addIndex){
                        setTimeout(function(){
                            layer.tips('点击此处返回会员列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
                //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                $(window).resize(function(){
                    layer.full(addIndex);
                });
                layer.full(addIndex);
            },
            getCheckData: function(){ //获取选中数据
                var checkStatus = table.checkStatus('test'),
                data = checkStatus.data,
                baseTables = [],
                treeTables = [];
                if(data.length === 0){
                    layer.msg("请选择所需要生成源码的数据表");
                    return;
                }
                for(var i=0;i<data.length;i++){
                    if(data[i].name.indexOf("sys_")>=0){
                        return layer.msg("系统表不能生成源码");
                    }
                    if(data[i].name.indexOf("qrtz_")>=0){
                        return layer.msg("定时任务表不支持生成源码");
                    }
                    var comment = data[i].comment.split(','),
                    type = comment[1];
                    if(type === '1'){
                        baseTables.push(data[i].name);
                    }
                    if(type === '2'){
                        treeTables.push(data[i].name);
                    }
                    if(type === '3'){
                        return layer.msg("辅助表不能生成源码");
                    }
                }
                var formData = new FormData();
                formData.append('baseTables', baseTables);
                formData.append('treeTables', treeTables);
                <#--var url = '${base}/admin/system/table/download',-->
                <#--xhr = new XMLHttpRequest();-->
                <#--xhr.open('POST', url, true);        // 也可以使用POST方式，根据接口-->
                <#--xhr.responseType = "blob";-->
                <#--xhr.onload = function () {-->
                    <#--// 请求完成-->
                    <#--if (this.status === 200) {-->
                        <#--// 返回200-->
                        <#--var blob = this.response;-->
                        <#--var reader = new FileReader();-->
                        <#--reader.readAsDataURL(blob);    // 转换为base64，可以直接放入a表情href-->
                        <#--reader.onload = function (e) {-->
                            <#--// 转换完成，创建一个a标签用于下载-->
                            <#--var a = document.createElement('a');-->
                            <#--a.download = '源码.rar';-->
                            <#--a.href = e.target.result;-->
                            <#--$("body").append(a);    // 修复firefox中无法触发click-->
                            <#--a.click();-->
                            <#--$(a).remove();-->
                        <#--}-->
                    <#--}-->
                <#--};-->
                <#--// 发送ajax请求-->
                <#--xhr.send(formData);-->
                var config = $.extend(true, { method: 'post' }, {
                    url:"${base}/admin/system/table/download",
                    data:{baseTables:baseTables,treeTables:treeTables}
                }),
                $iframe = $('<iframe id="down-file-iframe" />'),
                $form = $('<form target="down-file-iframe" method="' + config.method + '" />');

                $form.attr('action', config.url);
                for (var key in config.data) {
                    $form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
                }
                $iframe.append($form);
                $(document.body).append($iframe);
                $form[0].submit();
                $iframe.remove();
            }
        };

        $('.layui-inline .layui-btn').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        //搜索
        form.on("submit(searchForm)",function(data){
            t.where = data.field;
            table.reload('test', t);
            return false;
        });

    });
</script>
</body>
</html>