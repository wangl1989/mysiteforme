<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>博客内容--${site.name}</title>
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
    <link rel="stylesheet" href="${base}/static/zTree/v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
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

        /***
        *  ztree 图标变成黄色
        */
        .ztree .line{
            line-height: 0;
            border-top:none;
            float: none;
        }
        .ztree li span.button.ico_docu {
            background-position: -110px 0;
            margin-right: 2px;
            vertical-align: top;
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
<div class="layui-col-xs2">
    <div class="grid-demo grid-demo-bg1"><ul id="treeDemo" class="ztree"></ul></div>
</div>
<div class="layui-col-xs10">
    <fieldset class="layui-elem-field">
        <legend>博客内容检索</legend>
        <div class="layui-field-box">
            <form class="layui-form" id="searchForm">
                <div class="layui-inline" style="margin-left: 15px">
                    <label>标题:</label>
                    <div class="layui-input-inline">
                        <input type="text" value="" name="s_title" placeholder="请输入标题" class="layui-input search_input">
                    </div>
                </div>
                <div class="layui-inline" style="margin-left: 15px">
                    <label>文章类型:</label>
                    <div class="layui-input-inline">
                        <select name="s_category">
                            <option value="" selected="">请选择文章类型</option>
                    <@my type="blog_article_category">
                        <#list result as r>
                    <option value="${r.value}" >${r.label}</option>
                        </#list>
                    </@my>
                        </select>
                    </div>
                </div>
                <div class="layui-inline" style="margin-left: 15px">
                    <label>发布时间:</label>
                    <div class="layui-input-inline">
                        <input type="text" name="s_beginPublistTime" id="beginPublistTime" lay-verify="date"  autocomplete="off" class="layui-input">
                    </div>
                    <span>-</span>
                    <div class="layui-input-inline">
                        <input type="text" name="s_endPublistTime" id="endPublistTime" lay-verify="date"  autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline" style="margin-left: 15px">
                    <label>内容:</label>
                    <div class="layui-input-inline">
                        <input type="text" value="" name="s_content" placeholder="请输入内容" class="layui-input search_input">
                    </div>
                </div>
                <div class="layui-inline">
                    <a class="layui-btn" lay-submit="" lay-filter="searchForm">查询</a>
                </div>
                <div class="layui-inline" >
                    <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                </div>
                <div class="layui-inline">
                    <a class="layui-btn layui-btn-normal" data-type="addBlogArticle">添加博客内容</a>
                </div>
            </form>
        </div>
    </fieldset>
    <div class="layui-form users_list">
        <table class="layui-table" id="test" lay-filter="demo"></table>
        <script type="text/html" id="category">
            <@my type="blog_article_category">
                <#list result as r>
                {{#  if(d.category == ${r.value}){ }}
                <span>${r.label}</span>
                {{#  } }}
                </#list>
            </@my>
        </script>
        <script type="text/html" id="content">
            {{#  if(d.content != "" && d.content != null){ }}
            <span><button lay-event="showcontent" class="layui-btn layui-btn-warm layui-btn-sm">文章预览</button></span>
            {{#  } else { }}
            <span ></span>
            {{#  } }}
        </script>
        <script type="text/html" id="channel">
            {{#  if(d.blogChannel != "" && d.blogChannel != null){ }}
            <span>{{ d.blogChannel.name }}</span>
            {{#  } else { }}
            <span >未归类</span>
            {{#  } }}
        </script>
        <script type="text/html" id="isTop">
            {{#  if(d.top == true){ }}
            <span>是</span>
            {{# }else{ }}
            <span>否</span>
            {{# } }}
        </script>
        <script type="text/html" id="isRecommend">
            {{#  if(d.recommend == true){ }}
            <span>是</span>
            {{# }else{ }}
            <span>否</span>
            {{# } }}
        </script>
        <script type="text/html" id="userStatus">
            <!-- 这里的 checked 的状态只是演示 -->
            {{#  if(d.delFlag == false){ }}
            <span class="layui-badge layui-bg-green">正常</span>
            {{#  } else { }}
            <span class="layui-badge layui-bg-gray">停用</span>
            {{#  } }}
        </script>

        <script type="text/html" id="barDemo">
            <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
            <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
        </script>
    </div>
    <div id="page"></div>
</div>
<script type="text/javascript" src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js"></script>
<script type="text/javascript" src="${base}/static/zTree/v3/js/jquery.ztree.all-3.5.min.js"></script>
<script>
    var zTreeObj;
    layui.use(['layer','form','table','laydate'], function() {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                laydate = layui.laydate,
                table = layui.table,
                setting  = {callback:{
                    onClick : function (event, treeId, treeNode) {
                        var id = treeNode.id,
                                data;
                        if(id === undefined || id == null){
                            data = {s_channelId:""}
                        }else{
                            data = {s_channelId:id};
                        }
                        t.where = data;
                        table.reload('test', t);
                    }
                }};

        var beginPublistTime= laydate.render({//渲染开始时间选择
            elem: '#beginPublistTime', //通过id绑定html中插入的start
            type: 'date',
            max:"2099-12-31",//设置一个默认最大值
            done: function (value, dates) {
                endPublistTime.config.min ={
                    year:dates.year,
                    month:dates.month-1, //关键
                    date: dates.date,
                    hours: 0,
                    minutes: 0,
                    seconds : 0
                };
            }
        });

        var endPublistTime= laydate.render({//渲染结束时间选择
            elem: '#endPublistTime',//通过id绑定html中插入的end
            type: 'date',
            min:"1970-1-1",//设置min默认最小值
            done: function (value, dates) {
                beginPublistTime.config.max={
                    year:dates.year,
                    month:dates.month-1,//关键
                    date: dates.date,
                    hours: 0,
                    minutes: 0,
                    seconds : 0
                }
            }
        });

        $(function() {
            var tableload = layer.msg('栏目树拼命加载中', {
                icon: 16
                ,shade: 0.01
            });
            $.post("${base}/admin/blogChannel/ztreeData",function (res) {
                layer.close(tableload);
                if(res.success){
                    var zNodes = res.data;
                    var newNode = [{name:"未归类",id:-1},{name:"所有文章"}];
                    zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, zNodes);
                    newNode = zTreeObj.addNodes(null,newNode);
                }else{
                    layer.msg("加载栏目树异常:{"+res.message+"}");
                }
            });
        });

        //监听工具条
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'edit'){
                var editIndex = layer.open({
                    title : "编辑博客内容",
                    type : 2,
                    content : "${base}/admin/blogArticle/edit?id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回博客内容列表', '.layui-layer-setwin .layui-layer-close', {
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
                layer.confirm("你确定要删除该博客内容么？",{btn:['是的,我确定','我再想想']},
                        function(){
                            $.post("${base}/admin/blogArticle/delete",{"id":data.id},function (res){
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
            if(obj.event === "imageshowPic"){
                layer.photos({
                    photos: '#showPic_'+data.id,
                    anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机（请注意，3.0之前的版本用shift参数）
                });
            }
            if(obj.event === "showcontent"){
                var contentIndex = layer.open({
                    type: 1,
                    title: 'content预览',
                    content: '<div class="detail-body" style="margin:20px;">'+data.content+'</div>'
                });
                layer.full(contentIndex);
            }
        });

        var t = {
            elem: '#test',
            url:'${base}/admin/blogArticle/list',
            method:'post',
            width: ($(parent.window).width()-223)*(10/12),
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
                {field:'title', title: '标题'},
                {field:'category', title: '文章类型',templet:'#category'},
                {field:'channelId', title: '所属栏目',templet:'#channel'},
                {field:'content', title: '内容',templet:'#content'},
                {field:'isTop', title: '是否置顶',templet:'#isTop'},
                {field:'isRecommend', title: '是否推荐',templet:'#isRecommend'},
                {field:'commentCount', title: '评论数'},
                {field:'publistTime',  title: '发布时间',templet:'<div>{{ layui.laytpl.toDateString(d.publistTime,"yyyy-MM-dd") }}</div>',unresize: true},
                {fixed: 'right', title:'操作',  width: '13%', align: 'center',toolbar: '#barDemo'}
            ]]
        };
        table.render(t);

        var active={
            addBlogArticle : function(){
                var c = zTreeObj.getSelectedNodes(),url="${base}/admin/blogArticle/add";
                if(c.length>0){
                    if(c[0].id != null && c[0].id !== ""){
                        url = url+"?channelId="+c[0].id;
                    }
                }
                var addIndex = layer.open({
                    title : "添加博客内容",
                    type : 2,
                    content : url,
                    success : function(layero, addIndex){
                        setTimeout(function(){
                            layer.tips('点击此处返回博客内容列表', '.layui-layer-setwin .layui-layer-close', {
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
            var d = data.field,
                c = zTreeObj.getSelectedNodes();
            if(c.length>0){
                if(c[0].id != null && c[0].id !== ""){
                    d.s_channelId= c[0].id;
                }
            }
            t.where = d;
            table.reload('test', t);
            return false;
        });

    });
</script>
</body>
</html>