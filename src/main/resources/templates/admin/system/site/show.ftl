<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>网站基本信息</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->
    <style type="text/css">
        .layui-form-item .layui-inline{ min-width:15%; float:left; margin-right:0; }
        .layui-form-item .role-box {
            position: relative;
        }

    </style>
</head>
<body class="childrenBody">

<form class="layui-form">
    <input type="hidden" name="id" value="${site.id}">
    <table class="layui-table">
        <colgroup>
            <col width="20%">
            <col width="50%">
            <col>
        </colgroup>
        <thead>
        <tr>
            <th>参数说明</th>
            <th>参数值</th>
            <th>变量名</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>网站名称</td>
            <td><input type="text" class="layui-input cmsName" name="name" value="${site.name}" lay-verify="required" placeholder="请输入网站名称"></td>
            <td>name</td>
        </tr>
        <tr>
            <td>网站地址</td>
            <td><input type="text" class="layui-input cmsName" name="url" value="${site.url}" lay-verify="required" placeholder="请输入网站地址"></td>
            <td>url</td>
        </tr>
        <tr>
            <td>是否开放留言</td>
            <td>
                <input type="checkbox" <#if (site.openMessage == true)>checked=""</#if> name="openMessage" lay-skin="switch"  lay-text="打开|关闭">
            </td>
            <td>openMessage</td>
        </tr>
        <tr>
            <td>是否可以匿名留言</td>
            <td>
                <input type="checkbox" <#if (site.noName == true)>checked=""</#if> name="noName" lay-skin="switch"  lay-text="可以|不可">
            </td>
            <td>noName</td>
        </tr>
        <tr>
            <td>当前版本</td>
            <td><input type="text" class="layui-input version" name="version" value="${site.version}" placeholder="请输入当前版本"></td>
            <td>version</td>
        </tr>
        <tr>
            <td>开发作者</td>
            <td><input type="text" class="layui-input author" name="author" value="${site.author}" placeholder="请输入开发作者"></td>
            <td>author</td>
        </tr>
        <tr>
            <td>作者头像</td>
            <td>
                <input type="hidden" class="layui-input author" name="authorIcon" value="${site.authorIcon}">
                <button type="button" class="layui-btn layui-btn-normal" id="test2"><i class="layui-icon"></i>上传一个照片什么的</button>
                <img <#if site.authorIcon??> src="${site.authorIcon}" <#else> src="${base}/static/images/face.jpg " </#if> class="layui-circle" id="userFace" style="width: 100px;height: 100px">
            </td>
            <td>authorIcon</td>
        </tr>
        <tr>
            <td>文件上传方式</td>
            <td>
                <input type="radio" name="fileUploadType" value="local" title="本地上传" <#if (site.fileUploadType == "local") >checked=""</#if>   >
                <input type="radio" name="fileUploadType" value="qiniu" title="七牛云存储" lay-filter="qiniuUpload" <#if (site.fileUploadType == "qiniu") >checked=""</#if>>
                <input type="radio" name="fileUploadType" value="oss" title="阿里云存储" lay-filter="ossUpload" <#if (site.fileUploadType == "oss") >checked=""</#if>>
            </td>
            <td>fileUploadType</td>
        </tr>
        <tr>
            <td>微博</td>
            <td><input type="text" class="layui-input author" name="weibo" value="${site.weibo}" placeholder="请输入微博地址"></td>
            <td>weibo</td>
        </tr>
        <tr>
            <td>QQ</td>
            <td><input type="text" class="layui-input author" name="qq" value="${site.qq}" placeholder="请输入QQ号"></td>
            <td>qq</td>
        </tr>
        <tr>
            <td>git</td>
            <td><input type="text" class="layui-input author" name="git" value="${site.git}" placeholder="请输入码云地址"></td>
            <td>git</td>
        </tr>
        <tr>
            <td>github</td>
            <td><input type="text" class="layui-input author" name="github" value="${site.github}" placeholder="请输入github地址"></td>
            <td>github</td>
        </tr>
        <tr>
            <td>手机</td>
            <td><input type="number" class="layui-input homePage" name="phone" value="${site.phone}" lay-verify="phone" placeholder="请输入手机号码"></td>
            <td>phone</td>
        </tr>
        <tr>
            <td>邮箱</td>
            <td><input type="text" class="layui-input" name="email" value="${site.email}" lay-verify="email" placeholder="请输入邮箱"></td>
            <td>email</td>
        </tr>
        <tr>
            <td>地址</td>
            <td><input type="text" class="layui-input author" name="address" value="${site.address}" placeholder="请输入地址"></td>
            <td>address</td>
        </tr>
        <tr>
            <td>网站LOGO</td>
            <td>
                <div class="layui-upload">
                    <input type="hidden" name="logo" id="logo" value="${site.logo}" >
                    <button type="button" class="layui-btn" id="test1">上传LOGO</button>
                    <div class="layui-upload-list" style="width: 50%;float: right;">
                        <img class="layui-upload-img" id="demo1" <#if (site.logo)??> src="${site.logo}"</#if>>
                        <p id="demoText"></p>
                    </div>
                </div>
            </td>
            <td>logo</td>
        </tr>
        <tr>
            <td>服务器环境</td>
            <td><input type="text" class="layui-input server layui-disabled" name="server" disabled value="${site.server}" placeholder="请输入服务器环境"></td>
            <td>server</td>
        </tr>
        <tr>
            <td>数据库版本</td>
            <td><input type="text" class="layui-input dataBase layui-disabled" name="database" disabled value="${site.database}" placeholder="请输入数据库版本"></td>
            <td>database</td>
        </tr>
        <tr>
            <td>最大上传限制</td>
            <td><input type="number" class="layui-input maxUpload" name="maxUpload" lay-verify="number" value="${site.maxUpload}" placeholder="请输入最大上传限制"></td>
            <td>maxUpload</td>
        </tr>
        <tr>
            <td>默认关键字</td>
            <td><input type="text" class="layui-input keywords" name="keywords" value="${site.keywords}" placeholder="请输入默认关键字"></td>
            <td>keywords</td>
        </tr>
        <tr>
            <td>网站描述</td>
            <td><textarea placeholder="请输入网站描述" name="description"  class="layui-textarea description">${site.description}</textarea></td>
            <td>description</td>
        </tr>
        <tr>
            <td>版权信息</td>
            <td><input type="text" class="layui-input powerby" name="powerby" value="${site.powerby}"  placeholder="请输入网站版权信息"></td>
            <td>powerby</td>
        </tr>
        <tr>
            <td>网站备案号</td>
            <td><input type="text" class="layui-input record" name="record" value="${site.record}" placeholder="请输入网站备案号"></td>
            <td>record</td>
        </tr>
        <tr>
            <td>个人简介</td>
            <td><div id="content">${site.remarks}</div></td>
            <td>remarks</td>
        </tr>
        </tbody>
    </table>
    <div class="layui-form-item" style="text-align: right;">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="site">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/wangEditor.min.js"></script>
