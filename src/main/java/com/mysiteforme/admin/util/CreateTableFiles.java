package com.mysiteforme.admin.util;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.mysiteforme.admin.base.DataEntity;
import com.mysiteforme.admin.base.TreeEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CreateTableFiles {
    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.url}")
    private String dataBaseUrl;
    @Value("${source-code-dic}")
    public  String baseDic;
    @Value("${source-code-zipfile}")
    public  String zipFile;

    private static final String author = "wangl";

    private static final String[] superEntityColumnsTypeOne = new String[] { "id","create_date","create_by","update_date","update_by","remarks","del_flag" };
    private static final String[] superEntityColumnsTypeTwo = new String[] { "id","parent_id","level","parent_ids","sort","create_date","create_by","update_date","update_by","remarks","del_flag" };

    public void createFile(String[] tableNames,int type) {
        createFile(tableNames,type,driverName,userName,password,dataBaseUrl,baseDic,zipFile,author);
    }

    public void createFile(String[] tableNames,int type,String driverName,String userName,String password,String dataBaseUrl,String baseDic,String zipFile,String author) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create(driverName, userName, password)
                .globalConfig(builder -> {
                    builder.author(author) // 设置作者
                            .disableOpenDir()   // 执行完成后是否自动打开输出目录
                            .outputDir(baseDic); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.mysiteforme") // 设置父包名
                            .entity("admin") // 设置实体类包名
                            .controller("controller") // 设置 Controller 类包名
                            .mapper("dao") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mappers"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> builder.addInclude(tableNames) // 设置需要生成的表名
                        .entityBuilder()
                        .columnNaming(NamingStrategy.underline_to_camel) // 设置列名下划线转驼峰命名
                        .superClass(type == 1 ? DataEntity.class.getName() : TreeEntity.class.getName()) // 设置实体类父类
                        .addSuperEntityColumns(type == 1 ? superEntityColumnsTypeOne : superEntityColumnsTypeTwo) // 设置实体类公共字段
                        .javaTemplate("/templates/vm/entity.java.vm") // 设置实体类模板文件
                        .enableLombok() // 启用 Lombok
                        .enableTableFieldAnnotation() // 启用字段注解
                        .enableFileOverride() // 允许覆盖已有文件
                        .controllerBuilder()
                        .enableRestStyle()  // 启用 REST 风格
                        .formatFileName("%sController")
                        .template("/templates/vm/controller.java.vm")
                        .enableFileOverride()
                        .serviceBuilder()
                        .superServiceClass(IService.class.getName())
                        .formatServiceFileName("%sService")
                        .superServiceImplClass(ServiceImpl.class.getName())
                        .formatServiceFileName("%sServiceImpl")
                        .serviceTemplate("/templates/vm/service.java.vm")
                        .serviceImplTemplate("/templates/vm/serviceImpl.java.vm")
                        .enableFileOverride()
                        .mapperBuilder()
                        .formatMapperFileName("%sDao")
                        .formatXmlFileName("%sDao")
                        .mapperTemplate("/templates/vm/mapper.java.vm")
                        .enableFileOverride())
                .injectionConfig(builder -> builder.customMap(Collections.singletonMap("abc", author+ "-mp")) // 自定义参数
                        .beforeOutputFile((tableInfo, objectMap) -> {
                            System.out.println("准备生成文件: " + tableInfo.getEntityName());
                            // 可以在这里添加自定义逻辑，如修改 objectMap 中的配置
                        })
                        .customFile(new ArrayList<CustomFile>(){{
                            add(new CustomFile.Builder()
                                    .fileName("list.ftl")
                                    .filePath(baseDic+ "/" + OutputFile.entity + "/")
                                    .templatePath("/templates/vm/list.jsp.vm")
                                    .packageName("front")
                                    .build());
                            add(new CustomFile.Builder()
                                    .fileName("add.ftl")
                                    .filePath(baseDic+ "/" + OutputFile.entity + "/")
                                    .templatePath("/templates/vm/add.jsp.vm")
                                    .packageName("front")
                                    .build());
                            add(new CustomFile.Builder()
                                    .fileName("edit.jsp")
                                    .filePath(baseDic+ "/" + OutputFile.entity + "/")
                                    .templatePath("/templates/vm/edit.jsp.vm")
                                    .packageName("front")
                                    .build());

                        }}))
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成

    }
}
