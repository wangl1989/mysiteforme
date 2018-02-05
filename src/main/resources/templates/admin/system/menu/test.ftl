<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title></title>
    <meta name="description" content="">
    <meta name="keywords" content="">
    <link href="" rel="stylesheet">
    <link rel="stylesheet" href="https://static.mysiteforme.com/layui-treetable/layui/css/layui.css">
    <style>
        .layui-table .value_col{
            text-align: center;
        }
    </style>
</head>

<body style="margin:10px 10px 0;">
<fieldset class="layui-elem-field">
    <legend>系统菜单</legend>
    <div class="layui-field-box">
    <div class="layui-inline">
        <a class="layui-btn layui-btn-normal" data-type="addUser">添加根菜单</a>
    </div>
    </div>
</fieldset>
<div class="layui-form users_list">
    <div id="demo"></div>
</div>
</body>
<script src="https://static.mysiteforme.com/layui-treetable/layui/layui.js"></script>
<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>

<script type="text/javascript">
    layui.use(['tree', 'layer','table'], function() {
        var layer = layui.layer,
             $ = layui.jquery;

        var layout = [
            { name: '菜单名称', treeNodes: true, headerClass: 'value_col'},
            {
                name: '链接地址',
                headerClass: 'value_col',
                colClass: 'value_col',
                style: 'width: 15%',
                render:function(row){
                    return undefined === row.href?"" : row.href;
                }
            },
            {
                name: '图标',
                headerClass: 'value_col',
                colClass: 'value_col',
                style: 'width: 5%;text-align: center;',
                render:function(row){
                    return undefined === row.icon?"" : '<i class="layui-icon" style="font-size: 30px;">'+row.icon+'</i>';
                }
            },
            {
                name: '排序',
                headerClass: 'value_col',
                colClass: 'value_col',
                style: 'width: 5%;text-align: center;',
                render:function(row){
                    return undefined === row.sort?"" : row.sort;
                }
            },
            {
                name: '创建时间',
                headerClass: 'value_col',
                colClass: 'value_col',
                style: 'width: 10%',
                render:function(row){
                    return undefined === row.createDate?"" : new Date(row.createDate).Format("yyyy-MM-dd hh:mm:ss");
                }
            },
            {
                name: '操作',
                headerClass: 'value_col',
                colClass: 'value_col',
                style: 'width: 30%;text-align: center;',
                render: function(row) {
                    return '<a class="layui-btn layui-btn-normal layui-btn-sm" onclick="addChildMenu(' + row.id + ')"><i class="layui-icon">&#xe654;</i> 添加子菜单</a>' +
                            '<a class="layui-btn layui-btn-normal layui-btn-sm" onclick="editChildMenu(' + row.id + ')"><i class="layui-icon">&#xe642;</i> 编辑菜单</a>' +
                            '<a class="layui-btn layui-btn-danger layui-btn-sm" onclick="delMenu(' + row.id + ')"><i class="layui-icon">&#xe640;</i> 删除</a>';
                }
            }
        ];

        var setTree = function(data,layout){
            $("#demo").empty();
            layui.treeGird({
                elem: '#demo', //传入元素选择器
                nodes: data,
                layout: layout
            });
        };

        $(function(){
            $.post("${base}/admin/system/menu/treelist",function(res){
                if(res.success){
                    setTree(res.data,layout);
                }else{
                    layer.msg(res.message);
                }
            });
        });

        var active={
            addUser : function(){
                var addIndex = layer.open({
                    title : "添加系统菜单",
                    type : 2,
                    content : "${base}/admin/system/menu/add",
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
            }
        };

        $('.layui-inline .layui-btn').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

    });

    var addChildMenu = function(data){
        var addIndex = layer.open({
            title : "添加系统菜单",
            type : 2,
            content : "${base}/admin/system/menu/add?parentId="+data,
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
    };

    var editChildMenu = function(data){
        var editIndex = layer.open({
            title : "编辑菜单",
            type : 2,
            content : "${base}/admin/system/menu/edit?id="+data,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回会员列表', '.layui-layer-setwin .layui-layer-close', {
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
    };
    var delMenu =function(data){
        layer.confirm("你确定要删除该菜单么？这将会使得其下的所有子菜单都删除",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("${base}/admin/system/menu/delete",{"id":data},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                location.reload();
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
        )
    };
    //格式化时间
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };
</script>

</html>