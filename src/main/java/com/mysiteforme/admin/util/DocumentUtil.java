package com.mysiteforme.admin.util;

import com.mysiteforme.admin.entity.BlogArticle;
import org.apache.lucene.document.*;

public class DocumentUtil {
    public static Document writeDoc(BlogArticle blogArticle) {
        Document doc = new Document();
        doc.add(new LongPoint("id",blogArticle.getId()));
        doc.add(new TextField("title",blogArticle.getTitle(), Field.Store.YES));
        doc.add(new TextField("marks",blogArticle.getMarks()==null?"":blogArticle.getMarks(),Field.Store.YES));
        doc.add(new TextField("text",blogArticle.getText()==null?"":blogArticle.getText(),Field.Store.YES));
        doc.add(new StoredField("href",blogArticle.getBlogChannel().getHref()));
        doc.add(new StoredField("show_pic",blogArticle.getShowPic()==null?"":blogArticle.getShowPic()));
        return doc;
    }
}
