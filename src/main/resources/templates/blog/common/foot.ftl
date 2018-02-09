<!-- 底部 -->
    <footer class="blog-footer">
        <p><span>Copyright</span><span>&copy;</span><span>2018</span><a href="${site.url}">${site.name}</a><span>Design By ${site.author}</span></p>
        <p><a href="http://www.miibeian.gov.cn/" target="_blank">${site.record}</a></p>
    </footer>
    <!--侧边导航-->
    <ul class="layui-nav layui-nav-tree layui-nav-side blog-nav-left layui-hide" lay-filter="nav">
        <@mychannel limit="5">
            <#list result as item>
                <li class="layui-nav-item <#if (channel.href?contains(item.href))> layui-this</#if>">
                    <a href="${base}/showBlog${item.href}"><i class="layui-icon" style="font-size: 18px;">${item.logo}</i>&nbsp;${item.name}</a>
                </li>
            </#list>
        </@mychannel>
    </ul>
    <!--分享窗体-->
    <div class="blog-share layui-hide">
        <div class="blog-share-body">
            <div style="width: 200px;height:100%;">
                <div class="bdsharebuttonbox">
                    <a class="bds_qzone" data-cmd="qzone" title="分享到QQ空间"></a>
                    <a class="bds_tsina" data-cmd="tsina" title="分享到新浪微博"></a>
                    <a class="bds_weixin" data-cmd="weixin" title="分享到微信"></a>
                    <a class="bds_sqq" data-cmd="sqq" title="分享到QQ好友"></a>
                </div>
            </div>
        </div>
    </div>
    <!--遮罩-->
    <div class="blog-mask animated layui-hide"></div>
    <!-- layui.js -->
    <script src="${base}/static/layui/layui.js"></script>
    <!-- 全局脚本 -->
    <script src="${base}/static/blog/js/global.js?t=${.now?long}"></script>