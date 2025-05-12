/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:59:39
 * @ Description: Lucene搜索类
 */

package com.mysiteforme.admin.lucene;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.util.DocumentUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2018/1/28.
 * todo:
 */
@Component
public class LuceneSearch {

    private static final Logger log = LoggerFactory.getLogger(LuceneSearch.class);
    //Lucene索引文件路径
    public static String dir;

    @Value("${lucence-dic}")
    public void setDir(String lucenceDir) {
        dir = lucenceDir;
    }
    //定义分词器
    static Analyzer analyzer = new IKAnalyzer();

    /**
     * 封裝一个方法，用于将数据库中的数据解析为一个个关键字词存储到索引文件中
     */
    public static void write(Document document) throws IOException {
        //索引库的存储目录
        Directory directory = FSDirectory.open(Paths.get(dir));
        //在 6.6 以上版本中 version 不再是必要的，并且，存在无参构造方法，可以直接使用默认的 StandardAnalyzer 分词器。
        //创建索引写入配置
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //创建索引写入对象
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        //写入到目录文件中
        indexWriter.addDocument(document);
        //提交事务
        indexWriter.commit();
        //关闭流
        indexWriter.close();
    }

    /**
     * 根据ID删除索引
     */
    public static void deleteIndexById(String id) throws IOException{
        Directory directory = FSDirectory.open(Paths.get(dir));// 打开文件索引目录
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //创建索引写入对象
        IndexWriter writer = new IndexWriter(directory,indexWriterConfig);
        IndexReader reader = DirectoryReader.open(directory);// 读取目
        Query q = new TermQuery(new Term("id", id));
        writer.deleteDocuments(q);// 删除指定ID的Document
        writer.commit();// 提交
        writer.close();// 关闭
        reader.close();// 关闭
    }

    /**
     * 根据ID更新搜索内容
     */
    public static void updateIndexById(BlogArticle blogArticle) throws IOException{
        Directory directory = FSDirectory.open(Paths.get(dir));// 打开文件索引目录
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //创建索引写入对象
        IndexWriter writer = new IndexWriter(directory,indexWriterConfig);
        Document doc = DocumentUtil.article2Doc(blogArticle);
        writer.updateDocument(new Term("id", blogArticle.getId().toString()), doc);
        writer.commit();// 提交
        writer.close();// 关闭
    }

    /**
     * 搜索
     * @param field 字段名称
     * @param value 搜索内容
     */
    public Map<String,Object> search(String[] field, String value, IPage<Object> page) throws Exception{
        Map<String,Object> dataMap = Maps.newHashMap();
        //索引库的存储目录
        Directory directory = FSDirectory.open(Paths.get(dir));
        //读取索引库的存储目录
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        //创建一个索引的查找器，来检索索引库
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        //“与或”搜索
        /*
          1.和： MUST与MUST_NOT

         2.或： SHOULD与SHOULD

         3.A与B的并集－B  MUST与MUST_NOT
         */
        BooleanQuery.Builder builder=new BooleanQuery.Builder();
        //lucence查询解析器，用于指定查询的属性名和分词器
        Analyzer analyzer = new IKAnalyzer();
        QueryParser parser = new MultiFieldQueryParser(field,analyzer);
        //使用特定的分析器搜索
        Query query = parser.parse(value);
        builder.add(query,BooleanClause.Occur.SHOULD);
        for (String s : field) {
            //按词条搜索
            Query q = new TermQuery(new Term(s, value));
            builder.add(q, BooleanClause.Occur.SHOULD);
        }
        BooleanQuery finalquery = builder.build();
        //TopDocs 搜索返回的结果
        TopDocs topDocs = indexSearcher.search(finalquery, 100);
        log.info("搜索返回的结果:{}",topDocs.scoreDocs.length);
        //最终被分词后添加的前缀和后缀处理器，默认是粗体<B></B>
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style='color: #FF5722;'>","</span>");
        //高亮搜索的词添加到高亮处理器中
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
        // 搜索返回的结果集合
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //查询起始记录位置
        int begin = (int) (page.getSize() * (page.getCurrent() - 1));
        //查询终止记录位置
        int end = (int)Math.min(begin + page.getSize(), scoreDocs.length);
        List<Map<String ,Object>> list = Lists.newArrayList();
        //进行分页查询
        for(int i=begin;i<end;i++) {
            Map<String ,Object> map = Maps.newHashMap();
            int docID = scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docID);
            Long id = Long.valueOf(doc.get("id"));
            String title = doc.get("title");
            if(StringUtils.isNotBlank(title)){
                TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(title));
                title = highlighter.getBestFragment(tokenStream, title);
            }
            String text = doc.get("text");
            if(StringUtils.isNotBlank(text)){
                TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(text));
                text = highlighter.getBestFragment(tokenStream, text);
            }
            String marks = doc.get("marks");
            if(StringUtils.isNotBlank(marks)){
                TokenStream tokenStream = analyzer.tokenStream("marks", new StringReader(marks));
                marks = highlighter.getBestFragment(tokenStream, marks);
            }
            String href=doc.get("href");
            String showPic=doc.get("showPic");
            //评分
            Explanation explanation = indexSearcher.explain(query, docID);
            map.put("id",id);
            map.put("href",href);
            map.put("showPic",showPic);
            map.put("title",title);
            map.put("text",text);
            map.put("marks",marks);
            DecimalFormat   fnum  =   new DecimalFormat("##0.00");
            map.put("percent",fnum.format(explanation.getValue()));
            list.add(map);
            dataMap.put("data",list);
            dataMap.put("count",list.size());
        }
        directoryReader.close();
        directory.close();
        TimeInterval timer = DateUtil.timer();
        dataMap.put("time", DateUtil.formatBetween(timer.intervalRestart()));
        dataMap.put("key",value);
        return dataMap;
    }

}
