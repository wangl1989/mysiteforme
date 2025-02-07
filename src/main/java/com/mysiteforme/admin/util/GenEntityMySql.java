package com.mysiteforme.admin.util;


public class GenEntityMySql {
	public static void main(String[] args) {
        String driverName = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "123456";
        String dataBaseUrl= "jdbc:mysql://localhost:3306/mysiteforme?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true&serverTimezone=Asia/Shanghai";
        String baseDic = "D://mysite";
        String zipFile = "D://mysite.zip";
        String author = "wangl";


        CreateTableFiles createTableFiles = new CreateTableFiles();
        createTableFiles.createFile(new String[]{"bolg_article"},1,driverName,userName,password,dataBaseUrl,baseDic,zipFile,author);


	}
}
