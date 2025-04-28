package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.Controller;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.base.DataEntity;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.base.TreeEntity;
import com.mysiteforme.admin.entity.DTO.TableFieldDTO;
import com.mysiteforme.admin.entity.request.PageListTableConfigRequest;
import com.mysiteforme.admin.entity.response.PageListTableConfigResponse;
import com.mysiteforme.admin.entity.response.TableConfigResponse;
import com.mysiteforme.admin.entity.response.TableFieldConfigResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.TableConfig;
import com.mysiteforme.admin.dao.TableConfigDao;
import com.mysiteforme.admin.service.TableConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TableConfigServiceImpl extends ServiceImpl<TableConfigDao, TableConfig> implements TableConfigService {

    private final DataSource dataSource;

    public TableConfigServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public IPage<PageListTableConfigResponse> selectPageTableConfig(PageListTableConfigRequest request) {
        return baseMapper.selectPageTableConfig(new Page<>(request.getPage(),request.getLimit()),request);
    }

    @Override
    public void saveOrUpdateTableConfig(TableConfig tableConfig) {

        // 校验表名是否在sys_table_config表中数据是否存在
        if(getTableNameCount(tableConfig.getTableName(),tableConfig.getId()) > 0 ){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_EXISTS).build();
        }
        // 检查数据库名称是否存在
        if(!baseMapper.getSchemaNameList().contains(tableConfig.getSchemaName())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_SCHEMA_NAME_NOT_EXISTS,tableConfig.getSchemaName()).build();
        }
        // 校验表名是否在数据库中
        if(baseMapper.existTable(tableConfig.getTableName(),tableConfig.getSchemaName()) == 0){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,tableConfig.getSchemaName(),tableConfig.getTableName()).build();
        }
        // 设置数据表类型
        checkTableType(tableConfig);
        baseMapper.insertOrUpdate(tableConfig);
    }

    /**
     * 校验准备新增或者编辑的表名是否在sys_table_config表中数据是否存在
     * @param tableName 表名
     * @param id 配置ID
     * @return 表名数量
     */
    private Long getTableNameCount(String tableName,Long id){
        LambdaQueryWrapper<TableConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableConfig::getTableName,tableName);
        if(id != null && id != 0){
            wrapper.ne(TableConfig::getId,id);
        }
        return count(wrapper);
    }

    @Override
    public void deleteTableConfig(Long id) {
        TableConfig tableConfig = baseMapper.selectById(id);
        if(tableConfig == null){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_NO_EXISTS).build();
        }
        tableConfig.setDelFlag(true);
        baseMapper.updateById(tableConfig);
    }

    @Override
    public void recoverTableConfig(Long id) {
        TableConfig tableConfig = baseMapper.selectById(id);
        if(tableConfig == null){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_NO_EXISTS).build();
        }
        // 校验表名是否在数据库中
        if(StringUtils.isBlank(tableConfig.getTableName())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY).build();
        }
        // 校验表名是否在数据库中
        if(StringUtils.isBlank(tableConfig.getSchemaName())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY).build();
        }
        // 校验表名是否在数据库中
        if(StringUtils.isBlank(tableConfig.getBusinessName())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.BUSINESS_NAME_NOT_EMPTY).build();
        }
        // 校验表名是否在数据库中
        if(!tableConfig.getTableName().startsWith(tableConfig.getTablePrefix())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_PREFIX_NOT_MATCH).build();
        }
        // 校验表名是否在数据库中
        if(baseMapper.existTable(tableConfig.getTableName(),tableConfig.getSchemaName()) == 0){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,tableConfig.getSchemaName(),tableConfig.getTableName()).build();
        }

        // 检查数据库名称是否存在
        if(!baseMapper.getSchemaNameList().contains(tableConfig.getSchemaName())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_SCHEMA_NAME_NOT_EXISTS,tableConfig.getSchemaName()).build();
        }
        // 如何生成路径存在，则校验是否合规
        if(StringUtils.isNotBlank(tableConfig.getGeneratePath())){
            if(!ToolUtil.isValidPath(tableConfig.getGeneratePath())){
                throw MyException.builder().businessError(MessageConstants.TableConfig.PATH_NOT_VALID_BY_SYSTEM).build();
            }
        }
        // 检查数据表类型
        checkTableType(tableConfig);
        tableConfig.setDelFlag(false);
        baseMapper.updateById(tableConfig);
    }

    private void checkTableType(TableConfig tableConfig) {
        Objects.requireNonNull(tableConfig.getSchemaName(), MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY);
        Objects.requireNonNull(tableConfig.getTableName(), MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY);
        List<TableFieldDTO> tableFieldDTOList = baseMapper.getFiledList(tableConfig.getSchemaName(), tableConfig.getTableName(),null);
        List<String> filedNamesList = tableFieldDTOList.stream().map(TableFieldDTO::getColumnName).toList();
        if(new HashSet<>(filedNamesList).containsAll(GenCodeConstants.TREE_TABLE_COMMON_FIELD)){
            tableConfig.setTableType(TableType.TREE_TABLE.getCode());
        } else if(new HashSet<>(filedNamesList).containsAll(GenCodeConstants.DATA_TABLE_COMMON_FIELD)){
            tableConfig.setTableType(TableType.DATA_TABLE.getCode());
        } else {
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_FIELDS_NOT_MATCH,tableConfig.getTableName()).build();
        }
    }

    @Override
    public List<String> getTableNameList(String schemaName) {
        List<String> result = baseMapper.getTableNameList(schemaName);
        return result.stream()
                .filter(tableName -> GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream()
                        .noneMatch(tableName::startsWith))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSchemaNameList() {
        List<String> result = baseMapper.getSchemaNameList();
        result.removeAll(GenCodeConstants.FIXED_TABLE_NAME);
        return result;
    }

    @Override
    public void downloadCode(List<Long> ids, HttpServletResponse response) {
        List<TableConfigResponse> tableConfigList = baseMapper.getTableConfigDetail(ids);
        if(tableConfigList == null || tableConfigList.isEmpty()){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_NO_EXISTS).build();
        }
        synchronized(this){

            String basePath = getOutPutDir();
            String zipPath = getOutZipDir();
            File baseFolder = new File(basePath);
            ZipUtil.deleteDir(baseFolder);
            tableConfigList.forEach(this::generatorCode);
            File f = new File(zipPath);
            try {
                if(f.exists() && !f.delete()){
                    log.warn("下载源码---删除文件失败:{}",f.getName());
                }
                cn.hutool.core.util.ZipUtil.zip(basePath, zipPath);
            } catch (SecurityException e) {
                log.error("下载源码---压缩文件失败:{}", e.getMessage());
                throw MyException.builder().businessError(MessageConstants.TableConfig.ZIP_CODE_ERROR).build();
            }

            try {
                // --- 1. 修正 Content-Type ---
                response.setContentType("application/zip");
                // --- 2. 修正 Content-Disposition 文件名编码 ---
                // 获取原始名称
                String originalFilename = f.getName();
                // 使用 URLEncoder 进行标准编码 (推荐)
                String encodedFilename = java.net.URLEncoder.encode(originalFilename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename;
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
                // --- 3. 设置 Content-Length ---
                response.setContentLengthLong(f.length());
                // --- 4. 设置其他必要的头 (例如 Cache-Control) ---
                response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                response.setHeader(HttpHeaders.PRAGMA, "no-cache");
                response.setHeader(HttpHeaders.EXPIRES, "0");

                try (BufferedInputStream br = new BufferedInputStream(Files.newInputStream(f.toPath()));
                     OutputStream out = response.getOutputStream()) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = br.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                }

//                if(!f.delete()) {
//                    log.error("删除文件失败:{}",f.getName());
//                }
            } catch (IOException e) {
                log.error("下载代码出现异常:{}",e.getMessage());
                throw MyException.builder().businessError(MessageConstants.TableConfig.DOWLOAD_CODE_ERROR).build();

            }
        }
    }

    /**
     * 生成代码配置
     * @param tableConfig 表配置
     */
    private void generatorCode(TableConfigResponse tableConfig){
        if(Objects.equals(TableType.ASSOCIATED_TABLE.getCode(), tableConfig.getTableType())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.CURRENT_TABLE_CAN_NOT_GENERATE_CODE,tableConfig.getTableName()).build();
        }
        // 参数配置
        FastAutoGenerator generator = FastAutoGenerator.create(dataSourceConfig(tableConfig));

        // 全局配置
        generator.globalConfig(globalConfig(tableConfig));

        // 包配置
        generator.packageConfig(packageConfig(tableConfig));

        // 策略配置
        generator.strategyConfig(strategyConfig(tableConfig));

        // 注入配置
        generator.injectionConfig(injectionConfig(tableConfig));

        // 设置模板引擎
        generator.templateEngine(new VelocityTemplateEngine());
        //执行
        generator.execute();
    }

    /**
     * 数据源配置
     */
    private DataSourceConfig.Builder dataSourceConfig(TableConfigResponse tableConfig) {
        return new DataSourceConfig.Builder(dataSource).schema(tableConfig.getSchemaName());
    }
    /**
     * 全局配置
     */
    private Consumer<GlobalConfig.Builder> globalConfig(TableConfigResponse tableConfig) {
        return builder -> {
            if(StringUtils.isNotBlank(tableConfig.getAuthor())) {
                builder.author(tableConfig.getAuthor());
            }else{
                builder.author(GenCodeConstants.DEFAULT_AUTHOR);
            }
            builder.outputDir(getOutPutDir());
            builder.disableOpenDir();
            builder.build();
        };
    }

    /**
     * 获取最终的文件生成的基本路径
     * @return 最终路径
     */
    private String getOutPutDir(){
        String outDir;
        if("windows".equals(ToolUtil.getOs())){
            outDir = GenCodeConstants.WINDOWS_GENERATOR_PATH;
        }else{
            outDir = GenCodeConstants.LINUX_GENERATOR_PATH;
        }
        return outDir + "/" + MySecurityUser.loginName();
    }

    private String getOutZipDir(){
        String outZipDir;
        if("windows".equals(ToolUtil.getOs())){
            outZipDir = GenCodeConstants.WINDOWS_SOURCE_CODE_NAME;
        }else{
            outZipDir = GenCodeConstants.LINUX_SOURCE_CODE_NAME;
        }
        return String.format(outZipDir,MySecurityUser.loginName());
    }
    /**
     * 包配置
     */
    private Consumer<PackageConfig.Builder> packageConfig(TableConfigResponse tableConfig) {
        return builder -> {
            if(StringUtils.isNotBlank(tableConfig.getPackageName())){
                builder.parent(tableConfig.getPackageName());
            }else{
                builder.parent(GenCodeConstants.BASE_PACKAGE);
            }
            builder.entity(GenCodeConstants.DEFAULT_ENTITY_PACKAGE_NAME);
            builder.controller(getControllerPackageName(tableConfig.getTableName()));
            builder.mapper(GenCodeConstants.DEFAULT_MAPPER_PACKAGE_NAME);
            builder.service(GenCodeConstants.DEFAULT_SERVICE_PACKAGE_NAME);
            builder.serviceImpl(GenCodeConstants.DEFAULT_SERVICE_IMPL_PACKAGE_NAME);
            builder.xml(GenCodeConstants.DEFAULT_MAPPER_XML_PACKAGE_NAME);
            builder.pathInfo(Collections.singletonMap(OutputFile.xml, getOutPutDir() + GenCodeConstants.DEFAULT_MAPPER_XML_PATH));
            builder.build();
        };
    }

    /**
     * 根据表名前缀判断controller位置
     * @param tableName 表名
     * @return controller包名
     */
    private String getControllerPackageName(String tableName){
        if(tableName.startsWith(GenCodeConstants.DEFAULT_TABLE_PREFIX)){
            return GenCodeConstants.DEFAULT_SYSTEM_CONTROLLER_PACKAGE_NAME;
        }else{
            return GenCodeConstants.DEFAULT_CONTROLLER_PACKAGE_NAME;
        }
    }
    /**
     * 策略配置
     */
    private Consumer<StrategyConfig.Builder> strategyConfig(TableConfigResponse tableConfig) {
        return builder -> {
            // 添加表匹配
            builder.addInclude(tableConfig.getTableName());
            // 添加过滤表前缀
            if(StringUtils.isNotBlank(tableConfig.getTablePrefix())){
                builder.addTablePrefix(tableConfig.getTablePrefix());
            }else{
                builder.addTablePrefix(GenCodeConstants.DEFAULT_TABLE_PREFIX);
            }
            // entity 实体类设置
            Entity.Builder entityBuilder = builder.entityBuilder();
            // 设置实体类下划线转驼峰命名
            entityBuilder.columnNaming(NamingStrategy.underline_to_camel);
            if(Objects.equals(TableType.DATA_TABLE.getCode(),tableConfig.getTableType())){
                // 设置实体类父类
                entityBuilder.superClass(DataEntity.class);
                // 设置实体类公共字段
                entityBuilder.addSuperEntityColumns(GenCodeConstants.DATA_TABLE_COMMON_FIELD);
            } else if (Objects.equals(TableType.TREE_TABLE.getCode(),tableConfig.getTableType())) {
                // 设置实体类父类
                entityBuilder.superClass(TreeEntity.class);
                // 设置实体类公共字段
                entityBuilder.addSuperEntityColumns(GenCodeConstants.TREE_TABLE_COMMON_FIELD);
            }
            // 设置实体类模板文件位置
            entityBuilder.javaTemplate(GenCodeConstants.DEFAULT_ENTITY_TEMPLATE_PATH);
            // 启用字段注解
            entityBuilder.enableLombok();
            // 禁用生成 serialVersionUID
            entityBuilder.disableSerialVersionUID();
            // 启用字段注解
            entityBuilder.enableTableFieldAnnotation();
            // 允许覆盖已有文件
            entityBuilder.enableFileOverride();

            // controller 类设置
            Controller.Builder controllerBuilder = builder.controllerBuilder();
            // 启用 REST 风格
            controllerBuilder.enableRestStyle();
            // controller文件名字生成规则
            controllerBuilder.formatFileName(GenCodeConstants.DEFAULT_FORMAT_CONTROLLER_FILE_NAME);
            // 设置默认的controller模板路径
            controllerBuilder.template(GenCodeConstants.DEFAULT_CONTROLLER_TEMPLATE_PATH);
            // 设置文件覆盖
            controllerBuilder.enableFileOverride();

            // service 设置
            com.baomidou.mybatisplus.generator.config.builder.Service.Builder serviceBuilder = builder.serviceBuilder();
            // 设置继承父类
            serviceBuilder.superServiceClass(IService.class);
            // 设置service文件名字生成规则
            serviceBuilder.formatServiceFileName(GenCodeConstants.DEFAULT_FORMAT_SERVICE_FILE_NAME);
            // 设置service模板路径
            serviceBuilder.serviceTemplate(GenCodeConstants.DEFAULT_SERVICE_TEMPLATE_PATH);
            // 设置serviceImpl继承父类
            serviceBuilder.superServiceImplClass(ServiceImpl.class);
            // 设置serviceImpl文件名字生成规则
            serviceBuilder.formatServiceImplFileName(GenCodeConstants.DEFAULT_FORMAT_SERVICE_IMPL_FILE_NAME);
            // 设置serviceImpl模板路径
            serviceBuilder.serviceImplTemplate(GenCodeConstants.DEFAULT_SERVICE_IMPL_TEMPLATE_PATH);
            // 设置文件覆盖
            serviceBuilder.enableFileOverride();

            // mapper 设置
            Mapper.Builder mapperBuilder = builder.mapperBuilder();
            // 设置mapper文件名字生成规则
            mapperBuilder.formatMapperFileName(GenCodeConstants.DEFAULT_FORMAT_MAPPER_FILE_NAME);
            // 设置mapper模板路径
            mapperBuilder.mapperTemplate(GenCodeConstants.DEFAULT_MAPPER_TEMPLATE_PATH);
            // 设置mapper xml文件名字生成规则
            mapperBuilder.formatXmlFileName(GenCodeConstants.DEFAULT_FORMAT_MAPPER_XML_FILE_NAME);
            // 设置mapper xml模板路径
            mapperBuilder.mapperXmlTemplate(GenCodeConstants.DEFAULT_MAPPER_XML_TEMPLATE_PATH);
            // 设置文件覆盖
            mapperBuilder.enableFileOverride();
            builder.build();
        };
    }
    /**
     * 注入配置
     */
    private Consumer<InjectionConfig.Builder> injectionConfig(TableConfigResponse tableConfig) {
        return builder -> {
            // 注入表和字段配置信息
            Map<String,Object> customMap = new HashMap<>();
            // 是否有sort字段
            boolean hasSortField = false;
            // 是否需要验证字符串
            boolean isValidateStringNull = false;
            // 是否需要验证对象
            boolean isValidateObjectNull = false;
            // 是否需要验证正则
            boolean isValidateRuler = false;
            // 新增时需要导入的包
            Set<String> addPackages = new HashSet<>();
            // 编辑时需要导入的包
            Set<String> updatePackages = new HashSet<>();
            // 列表页参数需要导入的包
            Set<String> pageListPackages = new HashSet<>();
            List<TableFieldConfigResponse> fieldList = tableConfig.getFieldList();
            if(!fieldList.isEmpty()){
                for(TableFieldConfigResponse field: fieldList){
                    // 设置导入包路径集合
                    DbColumnType dbColumnType = DbColumnType.getByType(field.getJavaType());
                    if(dbColumnType != null && StringUtils.isNotBlank(dbColumnType.getPkg())) {
                        if (field.getIsAddVisible()) {
                            addPackages.add(dbColumnType.getPkg());
                        }
                        if (field.getIsEditVisible()) {
                            updatePackages.add(dbColumnType.getPkg());
                        }
                        if (field.getIsListVisible()) {
                            pageListPackages.add(dbColumnType.getPkg());
                        }
                    }
                    // 添加字段信息数据到map
                    customMap.put(GenCodeConstants.INJECT_TABLE_FIELD_CONFIG_DATA_KEY_PREFIX + field.getColumnName(), field);
                    if(!field.getIsNullable()){
                        if(GenCodeConstants.DB_STRING_FIELD.contains(field.getColumnType())){
                            isValidateStringNull = true;
                        }else{
                            isValidateObjectNull = true;
                        }
                    }
                    if(StringUtils.isNotBlank(field.getValidationRules())){
                        isValidateRuler = true;
                    }
                    // 如果字段中有sort字段
                    if(GenCodeConstants.FIELD_SORT_STRING.equalsIgnoreCase(field.getColumnName())){
                        hasSortField = true;
                    }

                    // 设置前端boolean类型
                    if(GenCodeConstants.FRONT_BO0LEAN_FIELD.contains(field.getColumnType())){
                        field.setFrontType(GenCodeConstants.FRONT_BOOLEAN_TYPE);
                    }
                    // 设置前端string类型
                    if(GenCodeConstants.FRONT_STRING_FIELD.contains(field.getColumnType())){
                        field.setFrontType(GenCodeConstants.FRONT_STRING_TYPE);
                    }
                    // 设置前端number类型
                    if(GenCodeConstants.FRONT_NUMBER_FIELD.contains(field.getColumnType())){
                        field.setFrontType(GenCodeConstants.FRONT_NUMBER_TYPE);
                    }
                }
            }
            // 如果是tree类型表则默认拥有sort字段
            if(Objects.equals(TableType.TREE_TABLE.getCode(),tableConfig.getTableType())){
                hasSortField = true;
            }
            tableConfig.setHasSortField(hasSortField);
            tableConfig.setIsSysFile(tableConfig.getTableName().startsWith(GenCodeConstants.DEFAULT_TABLE_PREFIX));
            tableConfig.setIsValidateStringNull(isValidateStringNull);
            tableConfig.setIsValidateObjectNull(isValidateObjectNull);
            tableConfig.setIsValidateRuler(isValidateRuler);
            tableConfig.setAddPackages(addPackages);
            tableConfig.setUpdatePackages(updatePackages);
            tableConfig.setPageListPackages(pageListPackages);
            customMap.put(GenCodeConstants.INJECT_TABLE_CONFIG_DATA_KEY_PREFIX+tableConfig.getTableName(),tableConfig);
            builder.customMap(customMap);
            List<CustomFile> customFileList = Lists.newArrayList();
            // 分页列表请求参数对象
            CustomFile.Builder pageListRequestCustomFile = new CustomFile.Builder();
            pageListRequestCustomFile.formatNameFunction(t -> String.format(GenCodeConstants.DEFAULT_FORMAT_PAGE_LIST_REQUEST_NAME,t.getEntityName()))
                    .packageName(GenCodeConstants.DEFAULT_REQUEST_FILE_PATH)
                    .fileName(GenCodeConstants.CUSTOM_JAVA_FILE_NAME)
                    .templatePath(GenCodeConstants.DEFAULT_PAGE_LIST_REQUEST_TEMPLATE_PATH)
                    .enableFileOverride();
            customFileList.add(pageListRequestCustomFile.build());
            // 新增的Request.java 请求参数对象
            CustomFile.Builder requestCustomFile = new CustomFile.Builder();
                    requestCustomFile.formatNameFunction(t -> String.format(GenCodeConstants.DEFAULT_FORMAT_ADD_REQUEST_NAME,t.getEntityName()))
                    .packageName(GenCodeConstants.DEFAULT_REQUEST_FILE_PATH)
                    .fileName(GenCodeConstants.CUSTOM_JAVA_FILE_NAME)
                    .templatePath(GenCodeConstants.DEFAULT_ADD_REQUEST_TEMPLATE_PATH)
                    .enableFileOverride();
            customFileList.add(requestCustomFile.build());
            // 编辑的Request参数对象
            CustomFile.Builder updateRequestCustomFile = new CustomFile.Builder();
            updateRequestCustomFile.formatNameFunction(t -> String.format(GenCodeConstants.DEFAULT_FORMAT_UPDATE_REQUEST_NAME,t.getEntityName()))
                    .packageName(GenCodeConstants.DEFAULT_REQUEST_FILE_PATH)
                    .fileName(GenCodeConstants.CUSTOM_JAVA_FILE_NAME)
                    .templatePath(GenCodeConstants.DEFAULT_UPDATE_REQUEST_TEMPLATE_PATH)
                    .enableFileOverride();
            customFileList.add(updateRequestCustomFile.build());

            // 前端自定义文件
            // model文件 .ts类型
            CustomFile.Builder frontModelCustomFile = new CustomFile.Builder();
            frontModelCustomFile.formatNameFunction(t -> String.format(GenCodeConstants.DEFAULT_FORMAT_FRONT_MODEL_FILE_NAME,t.getEntityPath()))
                    .filePath(getOutPutDir() + GenCodeConstants.DEFAULT_FRONT_MODEL_FILE_PATH)
                    .fileName(GenCodeConstants.CUSTOM_TS_FILE_NAME)
                    .templatePath(GenCodeConstants.DEFAULT_FRONT_MODEL_TEMPLATE_PATH)
                    .enableFileOverride();
            customFileList.add(frontModelCustomFile.build());
            // api文件 .ts类型
            CustomFile.Builder frontApiCustomFile = new CustomFile.Builder();
            frontApiCustomFile.formatNameFunction(t -> String.format(GenCodeConstants.DEFAULT_FORMAT_FRONT_API_FILE_NAME,t.getEntityPath()))
                    .filePath(getOutPutDir() + GenCodeConstants.DEFAULT_FRONT_API_FILE_PATH)
                    .fileName(GenCodeConstants.CUSTOM_TS_FILE_NAME)
                    .templatePath(GenCodeConstants.DEFAULT_FRONT_API_TEMPLATE_PATH)
                    .enableFileOverride();
            customFileList.add(frontApiCustomFile.build());
            // 视图文件 .vue类型
            CustomFile.Builder frontViewCustomFile = new CustomFile.Builder();
            frontViewCustomFile.formatNameFunction(t -> t.getEntityPath() + "/" + t.getEntityName())
                    .filePath(getOutPutDir() + GenCodeConstants.DEFAULT_FRONT_VIEW_FILE_PATH)
                    .fileName(GenCodeConstants.CUSTOM_VUE_FILE_NAME)
                    .templatePath(GenCodeConstants.DEFAULT_FRONT_VIEW_TEMPLATE_PATH)
                    .enableFileOverride();
            customFileList.add(frontViewCustomFile.build());

            builder.customFile(customFileList);

        };
    }

}
