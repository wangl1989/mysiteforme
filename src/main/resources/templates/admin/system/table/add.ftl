<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>数据表添加--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
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
        .detail-body{
            margin: 20px 20px 20px 20px;
            min-height: 306px;
            line-height: 26px;
            color: #333;
            word-wrap: break-word;
        }
    </style>
</head>
<body class="childrenBody">
<form class="layui-form" style="width:80%;">
    <div class="layui-form-item">
        <label class="layui-form-label" style="width: 100px;">表名</label>
        <div class="layui-input-block" style="margin-left: 130px">
            <input type="text" class="layui-input" name="name" id="tablenameinput" lay-verify="tablename" placeholder="请输入表名">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label" style="width: 100px;">请输入备注</label>
        <div class="layui-input-block" style="margin-left: 130px">
            <input type="text" class="layui-input" name="comment" lay-verify="required" placeholder="请输入备注">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label" style="width: 100px;">请选择表类型</label>
        <div class="layui-input-block">
            <input type="radio" name="tabletype" value="1" title="基本类型" checked="">
            <input type="radio" name="tabletype" value="2" title="树结构类型(有父子继承关系的)">
            <input type="radio" name="tabletype" value="3" title="辅助表(多对多关联关系表)">
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="addUser">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            <a class="layui-btn layui-btn-normal" id="addField">添加字段</a>
        </div>
    </div>
</form>
<fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
    <legend>数据表格区域</legend>
