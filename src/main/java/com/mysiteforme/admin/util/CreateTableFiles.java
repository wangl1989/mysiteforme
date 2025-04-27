/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:33:52
 * @ Description: 数据库表生成文件工具类 用于根据数据库表结构自动生成相关的实体类、Mapper、Service等文件
 */

package com.mysiteforme.admin.util;

import java.util.*;

import com.google.common.collect.Lists;
import com.mysiteforme.admin.entity.response.TableConfigResponse;
import com.mysiteforme.admin.entity.response.TableFieldConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.mysiteforme.admin.base.DataEntity;
import com.mysiteforme.admin.base.TreeEntity;
import com.mysiteforme.admin.entity.Site;

@Component
public class CreateTableFiles {
    @Value("${spring.datasource.druid.driver-class-name}")
    private String driverName;
    @Value("${spring.datasource.druid.username}")
    private String userName;
    @Value("${spring.datasource.druid.password}")
    private String password;
    @Value("${spring.datasource.druid.url}")
    private String dataBaseUrl;
    @Value("${source-code-dic}")
    public  String baseDic;
    @Value("${source-code-zipfile}")
    public  String zipFile;

    private static final String AUTHOR_NAME_STRING = "wangl";


    private static final String[] superEntityColumnsTypeOne = new String[] { "id","create_date","create_by","update_date","update_by","remarks","del_flag" };
    private static final String[] superEntityColumnsTypeTwo = new String[] { "id","parent_id","level","parent_ids","sort","create_date","create_by","update_date","update_by","remarks","del_flag" };

    public void createFile(String[] tableNames, int type, Site site) {
        if(site == null){
            // 伪造一个site数据
            site = new Site();
            site.setName("默认站点");
            site.setDescription("这是一个默认站点");
            site.setAuthor("wang");

        }
        createFile(tableNames,type,driverName,userName,password,dataBaseUrl,baseDic,zipFile,AUTHOR_NAME_STRING,site);
    }

    public void createFile(String[] tableNames,int type,String driverName,String userName,String password,String dataBaseUrl,String baseDic,String zipFile,String author, Site site) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator fastAutoGenerator = FastAutoGenerator.create(dataBaseUrl, userName, password);
        fastAutoGenerator.globalConfig(builder -> {
            builder.author(author) // 设置作者
                    .disableOpenDir()   // 执行完成后是否自动打开输出目录
                    .outputDir(baseDic); // 输出目录
        });
        fastAutoGenerator.packageConfig(builder -> {
            builder.parent("com.mysiteforme.admin") // 设置父包名
                    .entity("entity") // 设置实体类包名
                    .controller("controller.system") // 设置 Controller 类包名
                    .mapper("dao") // 设置 Mapper 接口包名
                    .service("service") // 设置 Service 接口包名
                    .serviceImpl("service.impl") // 设置 Service 实现类包名
                    .xml("mappers"); // 设置 Mapper XML 文件包名
        });
        fastAutoGenerator.strategyConfig(builder -> {
            builder.addInclude(tableNames) // 设置需要生成的表名
                    .addTablePrefix("sys_")
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
                    //service
                    .superServiceClass(IService.class.getName())
                    .formatServiceFileName("%sService")
                    .serviceTemplate("/templates/vm/service.java.vm")
                    //serviceImpl
                    .superServiceImplClass(ServiceImpl.class.getName())
                    .formatServiceImplFileName("%sServiceImpl")
                    .serviceImplTemplate("/templates/vm/serviceImpl.java.vm")
                    .enableFileOverride()
                    .mapperBuilder()
                    .formatMapperFileName("%sDao")
                    .formatXmlFileName("%sDao")
                    .mapperTemplate("/templates/vm/mapper.java.vm")
                    .enableFileOverride();
        });
        fastAutoGenerator.injectionConfig((tableInfo, builder) -> {
            Map<String,Object> customMap = new HashMap<>();
            TableConfigResponse tableConfig = new TableConfigResponse();
            tableConfig.setTableName("sys_table_field_config");
            tableConfig.setAuthor("wangl");
            tableConfig.setBusinessName("表格字段配置");
            tableConfig.setIsSysFile(true);
            tableConfig.setIsValidateStringNull(true);
            List<TableFieldConfigResponse> fieldList = Lists.newArrayList();
            TableFieldConfigResponse columnNamefield = new TableFieldConfigResponse();
            columnNamefield.setColumnName("column_name");
            columnNamefield.setBusinessName("字段名");
            columnNamefield.setIsAddVisible(true);
            columnNamefield.setIsNullable(false);
            columnNamefield.setIsEditVisible(true);
            fieldList.add(columnNamefield);
            TableFieldConfigResponse businessNamefield = new TableFieldConfigResponse();
            businessNamefield.setColumnName("business_name");
            businessNamefield.setBusinessName("业务名称");
            businessNamefield.setIsAddVisible(true);
            businessNamefield.setIsEditVisible(true);
            businessNamefield.setIsNullable(false);
            businessNamefield.setValidationRules("asscvff");
            fieldList.add(businessNamefield);
            TableFieldConfigResponse isUniquefield = new TableFieldConfigResponse();
            isUniquefield.setColumnName("is_unique");
            isUniquefield.setBusinessName("是否唯一");
            isUniquefield.setIsAddVisible(true);
            isUniquefield.setIsEditVisible(true);
            isUniquefield.setIsNullable(false);
            fieldList.add(isUniquefield);
            tableConfig.setFieldList(fieldList);
            customMap.put(GenCodeConstants.INJECT_TABLE_CONFIG_DATA_KEY_PREFIX+"sys_table_field_config",tableConfig);
            fieldList.forEach(f -> {
                customMap.put(GenCodeConstants.INJECT_TABLE_FIELD_CONFIG_DATA_KEY_PREFIX+f.getColumnName(),f);
            });

            builder.customMap(customMap) // 自定义参数
                    .beforeOutputFile((t, objectMap) -> {
                        System.out.println("准备生成文件: " + t.getEntityName());
                        if(t.getName().contains("sys_")){
                            objectMap.put("sysFile", true);
                        }
                        // 可以在这里添加自定义逻辑，如修改 objectMap 中的配置
                        objectMap.put("site", site);
                        objectMap.put("entityName", t.getEntityName());
                    }).customFile(new ArrayList<CustomFile>() {{
                        // 通过格式化函数添加文件最后缀
                        add(new CustomFile.Builder()
                                .formatNameFunction(tableInfo -> String.format(GenCodeConstants.DEFAULT_FORMAT_ADD_REQUEST_NAME,tableInfo.getEntityName()))
                                .enableFileOverride()
                                .filePath(baseDic + "/front")
                                .fileName(GenCodeConstants.CUSTOM_JAVA_FILE_NAME)
                                .templatePath(GenCodeConstants.DEFAULT_ADD_REQUEST_TEMPLATE_PATH)
                                .packageName("request")
                                .build());
                        add(new CustomFile.Builder()
                                .enableFileOverride()
                                .fileName(".java")
                                .templatePath("/templates/vm/entity.java.vm")
                                .packageName("DTO")
                                .build());
                    }});
        });
        fastAutoGenerator.templateEngine(new VelocityTemplateEngine()); // 使用 Velocity 模板引擎
        fastAutoGenerator.execute(); // 执行生成
    }
}
