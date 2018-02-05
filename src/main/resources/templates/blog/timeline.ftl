<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "${base}/blog/common/header.ftl">
    <!-- animate.css -->
    <link href="${base}/static/blog/css/animate.min.css" rel="stylesheet" />
    <!-- 本页样式表 -->
    <link href="${base}/static/blog/css/timeline.css" rel="stylesheet" />
</head>
<body>
<!-- 导航 -->
<#include "${base}/blog/common/nav.ftl">
<!-- 主体（一般只改变这里的内容） -->
<div class="blog-body">
    <div class="blog-container">
        <blockquote class="layui-elem-quote sitemap layui-breadcrumb shadow">
            <a href="home.html" title="网站首页">网站首页</a>
            <@articleChannelList cid="${channel.id}">
                <#list result as item>
                    <a href="${base}/showBlog${item.href}"><cite>${item.name}</cite></a>
                </#list>
            </@articleChannelList>
        </blockquote>
        <div class="blog-main">
            <div class="child-nav shadow">
                <span class="child-nav-btn child-nav-btn-this">时光轴</span>
                <span class="child-nav-btn">笔记墙</span>
            </div>
            <div class="timeline-box shadow">
                <div class="timeline-main">
                    <h1><i class="fa fa-clock-o"></i>时光轴<span> —— 记录生活点点滴滴</span></h1>
                    <div class="timeline-line"></div>
                    <div class="timeline-year">
                        <h2><a class="yearToggle" href="javascript:">2017年</a><i class="fa fa-caret-down fa-fw"></i></h2>
                        <div class="timeline-month">
                            <h3 class=" animated fadeInLeft"><a class="monthToggle" href="javascript:">2月</a><i class="fa fa-caret-down fa-fw"></i></h3>
                            <ul>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">02月23日 19:33</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">该时光轴支持手机平板PC，但并不能兼容一些老的浏览器！</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">02月11日 20:29</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2017年2月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">02月10日 20:35</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2017年2月发表的</div>
                                    <div class="clear"></div>
                                </li>
                            </ul>
                        </div>
                        <div class="timeline-month">
                            <h3 class=" animated fadeInLeft"><a class="monthToggle" href="javascript:">1月</a><i class="fa fa-caret-down fa-fw"></i></h3>
                            <ul>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">01月23日 19:33</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2017年1月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">01月11日 20:29</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2017年1月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">01月10日 20:35</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2017年1月发表的</div>
                                    <div class="clear"></div>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="timeline-year">
                        <h2><a class="yearToggle" href="javascript:">2016年</a><i class="fa fa-caret-down fa-fw"></i></h2>
                        <div class="timeline-month">
                            <h3 class=" animated fadeInLeft"><a class="monthToggle" href="javascript:">2月</a><i class="fa fa-caret-down fa-fw"></i></h3>
                            <ul>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">02月23日 19:33</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2016年2月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">02月11日 20:29</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2016年2月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">02月10日 20:35</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2016年2月发表的</div>
                                    <div class="clear"></div>
                                </li>
                            </ul>
                        </div>
                        <div class="timeline-month">
                            <h3 class=" animated fadeInLeft"><a class="monthToggle" href="javascript:">1月</a><i class="fa fa-caret-down fa-fw"></i></h3>
                            <ul>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">01月23日 19:33</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2016年1月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">01月11日 20:29</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2016年1月发表的</div>
                                    <div class="clear"></div>
                                </li>
                                <li class=" ">
                                    <div class="h4  animated fadeInLeft">
                                        <p class="date">01月10日 20:35</p>
                                    </div>
                                    <p class="dot-circle animated "><i class="fa fa-dot-circle-o"></i></p>
                                    <div class="content animated fadeInRight">这是2016年1月发表的</div>
                                    <div class="clear"></div>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <h1 style="padding-top:4px;padding-bottom:2px;margin-top:40px;"><i class="fa fa-hourglass-end"></i>THE END</h1>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 底部 -->
<#include "${base}/blog/common/foot.ftl">
<!-- 本页脚本 -->
<script type="text/javascript">
    layui.use('jquery', function () {
        var $ = layui.jquery;
        $(function () {
            $('.monthToggle').click(function () {
                $(this).parent('h3').siblings('ul').slideToggle('slow');
                $(this).siblings('i').toggleClass('fa-caret-down fa-caret-right');
            });
            $('.yearToggle').click(function () {
                $(this).parent('h2').siblings('.timeline-month').slideToggle('slow');
                $(this).siblings('i').toggleClass('fa-caret-down fa-caret-right');
            });
        });
    });
</script>
</body>
</html>