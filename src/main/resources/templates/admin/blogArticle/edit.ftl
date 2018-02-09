<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>博客内容编辑--${site.name}</title>
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
    <link rel="stylesheet" href="${base}/static/zTree/v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
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
        .multiSelect{ line-height:normal; height:auto; padding:4px 10px; overflow:hidden;min-height:38px; margin-top:-38px; left:0; z-index:99;position:relative;background:none;}
        .multiSelect a{ padding:2px 5px; background:#908e8e; border-radius:2px; color:#fff; display:block; line-height:20px; height:20px; margin:2px 5px 2px 0; float:left;}
        .multiSelect a span{ float:left;}
        .multiSelect a i{ float:left; display:block; margin:2px 0 0 2px; border-radius:2px; width:8px; height:8px; background:url(${base}/static/images/close.png) no-repeat center; background-size:65%; padding:4px;}
        .multiSelect a i:hover{ background-color:#545556;}

        .layui-field-box a{ padding:2px 5px; background:#908e8e; border-radius:2px; color:#fff; display:block; line-height:20px; height:20px; margin:2px 5px 22px 0; float:left;}
        .layui-field-box a span{ float:left;}
        .boxadd{ float:left; display:block; margin:2px 0 0 2px; border-radius:2px; width:8px; height:8px; background-size:65%; padding:4px;}
    </style>
</head>
<body class="childrenBody">
<form class="layui-form" style="width:80%;">
    <input value="${blogArticle.id}" name="id" type="hidden">
    <div class="layui-form-item">
        <label class="layui-form-label">标题</label>
        <div class="layui-input-block">
                <input  type="text" class="layui-input" value = "${blogArticle.title}" name="title" lay-verify="required" placeholder="请输入标题">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">副标题</label>
        <div class="layui-input-block">
                <input  type="text" class="layui-input" value = "${blogArticle.subTitle}" name="subTitle"  placeholder="请输入副标题">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属栏目</label>
        <div class="layui-input-block">
            <input type="hidden" name="channelId" value="${blogArticle.channelId}">
            <input type="hidden" id="oldChannelId" value="${blogArticle.channelId}">
            <input  type="text"  class="layui-input layui-disabled" disabled  placeholder="请选择一个父栏目" id="channelNameShow">
            <div class="grid-demo grid-demo-bg1"><ul id="treeDemo" class="ztree"></ul></div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">摘要</label>
        <div class="layui-input-block">
                <textarea name="marks"   placeholder="请输入摘要" class="layui-textarea">${blogArticle.marks}</textarea>

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">显示图片</label>
        <div class="layui-input-block">
                <input type="hidden" class="layui-input" name="showPic" id="showPic" value="${blogArticle.showPic}" >
                <div class="layui-upload">
                    <button type="button" class="layui-btn" id="test_showPic">上传显示图片</button>
                    <div class="layui-upload-list">
                        <img class="layui-upload-img" id="demo_showPic" <#if (blogArticle.showPic??)>src="${blogArticle.showPic}"</#if> >
                        <p id="demoText_showPic"></p>
                    </div>
                </div>

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">文章类型</label>
        <div class="layui-input-block">
                <@my type="blog_article_category">
                    <#list result as r>
                    <input type="radio" name="category"  value="${r.value}" lay-filter="category" title="${r.label}" <#if (blogArticle.category == r.value)> checked="" </#if>>
                    </#list>
                </@my>
        </div>
    </div>
    <div id="outLinkUrl">
        <#if (blogArticle.category == "2")>
            <div class="layui-form-item">
                <label class="layui-form-label">外链地址</label>
                <div class="layui-input-block">
                    <input  type="text" class="layui-input" value = "${blogArticle.outLinkUrl}" lay-verify="required" name="outLinkUrl"  placeholder="请输入外链地址">
                </div>
            </div>
        </#if>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">来源</label>
        <div class="layui-input-block">
                <input  type="text" class="layui-input" value = "${blogArticle.resources}" name="resources"  placeholder="请输入来源">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">文章标签</label>
        <div class="layui-input-block">
            <input  type="text"  class="layui-input" name="resources">
            <div class="layui-input multiSelect">
                <@tags aid = "${blogArticle.id}">
                    <#if (result?? && result?size>0) >
                    <#list result as tag>
                        <a href="javascript:" class="listTag" data-id="${tag.id}"><span >${tag.name}</span><i></i></a>
                    </#list>
                    </#if>
                </@tags>
            </div>
            <fieldset class="layui-elem-field">
                <legend>推荐标签</legend>
                <div class="layui-field-box">
                    <#if (taglist?size>0)>
                        <#list taglist as item>
                            <a href="javascript:" class="listTag" data-id="${item.id}"><span >${item.name}</span></a>
                        </#list>
                    </#if>
                    <a class="layui-btn layui-btn-xs" style="padding-bottom: 22px;padding-right: 16px;"><i class="layui-icon boxadd" style="line-height: 10px;width: 100%">&#xe654;</i> </a>
                </div>
            </fieldset>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">发布时间</label>
        <div class="layui-input-block">
                <input type="text" name="publistTime" id="publistTime" <#if (blogArticle.publistTime)??>value="${blogArticle.publistTime?string('yyyy-MM-dd')}"</#if>  lay-verify="date" placeholder="请选择发布时间" autocomplete="off" class="layui-input">

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">内容</label>
        <div class="layui-input-block">
                <div id="content">${blogArticle.content}</div>

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否置顶</label>
        <div class="layui-input-block">
                <input type="checkbox" name="top"  lay-skin="switch" lay-text="是|否" value="1"  <#if (blogArticle.top == true)> checked </#if> >

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否推荐</label>
        <div class="layui-input-block">
                <input type="checkbox" name="recommend"  lay-skin="switch" lay-text="是|否" value="1"  <#if (blogArticle.recommend == true)> checked </#if> >

        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">排序值</label>
        <div class="layui-input-block">
            <input  type="text" class="layui-input" value = "${blogArticle.sort}" name="sort" lay-verify="required"  placeholder="请输入排序值">
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addBlogArticle">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/wangEditor.min.js"></script>
<script type="text/javascript" src="${base}/static/zTree/v3/js/jquery.ztree.all-3.5.min.js"></script>
<script>
    layui.use(['form','jquery','layer','upload','laydate'],function(){
        var form      = layui.form,
                $     = layui.jquery,
                upload = layui.upload,
                laydate = layui.laydate,
                imageIndex,
                E = window.wangEditor,
                layer = layui.layer,
                zTreeObj,
                setting  = {callback:{
                        onClick : function (event, treeId, treeNode) {
                            $("#channelNameShow").val(treeNode.name);
                            $("input[name='channelId']").val(treeNode.id);
                        }
                }},
                outLinkUrlHtml = '<div class="layui-form-item">\n' +
                        '                <label class="layui-form-label">外链地址</label>\n' +
                        '                <div class="layui-input-block">\n' +
                        '                    <input  type="text" class="layui-input" lay-verify="required" value = "${blogArticle.outLinkUrl}" name="outLinkUrl"  placeholder="请输入外链地址">\n' +
                        '                </div>\n' +
                        '            </div>';
        zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, ${ztreeData});
        <#if (blogArticle.channelId != null && blogArticle.channelId != "")>
        var choose = zTreeObj.getNodeByParam("id", '${blogArticle.channelId}', null);
        if(choose !== undefined && choose != null){
            $("#channelNameShow").val(choose.name);
            $("input[name='channelId']").val(choose.id);
            zTreeObj.selectNode(choose);
        }
        </#if>

        //普通图片上传
        var upload_showPic = upload.render({
            elem: '#test_showPic',
            url: '${base}/file/upload/',
            field:'test',
            before: function(obj){
                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#demo_showPic').attr('src', result); //图片链接（base64）
                });
                imageIndex = layer.load(2, {
                    shade: [0.3, '#333']
                });
            },
            done: function(res){
                layer.close(imageIndex);
                //如果上传失败
                if(res.success == false){
                    return layer.msg('上传失败');
                }
                $("#showPic").val(res.data.url);
            },
            error: function(){
                //演示失败状态，并实现重传
                var demoText = $('#demoText_showPic');
                demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-mini demo-reload">重试</a>');
                demoText.find('.demo-reload').on('click', function(){
                    upload_showPic.upload();
                });
            }
        });

        /**
         * 文章标签移除
         */
        $(".multiSelect").on("click","a i",function () {
            $(this).parent('a').remove();
        });
        /**
         * 文章标签新增已有
         */
        $(".layui-field-box").on("click","a.listTag",function () {
            var t = $(this).append("<i></i>"),
                    id = $(this).data("id"),
                    d = [];
            $(".multiSelect").children("a").each(function () {
                d.push($(this).data("id"));
            });
            if(d.length<5 && d.indexOf(id)<0){
                $(".multiSelect").append(t.get(0).outerHTML);
            }
            $(this).children("i").remove();
        });
        //添加一个新的标签
        $(".layui-btn-xs").on("click",function () {
            var t = $(this);
            layer.prompt({title: '输入一个标签的名字', formType: 0}, function(pass, index,elem){
                var reg = new RegExp("^[A-Za-z0-9\u4e00-\u9fa5]+$");
                if(!reg.test(pass)){
                    layer.alert("请输入中文、数字和英文！");
                    elem.val("");
                }else{
                    $.post("${base}/admin/blogTags/checkTagName",{name:pass.trim()},function (res) {
                        if(res.success){
                            t.before('<a href="javascript:;" class="listTag" data-id="'+res.data.id+'"><span >'+res.data.name+'</span></a>');
                            layer.close(index);
                        }else{
                            layer.msg(res.message,{time:1000});
                            elem.val("");
                        }
                    });
                }
            });

        });

        //初始赋值
        laydate.render({
            elem: '#publistTime'
            <#if (blogArticle.publistTime)??>
            ,value: '${blogArticle.publistTime?string("yyyy-MM-dd")}'
            </#if>
        });

        var content_editor = new E('#content');
            //图片上传
        content_editor.customConfig.uploadImgServer = '${base}/file/uploadWang';
        content_editor.customConfig.uploadFileName = 'test';
        // 自定义处理粘贴的文本内容(这里处理图片抓取)
        content_editor.customConfig.pasteTextHandle = function (content) {
            if(undefined == content){
                return content;
            }
            if(content.indexOf("src=")<=0){
                return content;
            }
            var loadContent = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.ajax({
                url: "${base}/file/doContent/",
                type: "POST",
                async: false,
                data:{"content":content},
                dataType: "json",
                success:function(res){
                    layer.close(loadContent);
                    content = res.data;
                }
            });
            return content;
        };
        // 关闭粘贴样式的过滤
        content_editor.customConfig.pasteFilterStyle = false;
        content_editor.customConfig.customAlert = function (info) {
            // info 是需要提示的内容
            layer.msg(info);
        };
        content_editor.create();

        form.on('radio(category)', function(data){
            if(data.value === "1"){
                $("#outLinkUrl").empty();
            }
            if(data.value === "2"){
                $("#outLinkUrl").empty().append(outLinkUrlHtml);
            }
        });

        form.on("submit(addBlogArticle)",function(data){
            if(null === data.field.publistTime || "" ===data.field.publistTime){
                delete data.field["publistTime"];
            }else{
                data.field.publistTime = new Date(data.field.publistTime);
            }
            //编辑器数据
            var c = content_editor.txt.html(),
            ct=content_editor.txt.text();
            if(null === ct || "" === ct || undefined === ct){
                layer.msg("内容不能为空");
                return false;
            }
            if(null === c || "" === c || undefined === c){
                layer.msg("editor不能为空");
                return false;
            }
            c = c.replace(/\"/g, "'");
            ct = ct.replace(/\"/g, "'");
            data.field.content = c;
            data.field.text = ct;
            //是否置顶按钮
            if(undefined === data.field.top || '0' === data.field.top || null === data.field.top){
                data.field.top = false;
            }else{
                data.field.top = true;
            }
            //是否推荐按钮
            if(undefined === data.field.recommend || '0' === data.field.recommend || null === data.field.recommend){
                data.field.recommend = false;
            }else{
                data.field.recommend = true;
            }

            //博客标签数据
            var d = [];
            $(".multiSelect").children("a").each(function () {
                d.push({id:$(this).data("id")});
            });
            if(d.length>0){
                data.field.blogTags = d;
            }

            if(undefined === data.field.channelId || '' === data.field.channelId || null === data.field.channelId){
                layer.msg("请选择一个栏目");
                return false;
            }
            console.log("原来的值为:"+$("#oldChannelId").val()+"---现在的值为:"+data.field.channelId);
            if($("#oldChannelId").val() !== data.field.channelId){
                layer.msg('栏目已经发生改变,你确定要这么做么？', {
                    btn: ['我确定','我再想想'],
                    time: 20000,
                    yes:function(){
                        submitForm(data);
                    }
                });
            }else {
                submitForm(data);
            }
            return false;
        });

        var submitForm = function (data) {
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.ajax({
                url:"${base}/admin/blogArticle/edit",
                type:"POST",
                data:JSON.stringify(data.field),
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function(res){
                    layer.close(loadIndex);
                    if(res.success){
                        //刷新父页面
                        var node = parent.zTreeObj.getNodeByParam('id',data.field.channelId);
                        parent.zTreeObj.selectNode(node);
                        parent.zTreeObj.setting.callback.onClick(null, parent.zTreeObj.setting.treeId, node);
                        parent.layer.closeAll();
                        parent.layer.msg("博客内容更新成功！",{time:1000});
                    }else{
                        layer.msg(res.message);
                    }
                }
            });
        }

    });
</script>
</body>
</html>