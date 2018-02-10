<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>博客评论--${site.name}</title>
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
    <link rel="stylesheet" href="//at.alicdn.com/t/font_tnyc012u2rlwstt9.css" media="all" />
    <link rel="stylesheet" href="${base}/static/css/user.css" media="all" />
    <style>
        .detail-body{
            margin: 20px 0 0;
            min-height: 306px;
            line-height: 26px;
            font-size: 16px;
            color: #333;
            word-wrap: break-word;
        }
        /* blockquote 样式 */
        blockquote {
            display: block;
            border-left: 8px solid #d0e5f2;
            padding: 5px 10px;
            margin: 10px 0;
            line-height: 1.4;
            font-size: 100%;
            background-color: #f1f1f1;
        }
        /* code 样式 */
        code {
            display: inline-block;
            *display: inline;
            *zoom: 1;
            background-color: #f1f1f1;
            border-radius: 3px;
            padding: 3px 5px;
            margin: 0 3px;
        }
        pre code {
            display: block;
        }
    </style>
</head>
<body class="childrenBody">
<fieldset class="layui-elem-field">
  <legend>博客评论检索</legend>
  <div class="layui-field-box">
    <form class="layui-form" id="searchForm">
    <div class="layui-inline" style="margin-left: 15px">
            <label>评论内容:</label>
                <div class="layui-input-inline">
                <input type="text" value="" name="s_content" placeholder="请输入评论内容" class="layui-input search_input">
                </div>
    </div>
    <div class="layui-inline" style="margin-left: 15px">
            <label>管理员是否回复:</label>
                <div class="layui-input-inline">
                <select name="s_isAdminReply">
                    <option value="" selected="">请选择管理员是否回复</option>
                    <option value="true" >是</option>
                    <option value="false" >否</option>
                </select>
                </div>
    </div>
        <div class="layui-inline">
            <a class="layui-btn" lay-submit="" lay-filter="searchForm">查询</a>
        </div>
        <div class="layui-inline" >
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
        <#--<div class="layui-inline">-->
            <#--<a class="layui-btn layui-btn-normal" data-type="addBlogComment">添加博客评论</a>-->
        <#--</div>-->
    </form>
  </div>
</fieldset>
<div class="layui-form users_list">
    <table class="layui-table" id="test" lay-filter="demo"></table>

    <script type="text/html" id="content">
        {{#  if(d.content != "" && d.content != null){ }}
        <span><button lay-event="showcontent" class="layui-btn layui-btn-warm layui-btn-sm">评论内容</button></span>
        {{#  } else { }}
        <span ></span>
        {{#  } }}
    </script>

    <script type="text/html" id="replyContent">
        {{#  if(d.replyContent != "" && d.replyContent != null){ }}
        <span><button lay-event="showReplyContent" class="layui-btn layui-btn-warm layui-btn-sm">回复内容</button></span>
        {{#  } else { }}
        <span ></span>
        {{#  } }}
    </script>
    <script type="text/html" id="isAdminReply">
        {{#  if(d.adminReply == true){ }}
        <span>是</span>
        {{# }else{ }}
        <span>否</span>
        {{# } }}
    </script>

    <script type="text/html" id="barDemo">
        {{# if(d.replyId == null){ }}
        <a class="layui-btn layui-btn-xs" lay-event="edit">回复</a>
        {{# } }}
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    </script>
</div>
<div id="page"></div>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js?t=${.now?long}"></script>
<script>
    layui.use(['layer','form','table','laydate'], function() {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                laydate = layui.laydate,
                table = layui.table;


        //监听工具条
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'edit'){
                var editIndex = layer.open({
                    title : "回复博客评论",
                    type : 2,
                    content : "${base}/admin/blogComment/edit?id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回博客评论列表', '.layui-layer-setwin .layui-layer-close', {
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
                layer.confirm("你确定要删除该评论么？",{btn:['是的,我确定','我再想想']},
                        function(){
                            $.post("${base}/admin/blogComment/delete",{"id":data.id},function (res){
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
            }
            if(obj.event === "showcontent"){
                layer.open({
                    type: 1,
                    closeBtn: 0,
                    shadeClose: true,
                    title: 'content预览',
                    area: ['700px', '500px'],
                    content: '<div class="detail-body" style="margin:20px;">'+data.content+'</div>'
                });
            }
            if(obj.event === "showReplyContent"){
                layer.open({
                    type: 1,
                    title: 'content预览',
                    closeBtn: 0,
                    shadeClose: true,
                    area: ['700px', '500px'],
                    content: '<div class="detail-body" style="margin:20px;">'+data.replyContent+'</div>'
                });
            }
        });

        var t = {
            elem: '#test',
            url:'${base}/admin/blogComment/list',
            method:'post',
            page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
                //,curr: 5 //设定初始在第 5 页
                groups: 2, //只显示 1 个连续页码
                first: "首页", //显示首页
                last: "尾页", //显示尾页
                limits:[3,10, 20, 30]
            },
            cellMinWidth: 80, //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            cols: [[
                {type:'checkbox'},
                {field:'content', title: '评论内容',templet:'#content'},
                {field:'ip', title: 'ip'},
                {field:'system', title: '操作系统'},
                {field:'browser', title: '浏览器'},
                {field:'adminReply', title: '管理员是否回复',templet:'#isAdminReply'},
                {field:'replyContent', title: '管理员回复内容',templet:'#replyContent'},
                {field:'createDate',  title: '创建时间',width:'15%',templet:'<div>{{ layui.laytpl.toDateString(d.createDate) }}</div>',unresize: true}, //单元格内容水平居中
                {fixed: 'right', title:'操作',  width: '15%', align: 'center',toolbar: '#barDemo'}
            ]]
        };
        table.render(t);

        var active={
            addBlogComment : function(){
                var addIndex = layer.open({
                    title : "添加博客评论",
                    type : 2,
                    content : "${base}/admin/blogComment/add",
                    success : function(layero, addIndex){
                        setTimeout(function(){
                            layer.tips('点击此处返回博客评论列表', '.layui-layer-setwin .layui-layer-close', {
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

        $('.layui-inline .layui-btn-normal').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        form.on("submit(searchForm)",function(data){
            t.where = data.field;
            table.reload('test', t);
            return false;
        });

    });
</script>
</body>
</html>