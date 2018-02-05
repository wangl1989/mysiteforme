<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>菜单管理--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <link rel="stylesheet" href="//at.alicdn.com/t/font_tnyc012u2rlwstt9.css" media="all" />
    <link rel="stylesheet" href="${base}/static/css/user.css" media="all" />
    <link rel="stylesheet" href="${base }/static/zTree/v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
</head>
<style type="text/css">
    .ztree li span.button.ico_docu {
        background-position: -110px 0;
        margin-right: 2px;
        vertical-align: top;
    }
</style>
<body class="childrenBody">
<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
<script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
<script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
    <div class="layui-row">
        <div class="layui-col-xs2">
            <ul id="treeDemo" class="ztree" ></ul>
        </div>
        <div class="layui-col-xs10">
            <blockquote class="layui-elem-quote news_search">
                <div class="layui-inline">
                    <div class="layui-input-inline">
                        <input type="text" value="" id="menuContent" placeholder="请输入菜单名称" class="layui-input search_input">
                    </div>
                    <div class="layui-input-inline">
                        <input type="text" value="" id="permissionContent" placeholder="请输入权限名称" class="layui-input search_input">
                    </div>
                    <div class="layui-input-inline">
                        <input type="text" value="" id="permissionContent" placeholder="请选择父目录" class="layui-input search_input">
                    </div>

                    <a class="layui-btn" data-type="selectUser">查询</a>
                </div>
                <div class="layui-inline">
                    <a class="layui-btn layui-btn-disabled" data-type="addUser" >添加菜单</a>
                </div>
            </blockquote>
            <div class="layui-form layui-hide" id="menuformdiv">
            </div>
            <div id="page"></div>
        </div>
    </div>

<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.js"></script>
<script type="text/javascript" src="${ctx }/static/zTree/v3/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js"></script>
<script>

    layui.use(['layer','form','table'], function() {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                table = layui.table,
                ztree,
                addIndex,
                editIndex;

        ztree = function (data) {
            var setting = {
                view: {
                    fontCss : {"font-family": "微软雅黑", "font-size": "16px"}
                },
                data: {
                    simpleData: {
                        enable: true,
                        idKey: "id",
                        pIdKey: "pid",
                        rootPId: null
                    }
                },
                callback: {
                    onClick: function(event, treeId, treeNode){
                    }
                }
            };
            zTreeObj = $.fn.zTree.init($("#treeDemo"), setting,  data);
        };

        $(function(){
            $.post("${base}/admin/system/menu/tree",function(res){
                if(res.success){
                    ztree(res.data);
                }else{
                    layer.msg(res.message);
                }
            });
        });

        //批量删除
        var active={
            addUser : function(){
                var addIndex = layer.open({
                    title : "添加会员",
                    type : 2,
                    content : "${base}/admin/system/user/add",
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
            deleteSome : function(){
                var checkStatus = table.checkStatus('test'),
                     data = checkStatus.data;
                if(data.length > 0){
                    console.log(JSON.stringify(data));
                    for(var i=0;i<data.length;i++){
                        var d = data[i];
                        if(d.id == 1){
                            layer.msg("不能删除超级管理员");
                            return false;
                        }
                    }
                    layer.confirm("你确定要删除这些菜单么？",{btn:['是的,我确定','我再想想']},
                        function(){
                            var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                            $.ajax({
                                type:"POST",
                                url:"${base}/admin/system/user/deleteSome",
                                dataType:"json",
                                contentType:"application/json",
                                data:JSON.stringify(data),
                                success:function(res){
                                    layer.close(deleteindex);
                                    if(res.success){
                                        layer.msg("删除成功",{time: 1000},function(){
                                            location.reload();
                                        });
                                    }else{
                                        layer.msg(res.message);
                                    }
                                }
                            });
                        }
                    )
                }else{
                    layer.msg("请选择需要删除的菜单");
                }
            },
            selectUser : function(){
                var searchContent = $("#searchContent").val();
                t.where = {"s_key":searchContent};
                table.reload('test', t);
            }
        };

        $('.layui-inline .layui-btn').on('click', function(){
            var type = $(this).data('type');
            var style = $(this).attr("class");
            if(style.indexOf("disabled")>=0){
                layer.msg("按钮被禁用了");
                $(this).removeClass("layui-btn-disabled");
            }else{
                active[type] ? active[type].call(this) : '';
            }
        });

    });
</script>
</body>
</html>