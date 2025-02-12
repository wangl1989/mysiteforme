package com.mysiteforme.admin.config;

import com.mysiteforme.admin.freemark.*;
import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Freemarker模板引擎配置类
 * 配置自定义标签和模型
 * @author wangl
 * @since 2017/11/26
 */
@Component
public class FreemarkerConfig {

    private Configuration configuration;

    private SystemDirective systemDirective;

    private ArticleDirective articleDirective;

    private IndexArticleDirective indexArticleDirective;

    private ChannelDirective channelDirective;

    private ParentChannelListDirective parentChannelListDirective;

    private ArticleClickTempletModel articleClickTempletModel;

    private SysUserTempletModel sysUserTempletModel;

    private TagsTempletModel tagsTempletModel;

    private NewCommentArticleTempletModel newCommentArticleTempletModel;

    private LookLikeArticlesTempletModel lookLikeArticlesTempletModel;


    private CommentNumberTempletModel commentNumberTempletModel;

    public FreemarkerConfig() {}

    @Autowired
    public FreemarkerConfig(Configuration configuration,
                            SystemDirective systemDirective,
                            ArticleDirective articleDirective,
                            IndexArticleDirective indexArticleDirective,
                            ChannelDirective channelDirective,
                            ParentChannelListDirective parentChannelListDirective,
                            ArticleClickTempletModel articleClickTempletModel,
                            SysUserTempletModel sysUserTempletModel,
                            TagsTempletModel tagsTempletModel,
                            NewCommentArticleTempletModel newCommentArticleTempletModel,
                            LookLikeArticlesTempletModel lookLikeArticlesTempletModel,
                            CommentNumberTempletModel commentNumberTempletModel) {
        this.configuration = configuration;
        this.systemDirective = systemDirective;
        this.articleDirective = articleDirective;
        this.indexArticleDirective = indexArticleDirective;
        this.channelDirective = channelDirective;
        this.parentChannelListDirective = parentChannelListDirective;
        this.articleClickTempletModel = articleClickTempletModel;
        this.sysUserTempletModel = sysUserTempletModel;
        this.tagsTempletModel = tagsTempletModel;
        this.newCommentArticleTempletModel = newCommentArticleTempletModel;
        this.lookLikeArticlesTempletModel = lookLikeArticlesTempletModel;
        this.commentNumberTempletModel = commentNumberTempletModel;
    }

    /**
     * 初始化Freemarker配置
     */
    @PostConstruct
    public void setSharedVariable() {
        //系统字典标签
        configuration.setSharedVariable("my",systemDirective);
        //博客文章标签
        configuration.setSharedVariable("ar",articleDirective);
        //博客首页文章列表标签
        configuration.setSharedVariable("myindex",indexArticleDirective);
        //博客栏目标签
        configuration.setSharedVariable("mychannel",channelDirective);
        //博客当前栏目所有父目录集合标签
        configuration.setSharedVariable("articleChannelList",parentChannelListDirective);

        //获取文章点击量标签
        configuration.setSharedVariable("clickNumber",articleClickTempletModel);
        //获取文章评论数量
        configuration.setSharedVariable("commentNumber",commentNumberTempletModel);
        //获取系统用户信息
        configuration.setSharedVariable("sysuser",sysUserTempletModel);
        //获取标签集合
        configuration.setSharedVariable("tags",tagsTempletModel);
        //最新评论文章集合
        configuration.setSharedVariable("nca",newCommentArticleTempletModel);
        //当前文章相似的文章
        configuration.setSharedVariable("same",lookLikeArticlesTempletModel);
    }

}
