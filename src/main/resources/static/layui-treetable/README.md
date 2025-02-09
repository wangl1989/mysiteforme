#layui-treeGird

首先介绍一下[layui](https://www.layui.com/)，是一个模块化前端UI框架，遵循原生HTML/CSS/JS的书写与组织形式，门槛极低，拿来即用。

由于最近的项目中引进了layui，使用了树形菜单的内置模块，可是该功能并未完全满足需求。
layui是开源包，为满足需求，故基于layui-tree写了一个treetable.


#使用
1. 页面元素
```
<div id="demo"></div>
```
2. js代码
```
var layout = [
        {name: '菜单名称', treeNodes: true, headerClass: 'value_col', colClass: 'value_col', style: 'width: 60%'}
];
```

```
layui.use(['tree', 'layer', 'form'], function(){
              var layer = layui.layer, $ = layui.jquery;
              var form = layui.form();
          
              layui.treeGird({
                elem: '#demo',   //传入元素选择器
                nodes: [
                          {
                              "id": "1",
                              "name": "父节点1",
                              "children": [
                                  {
                                      "id": "11",
                                      "name": "子节点11"
                                  },
                                  {
                                      "id": "12",
                                      "name": "子节点12"
                                  }
                              ]
                          },
                          {
                              "id": "2",
                              "name": "父节点2",
                              "children": [
                                  {
                                      "id": "21",
                                      "name": "子节点21",
                                      "children": [
                                          {
                                              "id": "211",
                                              "name": "子节点211"
                                          }
                                      ]
                                  }
                              ]
                          }
                ],
                layout:layout
            });
        });
```


3.页面展示

![输入图片说明](https://git.oschina.net/uploads/images/2017/0523/144746_e6c438e1_980808.png "在这里输入图片标题")


#方法
语法：layui.treeGird(options)

options是一个对象参数，可支持的key如下表
![输入图片说明](https://git.oschina.net/uploads/images/2017/0523/150434_c4a6586b_980808.png "在这里输入图片标题")


#节点数据格式nodes
![nodes](https://git.oschina.net/uploads/images/2017/0523/151002_4ea8f20a_980808.png "在这里输入图片标题")


#列表头元素layout
![layout](https://git.oschina.net/uploads/images/2017/0523/151627_46e0ad19_980808.png "在这里输入图片标题")

自定义的render
```
var layout = [
        {name: '菜单名称', treeNodes: true, headerClass: 'value_col', colClass: 'value_col', style: 'width: 60%'},
        {name: '操作', headerClass: 'value_col', colClass: 'value_col', style: 'width: 20%', render: function(row) {
            return '<a class="layui-btn layui-btn-danger layui-btn-mini" onclick="del('+row.id+')"><i class="layui-icon">&#xe640;</i> 删除</a>';   //列渲染
        }},
];
```

效果如下：
![输入图片说明](https://git.oschina.net/uploads/images/2017/0523/151846_9790e8b3_980808.png "在这里输入图片标题")


#总结
灵感来源layui，感谢layui的开源。

