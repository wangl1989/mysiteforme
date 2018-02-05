<!-- 导航 -->
    <nav class="blog-nav layui-header">
        <div class="blog-container">
            <!-- QQ互联登陆 -->
            <a href="javascript:" class="blog-user">
                <i class="fa fa-qq"></i>
            </a>
            <a href="javascript:" class="blog-user layui-hide">
                <img src="${base}/static/blog/images/Absolutely.jpg" alt="Absolutely" title="Absolutely" />
            </a>
            <!-- 不落阁 -->
            <a class="blog-logo" href="${base}/showBlog/index">${site.name}</a>
            <!-- 导航菜单 -->
            <ul class="layui-nav" lay-filter="nav">
                <@mychannel limit="5">
                    <#list result as item>
                        <li class="layui-nav-item <#if (channel.href?contains(item.href))> layui-this</#if>" >
                            <a href="${base}/showBlog${item.href}"><i class="layui-icon" style="font-size: 18px;">${item.logo}</i>&nbsp;${item.name}</a>
                        </li>
                    </#list>
                </@mychannel>
            </ul>
            <!-- 手机和平板的导航开关 -->
            <a class="blog-navicon" href="javascript:">
                <i class="fa fa-navicon"></i>
            </a>
        </div>
    </nav>