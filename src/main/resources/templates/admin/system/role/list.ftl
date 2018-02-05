<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>用户总数--layui后台管理模板</title>
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
    <legend>角色检索</legend>
    <div class="layui-field-box">
    <form class="layui-form">
        <div class="layui-inline">
            <input type="text" value="" name="s_key" placeholder="可以输入角色名" class="layui-input search_input">
        </div>
        <div class="layui-input-inline">
            <a class="layui-btn" lay-submit="" lay-filter="searchForm">查询</a>
        </div>
        <div class="layui-inline">
            <a class="layui-btn layui-btn-normal" data-type="addUser">添加角色</a>
        </div>
        <div class="layui-inline">
            <a class="layui-btn layui-btn-danger" data-type="deleteSome">批量删除</a>
        </div>
    </form>
    </div>
</fieldset>
<div class="layui-form users_list">
    <table class="layui-table" id="test" lay-filter="demo"></table>

    <script type="text/html" id="barDemo">
        <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    </script>
</div>
<div id="page"></div>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js"></script>
<script>
    layui.use(['layer','form','table'], function() {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                table = layui.table,
                t;              //表格变量
        //监听工具条
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'edit'){
                var editIndex = layer.open({
                    title : "编辑角色",
                    type : 2,
                    content : "${base}/admin/system/role/edit?id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回角色列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
                //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                $(window).resize(function(){
                    layer.full(editIndex);
                });
                layer.full(editIndex);
            }
            if(obj.event === "del"){
                layer.confirm("你确定要删除该角色么？",{btn:['是的,我确定','我再想想']},
                        function(){
                            $.post("${base}/admin/system/role/delete",{"id":data.id},function (res){
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

        t = {
            elem: '#test',
            even: true,
            url:'${base}/admin/system/role/list',
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
                {field:'id',          title: 'ID',      width:'3%'},
                {field:'name',        title: '角色名称'   },
                {field:'createUser',  title: '创建人',templet:'<div>{{  d.createUser.nickName }}</div>'},
                {field:'updateUser',  title: '修改人',templet:'<div>{{  d.updateUser.nickName }}</div>'},
                {field:'createDate',  title: '创建时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.createDate) }}</div>',unresize: true}, //单元格内容水平居中
                {field:'updateDate',  title: '修改时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.updateDate) }}</div>',unresize: true}, //单元格内容水平居中
                {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#barDemo'}
            ]]
        };
        table.render(t);

        var active={
            addUser : function(){
                    addIndex = layer.open({
                    title : "添加角色",
                    type : 2,
                    content : "${base}/admin/system/role/add",
                    success : function(layero, addIndex){
                        setTimeout(function(){
                            layer.tips('点击此处返回角色列表', '.layui-layer-setwin .layui-layer-close', {
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
            //批量删除
            deleteSome : function(){
                var checkStatus = table.checkStatus('test'),
                        data = checkStatus.data;
                if(data.length > 0){
                    console.log(JSON.stringify(data));
                    layer.confirm("你确定要删除这些角色么？",{btn:['是的,我确定','我再想想']},
                            function(){
                                var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                                $.ajax({
                                    type:"POST",
                                    url:"${base}/admin/system/role/deleteSome",
                                    dataType:"json",
                                    contentType:"application/json",
                                    data:JSON.stringify(data),
                                    success:function(res){
                                        layer.close(deleteindex);
                                        if(res.success){
                                            layer.msg("删除成功",{time: 1000},function(){
                                                table.reload('test', t);
                                            });
                                        }else{
                                            layer.msg(res.message);
                                        }
                                    }
                                });
                            }
                    )
                }else{
                    layer.msg("请选择需要删除的角色",{time:1000});
                }
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