</fieldset>
    <div id="mybtnlen">
        <table class="layui-hide" id="demo" lay-filter="demo"></table>
    </div>

    <script type="text/html" id="mShow">
        {{# if(d.dofor == "0"){ }}
        <div>辅助字段</div>
        {{# }else if(d.dofor == "input"){ }}
        <div>输入框</div>
        {{# }else if(d.dofor == "select"){ }}
        <div>下拉框</div>
        {{# }else if(d.dofor == "radio"){ }}
        <div>单选按钮</div>
        {{# }else if(d.dofor == "checkbox"){ }}
        <div>复选框</div>
        {{# }else if(d.dofor == "switch"){ }}
        <div>开关</div>
        {{# }else if(d.dofor == "timer"){ }}
        <div>时间控件</div>
        {{# }else if(d.dofor == "textarea"){ }}
        <div>普通文本域</div>
        {{# }else if(d.dofor == "editor"){ }}
        <div>编辑器</div>
        {{# }else if(d.dofor == "uploadImg"){ }}
        <div>上传图片</div>
        {{# }else{ }}
        <div>上传文件</div>
        {{# } }}
    </script>
    <script type="text/html" id="isNullValue">
        {{#  if(d.isNullValue == "YES"){ }}
        <span class="layui-badge layui-bg-green">YES</span>
        {{#  } else { }}
        <span class="layui-badge">NO</span>
        {{#  } }}
    </script>
    <script type="text/html" id="isShow">
        {{#  if(d.listIsShow == true){ }}
        <span class="layui-badge layui-bg-green">是</span>
        {{#  } else { }}
        <span class="layui-badge">否</span>
        {{#  } }}
    </script>
    <script type="text/html" id="isSearch">
        {{#  if(d.listIsSearch == true){ }}
        <span class="layui-badge layui-bg-green">是</span>
        {{#  } else { }}
        <span class="layui-badge">否</span>
        {{#  } }}
    </script>
    <script type="text/html" id="barDemo">
        <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    </script>


<div class="detail-body" id="fieldForm" style="display: none">
    <form class="layui-form" style="width:90%;" id="myform">
        <div class="layui-form-item">
            <label class="layui-form-label">字段名称</label>
            <div class="layui-input-block">
                <input type="text" class="layui-input" name="name" id="myname" lay-verify="fieldname" lay-verType="tips" placeholder="请输入字段名">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段类型</label>
            <div class="layui-input-block">
                <select name="type" lay-search="" lay-verify="typeCheck" lay-verType="tips" lay-filter="typeFilter">
                    <option value="">请选择一个字段类型</option>
                    <optgroup label="字符串类型">
                        <option value="VARCHAR" data-default="255">VARCHAR(JAVA:String)</option>
                        <option value="CHAR" data-default="255">CHAR(JAVA:String)</option>
                        <#--<option value="BLOB" data-default="">BLOB(JAVA:byte)</option>-->
                        <option value="TEXT" data-default="">TEXT(JAVA:String)</option>
                    </optgroup>
                    <optgroup label="布尔类型">
                        <option value="BIT" data-default="1">BIT(JAVA:Boolean)</option>
                    </optgroup>
                    <optgroup label="数字类型">
                        <option value="INT" data-default="11">INT(JAVA:Integer)</option>
                        <option value="BIGINT" data-default="20">BIGINT(JAVA:Long)</option>
                        <option value="FLOAT" data-default="20">FLOAT(JAVA:Float)</option>
                        <option value="DOUBLE" data-default="20">DOUBLE(JAVA:Double)</option>
                        <option value="DECIMAL" data-default="11">DECIMAL(JAVA:BigDecimal)</option>
                    </optgroup>
                    <optgroup label="日期类型">
                        <option value="DATETIME">DATETIME(JAVA:Date)</option>
                    </optgroup>
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段长度</label>
            <div class="layui-input-block">
                <input type="number" class="layui-input" lay-verify="lengthCheck"  lay-verType="tips" name="length" id="fieldLength"  placeholder="字段长度(不确认长度可以不填)">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段注释</label>
            <div class="layui-input-block">
                <input type="text" class="layui-input" name="comment" lay-verify="required" lay-verType="tips" placeholder="请输入字段注释">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否显示</label>
            <div class="layui-input-block">
                <input type="checkbox" name="listIsShow" title="table是否显示"  checked>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否搜索</label>
            <div class="layui-input-block">
                <input type="checkbox" name="listIsSearch" title="列表是否搜索">
            </div>
        </div>
        <div class="layui-form-item" id="selectDoforDiv">
            <label class="layui-form-label">字段用途</label>
            <div class="layui-input-block" id="doforSelect">
                <select name="dofor" lay-verify="required" lay-verType="tips" lay-filter="doforSelect">
                    <option value="">请选择一个用途</option>
                    <option value="0">辅助字段,不在页面显示</option>
                    <option value="input">输入框</option>
                    <option value="select">下拉框</option>
                    <option value="radio">单选框</option>
                    <#--<option value="checkbox">复选框</option>-->
                    <option value="switch">开关</option>
                    <option value="textarea">普通文本域</option>
                    <option value="timer">时间控件</option>
                    <option value="editor">编辑器</option>
                    <option value="uploadImg">上传图片</option>
                    <option value="uploadFile">上传文件</option>
                </select>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">是否可为空</label>
            <div class="layui-input-block">
                <input type="checkbox" checked="" name="isNullValue" id="isNullValue" value="YES" lay-skin="switch" lay-text="YES|NO">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="fieldFilter" id="fieldsubmit">我都填好了</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    <form>
</div>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    var tableData = [],
        fieldnames = [];  //已经填写好的字段数组
    layui.use(['form','jquery','layer','table'],function(){
        var form = layui.form,
                $    = layui.jquery,
                layer = layui.layer,
                table = layui.table,
                fieldFormShow,
                ft,
                fieldFormHtml = $("#fieldForm").html();

        $("#addField").on('click',function () {
            $("#fieldForm").empty().html(fieldFormHtml);
            if($("input[name='tabletype']:checked").val() === '3'){
                $("input[name='length']").val("20").attr("disabled","");
                $("select[name='type']").empty().html('<option value="BIGINT" data-default="20">BIGINT(JAVA:Long)</option>');
                $("select[name='dofor']").empty().html('<option value="0">辅助字段,不在页面显示</option>');
                $("input[name='listIsShow']").removeAttr("checked").attr("disabled","");
                $("input[name='listIsSearch']").removeAttr("checked").attr("disabled","");
                $("input[name='isNullValue']").removeAttr("checked").attr("disabled","");

                if(fieldnames.length === 2){
                    layer.alert("辅助表最多只能两个字段");
                    return false;
                }
            }
            form.render();
            ft = "";
            fieldFormShow =layer.open({
                type: 1,
                title: '添加一个字段',
                area:['700px', '600px'],
                content: $('#fieldForm')
            });
        });

        form.verify({
            typeCheck:function (value,item) {
                if(null == value || "" === value){
                    return '字段类型不能为空';
                }
            },
            lengthCheck:function (value,item) {
                if(value !== "" && value != null){
                    if(isNaN(value) || value<=0){
                        return '必须输入一个正整数';
                    }
                }else{
                    var v = $("select[name='type']").find("option:selected").data("default");
                    if(v !=null && v !== ""){
                        $("input[name='length']").val(v);
                        return '该类型的字段长度必须大于0';
                    }
                }
            },
            fieldname:function (value,item) {
                if(null == value || '' === value){
                    return '字段名不能为空';
                }
                if(/[^a-z_]{1,}$/.test(value)){
                    return '字段名只能以小写英文开头且只能包含小写英文字母下划线';
                }
                if(/(^\_)|(\__)|(\_+$)/.test(value)){
                    return '字段名首尾不能出现下划线\'_\'';
                }
                if(fieldnames.indexOf(value)>=0){
                    return '已存在该字段名';
                }
            },
            updatefieldname:function (value,item) {
                if(null == value || '' === value){
                    return '字段名不能为空';
                }
                if(/[^a-z_]{1,}$/.test(value)){
                    return '字段名只能以小写英文开头且只能包含小写英文字母下划线';
                }
                if(/(^\_)|(\__)|(\_+$)/.test(value)){
                    return '字段名首尾不能出现下划线\'_\'';
                }
                if(updateData.name != value){
                    if(fieldnames.indexOf(value)>=0){
                        return '已存在该字段名';
                    }
                }
            },
            tablename:function (value,item) {
                if(null == value || '' === value){
                    return '表名不能为空';
                }
                if(/[^a-z_]{1,}$/.test(value)){
                    return '表名只能以小写英文开头且只能包含小写英文字母下划线';
                }
                if(/(^\_)|(\__)|(\_+$)/.test(value)){
                    return '字段名首尾不能出现下划线\'_\'';
                }
                if(value.indexOf('sys')>=0){
                    return '表名中不能有sys字符';
                }
            }
        });

        form.on("select(typeFilter)",function (data) {
            ft = data.value;
            $("select[name='dofor']").find("option").each(function(){
               $(this).attr("disabled","");
            });
            //清空作用的值
            $("select[name='dofor']").val("");
            //情空作用下面的所有附加的选项
            $("select[name='dofor']").nextAll().remove();
            //先移除length字段的不可用css
            $("input[name='length']").removeAttr("disabled").removeClass("layui-disabled");
            //给长度字段赋值
            $("input[name='length']").val(data.elem.selectedOptions[0].getAttribute("data-default"));
            //如果是时间字段
            if(data.value === "DATE" || data.value === "TIME" || data.value === "DATETIME" || data.value === "TIMESTAMP"){
                $("input[name='length']").attr("disabled","").addClass("layui-disabled");
                $("select[name='dofor']").find("option[value='timer']").removeAttr("disabled");
            }
            //如果是文本字段
            if(data.value === "TEXT"){
                $("input[name='length']").attr("disabled","").addClass("layui-disabled");
                $("select[name='dofor']").find("option[value='editor'],[value='textarea']").removeAttr("disabled");
            }
            //如果是字符串字段
            if(data.value === "VARCHAR" || data.value === "CHAR"){
                $("select[name='dofor']").find("option:not([value=''],[value='timer'],[value='editor'])").removeAttr("disabled");
            }
            //如果是布尔类型的字段
            if(data.value === "BIT"){
                $("select[name='dofor']").find("option[value='switch']").removeAttr("disabled");
            }
            //如果是整形
            if(data.value === "INT" || data.value === "BIGINT" || data.value === "FLOAT" || data.value === "DOUBLE" || data.value === "DECIMAL"){
                $("select[name='dofor']").find("option[value='0'],[value='input'],[value='select'],[value='radio'],[value='switch']").removeAttr("disabled");
            }
            form.render("select");
        });

        form.on("select(doforSelect)",function (data) {
            $(this).parent().parent().nextAll().remove();
            $("input[name='listIsShow']").removeAttr("disabled");
            $("input[name='listIsSearch']").removeAttr("disabled");

            $("select[name='type']").val(ft);
            $("input[name='length']").val($("select[name='type']").find("option:selected").data("default"));

            if(data.value === "select" || data.value === "radio" || data.value === "checkbox" ){
                $("#doforSelect").append('<a id="addnewkeyvalue"   class="layui-btn layui-btn-normal" style="margin-top: 5px">新增一个KEYVALUE</a>');
                addbutton();
            }

            if(data.value === "switch"){
                $("#doforSelect").append('<input type="checkbox" checked="" value="default" name="defaultValue" id="doswitch" style="width: 100px" lay-filter="doswitch" lay-skin="switch" lay-text="默认|自定">');
                $("select[name='type']").val("BIT");
                $("input[name='length']").val("1");
            }
            if(data.value === '0'){
                $("input[name='listIsShow']").removeAttr("checked").attr("disabled","");
                $("input[name='listIsSearch']").removeAttr("checked").attr("disabled","");
            }
            if(data.value === "uploadImg" || data.value === "uploadFile"){
                $("input[name='listIsSearch']").removeAttr("checked").attr("disabled","");
            }
            form.render();
        });

        form.on('switch(doswitch)', function(data){
            if(data.elem.checked){
                $("#addnewkeyvalue").remove();
                $("#doforSelect").find(".onekeyvalue").remove();
                $("select[name='type']").val("BIT");
                $("input[name='length']").val("1");
            }else{
                $("select[name='type']").val(ft);
                $("input[name='length']").val($("select[name='type']").find("option:selected").data("default"));
                $("#doforSelect").append('<a id="addnewkeyvalue"   class="layui-btn layui-btn-normal" style="margin-top: 5px">新增一个KEY-VALUE</a>');
                addbutton();
            }
            form.render("select");
        });

        var addbutton = function () {
            $('#doforSelect').undelegate('a#addnewkeyvalue','click');
            $('#doforSelect').undelegate('.layui-btn-danger','click');
            $('#doforSelect').delegate('a#addnewkeyvalue','click', function() {
                $("#doforSelect").append('<div class="layui-form-item onekeyvalue" style="margin-top: 5px;">\n' +
                        '                    <div class="layui-input-inline" style="width: 150px;">\n' +
                        '                        <input type="text"   placeholder="KEY" lay-verify="required" lay-verType="tips" autocomplete="off" class="layui-input key">\n' +
                        '                    </div>\n' +
                        '                    <div class="layui-form-mid">-</div>\n' +
                        '                    <div class="layui-input-inline" style="width: 150px;">\n' +
                        '                        <input type="text"   placeholder="VALUE" lay-verify="required" lay-verType="tips" autocomplete="off" class="layui-input value">\n' +
                        '                    </div>\n' +
                        '                    <div class="layui-input-inline" style="width: 100px;">\n' +
                        '                        <a class="layui-btn layui-btn-danger">删除</a>\n' +
                        '                    </div>\n' +
                        '                </div>');
                form.render();
            });

            $('#doforSelect').delegate('.layui-btn-danger','click', function() {
                $(this).parent().parent().remove();
                form.render();
            });
        };
        //字段新增
        form.on("submit(fieldFilter)",function (data) {
            var flag = true;
            //处理开关数据
            if(undefined === data.field.isNullValue || null == data.field.isNullValue){
                data.field.isNullValue = 'NO';
            }else{
                data.field.isNullValue = 'YES';
            }
            if(undefined === data.field.listIsShow || null == data.field.listIsShow){
                data.field.listIsShow = false
            }else{
                data.field.listIsShow = true
            }

            if(undefined === data.field.listIsSearch || null == data.field.listIsSearch){
                data.field.listIsSearch = false
            }else{
                data.field.listIsSearch = true
            }
            //处理多选项数据
            if(data.field.dofor === "select" || data.field.dofor === "radio" || data.field.dofor === "checkbox" || data.field.dofor === "switch" ){
                var dictlist = [],    //字典集合
                    templabel = [];   //临时存放已经写好的key;
                $("#doforSelect").find(".onekeyvalue").each(function (index) {
                    var dict = {},
                    label = $(this).find(".key").val(),
                    value = $(this).find(".value").val();
                    if(templabel.indexOf(label)>=0){
                        layer.msg("有相同名称的label值");
                        flag =false;
                    }else {
                        dict.type = data.field.name;
                        dict.label = label;
                        dict.value = value;
                        dict.sort = index;
                        templabel.push(label);
                        dictlist.push(dict);
                    }
                });
                if(flag && data.field.dofor !== "switch"){
                    if(dictlist.length === 0){
                        layer.msg("选项的预置KEY-VALUE不能为空");
                        flag = false;
                    }
                    data.field.defaultValue = false;
                }
                if(flag && data.field.dofor === "switch"){
                    if(undefined === data.field.defaultValue || false === data.field.defaultValue || null == data.field.defaultValue){
                        if (dictlist.length !== 2) {
                            layer.msg("自定义开关选项的预置KEY-VALUE只能为两项");
                            flag = false;
                        }else{
                            if(dictlist[0].value === dictlist[1].value){
                                layer.msg("自定义开关选项的预置KEY-VALUE值不能相同");
                                flag = false;
                            }else{
                                data.field.defaultValue = false;
                            }
                        }
                    }else{
                        data.field.defaultValue = true;
                    }
                }
            }
            if(flag){
                data.field.dictlist = dictlist;
                //清空table区域表格并重新填充
                $("#mybtnlen").html("").html('<table class="layui-hide" id="demo" lay-filter="demo"></table>');
                //存储字段名以供判断是否存在
                fieldnames.push(data.field.name);
                //填充表格数据
                tableData.push(data.field);
                table.render({
                    elem: '#demo',
                    cols: [[
                        {field: 'name', title: '字段名称', align: 'center'},
                        {field: 'type', title: '字段类型', align: 'center'},
                        {field: 'length', title: '字段长度', align: 'center'},
                        {field: 'comment', title: '字段注释', align: 'center'},
                        {field: 'listIsShow', title: '是否table显示',templet:'#isShow', align: 'center'},
                        {field: 'listIsSearch', title: '是否list搜索',templet:'#isSearch', align: 'center'},
                        {field: 'dofor', title: '用途',templet:'#mShow', align: 'center'},
                        {field: 'isNullValue', title: '是否可以为空',templet:'#isNullValue', align: 'center'},
                        {field: 'id',title: '操作',width: '15%', align: 'center',toolbar: '#barDemo'}
                    ]],
                    limit:100,
                    data: tableData
                });
                //关闭新建表单窗口
                layer.close(fieldFormShow);
            }
           return false;
        });

        //字段修改
        form.on("submit(editfield)",function (data) {
            var flag = true;
            //处理开关数据
            if(undefined === data.field.isNullValue || null == data.field.isNullValue){
                data.field.isNullValue = 'NO';
            }else{
                data.field.isNullValue = 'YES';
            }
            if(undefined === data.field.listIsShow || null == data.field.listIsShow){
                data.field.listIsShow = false
            }else{
                data.field.listIsShow = true
            }

            if(undefined === data.field.listIsSearch || null == data.field.listIsSearch){
                data.field.listIsSearch = false
            }else{
                data.field.listIsSearch = true
            }
            //处理多选项数据
            if(data.field.dofor === "select" || data.field.dofor === "radio" || data.field.dofor === "checkbox" || data.field.dofor === "switch" ){
                var dictlist = [],    //字典集合
                        templabel = [];   //临时存放已经写好的key;
                $("#doforSelect").find(".onekeyvalue").each(function (index) {
                    var dict = {},
                            label = $(this).find(".key").val(),
                            value = $(this).find(".value").val();
                    if(templabel.indexOf(label)>=0){
                        layer.msg("有相同名称的label值");
                        flag =false;
                    }else {
                        dict.type = data.field.name;
                        dict.label = label;
                        dict.value = value;
                        dict.sort = index;
                        templabel.push(label);
                        dictlist.push(dict);
                    }
                });
                if(flag && data.field.dofor !== "switch"){
                    if(dictlist.length === 0){
                        layer.msg("选项的预置KEY-VALUE不能为空");
                        flag = false;
                    }
                    data.field.defaultValue = false;
                }
                if(flag && data.field.dofor === "switch"){
                    if(undefined === data.field.defaultValue || false === data.field.defaultValue || null == data.field.defaultValue){
                        if (dictlist.length !== 2) {
                            layer.msg("自定义开关选项的预置KEY-VALUE只能为两项");
                            flag = false;
                        }else{
                            if(dictlist[0].value === dictlist[1].value){
                                layer.msg("自定义开关选项的预置KEY-VALUE值不能相同");
                                flag = false;
                            }else{
                                data.field.defaultValue = false;
                            }
                        }
                    }else{
                        data.field.defaultValue = true;
                    }
                }
            }
            if(flag){
                data.field.dictlist = dictlist;
                //清空table区域表格并重新填充
                $("#mybtnlen").html("").html('<table class="layui-hide" id="demo" lay-filter="demo"></table>');
                //如果修改后的字段名跟原先的字段名不一致
                if(updateData.name !== data.field.name ){
                    //1.替换存储在fieldnames里的字段名
                    fieldnames[fieldnames.indexOf(updateData.name)] = data.field.name;
                }
                tableData[updateId] = data.field;
                table.render({
                    elem: '#demo',
                    cols: [[
                        {field: 'name', title: '字段名称', align: 'center'},
                        {field: 'type', title: '字段类型', align: 'center'},
                        {field: 'length', title: '字段长度', align: 'center'},
                        {field: 'comment', title: '字段注释', align: 'center'},
                        {field: 'listIsShow', title: '是否table显示',templet:'#isShow', align: 'center'},
                        {field: 'listIsSearch', title: '是否list搜索',templet:'#isSearch', align: 'center'},
                        {field: 'dofor', title: '用途',templet:'#mShow', align: 'center'},
                        {field: 'isNullValue', title: '是否可以为空',templet:'#isNullValue', align: 'center'},
                        {field: 'id',title: '操作',width: '15%', align: 'center',toolbar: '#barDemo'}
                    ]],
                    limit:100,
                    data: tableData
                });
                //关闭新建表单窗口
                layer.close(fieldFormShow);
            }
            return false;
        });

        var updateId,updateData;
        //监听工具条
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                layer.confirm('真的删除行么', function(index){
                    obj.del();
                    //删除表格数据
                    tableData.splice(obj.tr[0].rowIndex,1);
                    //删除名字集合数据
                    fieldnames.splice(fieldnames.indexOf(data.name),1);
                    layer.close(index);
                });
            } else if(obj.event === 'edit'){
                // layer.alert('编辑行：<br>'+ JSON.stringify(data));
                $("select[name='dofor']").nextAll().remove();
                $("#myform")[0].reset();
                updateId = obj.tr[0].rowIndex;
                updateData = data;
                $($("#myform")[0]).find('input,select').each(function (index) {
                    var name = $(this).attr('name');
                    if ("" === name || undefined === name) return;
                    if(name === "name" || name === 'comment'){
                        $(this).val(data[name]);
                        return;
                    }
                    if(name === 'type'){
                        $("select[name='dofor']").find("option").each(function () {
                            $(this).attr("disabled","");
                        });
                        var v = data['type'];
                        ft = v;
                        $(this).val(v);
                        //如果是时间字段
                        if(v === "DATE" || v === "TIME" || v === "DATETIME" || v === "TIMESTAMP"){
                            $("input[name='length']").attr("disabled","").addClass("layui-disabled");
                            $("select[name='dofor']").find("option[value='timer']").removeAttr("disabled");
                        }
                        //如果是文本字段
                        if(v === "TEXT"){
                            $("input[name='length']").attr("disabled","").addClass("layui-disabled");
                            $("select[name='dofor']").find("option[value='editor'],[value='textarea']").removeAttr("disabled");
                        }
                        //如果是字符串字段
                        if(v === "VARCHAR" || v === "CHAR"){
                            $("select[name='dofor']").find("option:not([value=''],[value='timer'],[value='editor'])").removeAttr("disabled");
                        }
                        //如果是布尔类型的字段
                        if(v === "BIT"){
                            $("select[name='dofor']").find("option[value='switch']").removeAttr("disabled");
                        }
                        //如果是整形
                        if(v === "INT" || v === "BIGINT" || v === "FLOAT" || v === "DOUBLE" || v === "DECIMAL"){
                            $("select[name='dofor']").find("option[value='0'],[value='input'],[value='select'],[value='radio'],[value='switch']").removeAttr("disabled");
                        }
                        return;
                    }
                    if(name === "length"){
                        $(this).val(data[name]);
                        if(data['type'] === "DATE" || data['type'] === "TIME" || data['type'] === "DATETIME" || data['type'] === "TIMESTAMP" || data['type'] === "TEXT"){
                            $(this).attr("disabled","").addClass("layui-disabled");
                        }else{
                            $(this).removeAttr("disabled").removeClass("layui-disabled");
                        }
                        return;
                    }
                    if(name === "listIsShow" || name === "listIsSearch"){
                        if(data[name] === false){
                            $(this).removeAttr("checked");
                        }else{
                            $(this).attr("checked","");
                        }
                        return;
                    }
                    if(name === 'dofor'){
                        $(this).val(data['dofor']);
                        if(data['dofor'] === "select" || data['dofor'] === "radio" || data['dofor'] === "checkbox" || data['dofor'] === "switch"){
                            var dictlist = data.dictlist;
                            var mdefault = data.defaultValue;
                            if(data['dofor'] === "switch"){
                                if(mdefault !== undefined  && mdefault != null && mdefault === true){
                                    $("#doforSelect").append('<input type="checkbox" checked="" value="default" name="defaultValue" id="doswitch" style="width: 100px" lay-filter="doswitch" lay-skin="switch" lay-text="默认|自定">');
                                }else{
                                    $("#doforSelect").append('<input type="checkbox"  value="default" name="defaultValue" id="doswitch" style="width: 100px" lay-filter="doswitch" lay-skin="switch" lay-text="默认|自定">');
                                }
                            }
                            if(mdefault === undefined || mdefault === false || mdefault == null){
                                $("#doforSelect").append('<a id="addnewkeyvalue"   class="layui-btn layui-btn-normal" style="margin-top: 5px">新增一个KEYVALUE</a>');
                            }
                            if(dictlist !== undefined && dictlist != null && dictlist.length>0){
                                for(var i = 0;i<dictlist.length;i++){
                                    $("#doforSelect").append('<div class="layui-form-item onekeyvalue" style="margin-top: 5px;">\n' +
                                            '                    <div class="layui-input-inline" style="width: 150px;">\n' +
                                            '                        <input type="text" value="'+dictlist[i].label+'"   placeholder="KEY" lay-verify="required" lay-verType="tips" autocomplete="off" class="layui-input key">\n' +
                                            '                    </div>\n' +
                                            '                    <div class="layui-form-mid">-</div>\n' +
                                            '                    <div class="layui-input-inline" style="width: 150px;">\n' +
                                            '                        <input type="text" value="'+dictlist[i].value+'"   placeholder="VALUE" lay-verify="required" lay-verType="tips" autocomplete="off" class="layui-input value">\n' +
                                            '                    </div>\n' +
                                            '                    <div class="layui-input-inline" style="width: 100px;">\n' +
                                            '                        <a class="layui-btn layui-btn-danger">删除</a>\n' +
                                            '                    </div>\n' +
                                            '                </div>');
                                }
                            }
                        }
                        if(data['dofor'] === '0'){
                            $("input[name='listIsShow']").removeAttr("checked").attr("disabled","");
                            $("input[name='listIsSearch']").removeAttr("checked").attr("disabled","");
                        }else{
                            $("input[name='listIsShow']").removeAttr("disabled");
                            $("input[name='listIsSearch']").removeAttr("disabled");
                        }
                        return;
                    }
                   if(name === "isNullValue" ){
                       if(data['isNullValue'] === 'NO'){
                           $(this).removeAttr("checked");
                       }else{
                           $(this).attr("checked","");
                       }

                   }
                });
                $("#myname").attr("lay-verify","updatefieldname");
                $("#fieldsubmit").attr("lay-filter","editfield");
                form.render();
                fieldFormShow =layer.open({
                    type: 1,
                    title: '修改一个字段',
                    area:['700px', '600px'],
                    content: $('#fieldForm')
                });
            }
        });

        form.on("submit(addUser)",function(data){
            if(tableData.length === 0){
                layer.alert("字段不能为空");
                return false;
            }
            //验证基本字段
            var baseField = ['id','create_by','create_date','update_by','update_date','del_flag']
            ,treeField = ['id','parent_id','level','parent_ids','sort','create_by','create_date','update_by','update_date','del_flag'],
                    ssmsg = [];
            if(data.field.tabletype === '1'){
                for(var j=0;j<fieldnames.length;j++){
                    if(baseField.indexOf(fieldnames[j])>=0){
                        ssmsg.push(fieldnames[j]);
                    }
                }
            }
            if(data.field.tabletype === '2'){
                for(var j=0;j<fieldnames.length;j++){
                    if(treeField.indexOf(fieldnames[j])>=0){
                        ssmsg.push(fieldnames[j]);
                    }
                }
            }
            if(data.field.tabletype === '3'){
                if(tableData.length !== 2){
                    layer.alert("辅助表只能有两个字段");
                    return false;
                }
                if(treeField.indexOf("id")){
                    ssmsg.push("id");
                }
            }
            if(ssmsg != null && ssmsg.length>0){
                layer.alert(ssmsg.join(','));
                return false;
            }
            //处理多项选择的字段type
            for(var i=0,len=tableData.length;i<len;i++){
                var type = tableData[i].dofor,dictlist,mydefault = tableData[i].defaultValue;
                if(type === 'select'|| type === 'radio'|| type === 'checkbox'|| type === 'switch'){
                    if(!mydefault){
                        dictlist = tableData[i].dictlist;
                        if(dictlist.length === 0){
                            layer.alert('多选项的预置KEYVALUE不能为空');
                            return false;
                        }
                        for(var j = 0;j<dictlist.length;j++){
                            //设置类型
                            dictlist[j].type = data.field.name+"_"+tableData[i].name;
                        }
                    }
                }
            }
            data.field.fieldList = tableData;
            var loadIndex = layer.load(2, {
                shade: [0.3, '#333']
            });
            $.ajax({
                type:"POST",
                url:"${base}/admin/system/table/add",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(data.field),
                success:function(res){
                    layer.close(loadIndex);
                    if(res.success){
                        parent.layer.msg("数据表新增成功!",{time:1500},function(){
                            //刷新父页面
                            parent.location.reload();
                        });
                    }else{
                        layer.msg(res.message);
                    }
                }
            });
            return false;
        });

    });
</script>
</body>
</html>