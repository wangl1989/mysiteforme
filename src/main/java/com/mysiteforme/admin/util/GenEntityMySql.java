package com.mysiteforme.admin.util;


import com.mysiteforme.admin.entity.Site;

public class GenEntityMySql {
	public static void main(String[] args) {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String userName = "root";
        String password = "123456";
        String dataBaseUrl= "jdbc:mysql://localhost:3306/mysiteforme?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true&serverTimezone=Asia/Shanghai";
        String baseDic = "D://mysite";
        String zipFile = "D://mysite.zip";
        String author = "wangl";


        CreateTableFiles createTableFiles = new CreateTableFiles();
        Site site = new Site();
        site.setName("默认站点");
        site.setDescription("这是一个默认站点");
        site.setAuthor("wang");
        createTableFiles.createFile(new String[]{"blog_article"},1,driverName,userName,password,dataBaseUrl,baseDic,zipFile,author, site);


	}
}