<script>
    layui.use(['form','layer','jquery','upload'], function(){
            var form = layui.form,
                layer = layui.layer,
                upload=layui.upload,
                E = window.wangEditor,
                $ = layui.jquery;

        var content_editor = new E('#content');
        //图片上传
        content_editor.customConfig.uploadImgServer = '${base}/file/uploadWang';
        content_editor.customConfig.uploadFileName = 'test';
        // 自定义处理粘贴的文本内容(这里处理图片抓取)
        content_editor.customConfig.pasteTextHandle = function (content) {
            if(undefined === content){
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
        //普通图片上传
        var uploadInst = upload.render({
            elem: '#test1',
            url: '${base}/file/upload',
            accept:'images',
            exts:'ico',
            field:'test',
            before: function(obj){
                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#demo1').attr('src', result); //图片链接（base64）
                });
            },
            done: function(res){
                //如果上传失败
                if(res.success === false){
                    return layer.msg('上传失败');
                }
                $("#logo").val(res.data.url);
            },
            error: function(){
                //演示失败状态，并实现重传
                var demoText = $('#demoText');
                demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-mini demo-reload">重试</a>');
                demoText.find('.demo-reload').on('click', function(){
                    uploadInst.upload();
                });
            }
        });

        upload.render({
            elem: '#test2',
            url: '${base}/file/upload/',
            field: 'test',
            before: function (obj) {
                //预读本地文件示例，不支持ie8
                obj.preview(function (index, file, result) {
                    $('#userFace').attr('src', result); //图片链接（base64）
                });
                layer.load(2, {
                    shade: [0.3, '#333']
                });
            },
            done: function (res) {
                layer.closeAll('loading');
                //如果上传失败
                if (res.success === false) {
                    return layer.msg('上传失败');
                }else{
                    layer.msg("上传成功",{time:1000},function () {
                        $("input[name='authorIcon']").val(res.data.url);
                    })
                }
            }
        });

        form.on('radio(qiniuUpload)', function(data){
            layer.open({
                title : "七牛云信息",
                type : 2,
                content : "${base}/admin/system/site/qiniu",
                area: ['600px','550px'],
                cancel:function (index, layero) {
                    console.log(layero);
                }
            });
        });

        form.on('radio(ossUpload)', function(data){
            layer.open({
                title : "阿里云信息",
                type : 2,
                content : "${base}/admin/system/site/oss",
                area: ['600px','600px']
            });
        });

        form.on('submit(site)',function(data){
            var c = content_editor.txt.html();
            if(null != c || "" !== c || undefined !== c){
                c = c.replace(/\"/g, "'");
                data.field.remarks = c;
            }

            if(null == data.field.openMessage || undefined === data.field.openMessage || "" === data.field.openMessage){
                data.field.openMessage = false;
            }else{
                data.field.openMessage = true;
            }
            if(null == data.field.noName || undefined === data.field.noName || "" === data.field.noName){
                data.field.noName = false;
            }else{
                data.field.noName = true;
            }


            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.post('${base}/admin/system/site/edit',data.field,function (res) {
                layer.close(loadIndex);
               if(res.success){
                   layer.msg("站点信息更新成功",function () {
                       location.reload();
                   });

               }else{
                   layer.msg(res.message);
               }
            });
            return false;
        });
    });
</script>
</body>
</html>