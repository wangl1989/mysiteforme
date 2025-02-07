package com.mysiteforme.admin.util;

import com.mysiteforme.admin.entity.BlogArticle;
import org.apache.lucene.document.*;

/**
 * Lucene文档工具类
 * 用于创建和管理Lucene索引文档
 * 
 * @author wangl
 */
public class DocumentUtil {

    /**
     * 创建文章索引文档
     * @param article 文章对象
     * @return Lucene文档对象
     */
    public static Document article2Doc(BlogArticle article) {
        Document doc = new Document();
        doc.add(new LongPoint("id",article.getId()));
        doc.add(new TextField("title",article.getTitle(), Field.Store.YES));
        doc.add(new TextField("marks",article.getMarks()==null?"":article.getMarks(),Field.Store.YES));
        doc.add(new TextField("text",article.getText()==null?"":article.getText(),Field.Store.YES));
        doc.add(new StoredField("href",article.getBlogChannel().getHref()));
        doc.add(new StoredField("show_pic",article.getShowPic()==null?"":article.getShowPic()));
        return doc;
    }

    /**
     * 从文档中提取文章信息
     * @param doc Lucene文档对象
     * @return 文章对象
     */
    public static BlogArticle doc2Article(Document doc) {
        BlogArticle article = new BlogArticle();
        article.setId(Long.parseLong(doc.get("id_stored")));
        article.setTitle(doc.get("title"));
        article.setText(doc.get("text"));
        return article;
    }
}
