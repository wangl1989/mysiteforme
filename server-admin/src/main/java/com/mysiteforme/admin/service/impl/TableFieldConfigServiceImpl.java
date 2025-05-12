package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.dao.TableConfigDao;
import com.mysiteforme.admin.entity.DTO.TableFieldDTO;
import com.mysiteforme.admin.entity.TableConfig;
import com.mysiteforme.admin.entity.request.GetSimpleFieldRequest;
import com.mysiteforme.admin.entity.request.PageListTableFieldsRequest;
import com.mysiteforme.admin.entity.request.SyncTableFieldConfigRequest;
import com.mysiteforme.admin.entity.request.UpdateTableFieldConfigRequest;
import com.mysiteforme.admin.entity.response.BaseTableFieldConfigResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.DictService;
import com.mysiteforme.admin.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.TableFieldConfig;
import com.mysiteforme.admin.dao.TableFieldConfigDao;
import com.mysiteforme.admin.service.TableFieldConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.mysiteforme.admin.util.GenCodeConstants.DB_TO_FORM_COMPONENT_TYPE_MAP;
import static com.mysiteforme.admin.util.GenCodeConstants.DB_TO_JAVA_TYPE_MAP;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TableFieldConfigServiceImpl extends ServiceImpl<TableFieldConfigDao, TableFieldConfig> implements TableFieldConfigService {

    private final TableConfigDao tableConfigDao;

    private final DictService dictService;

    /**
     * 根据数据库字段类型获取对应的Java类型
     * @param dbType 数据库字段类型
     * @return Java类型
     */
    private String getJavaType(String dbType) {
        if (StringUtils.isBlank(dbType)) {
            return "String";
        }

        // 处理类型中可能包含的长度信息，如varchar(255)
        String baseType = dbType.toLowerCase().replaceAll("\\(.*\\)", "");

        return DB_TO_JAVA_TYPE_MAP.getOrDefault(baseType, "String");
    }

    private FormComponentType getFormComponentType(String dbType) {
        // 处理类型中可能包含的长度信息，如varchar(255)
        String baseType = dbType.toLowerCase().replaceAll("\\(.*\\)", "");

        return DB_TO_FORM_COMPONENT_TYPE_MAP.getOrDefault(baseType, null);
    }
    @Override
    public IPage<BaseTableFieldConfigResponse> selectPageTableFieldConfig(PageListTableFieldsRequest request) {
        // 查询表配置
        TableConfig tableConfig = tableConfigDao.selectById(request.getTableConfigId());
        if(tableConfig == null){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_NO_EXISTS).build();
        }
        IPage<BaseTableFieldConfigResponse> result = new Page<>();
        // 查询表字段配置
        LambdaQueryWrapper<TableFieldConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableFieldConfig::getTableConfigId, tableConfig.getId());
        wrapper.eq(TableFieldConfig::getDelFlag, false);
        if(StringUtils.isNotBlank(request.getColumnName())){
            wrapper.like(TableFieldConfig::getColumnName,request.getColumnName());
        }
        if(StringUtils.isNotBlank(request.getBusinessName())){
            wrapper.like(TableFieldConfig::getBusinessName,request.getBusinessName());
        }
        if(StringUtils.isNotBlank(request.getFormComponentType())){
            wrapper.eq(TableFieldConfig::getFormComponentType,request.getFormComponentType());
        }
        if(request.getIsQueryField() != null){
            wrapper.eq(TableFieldConfig::getIsQueryField,request.getIsQueryField());
        }
        wrapper.orderByAsc(TableFieldConfig::getSort);
        IPage<TableFieldConfig> fieldConfigs = this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
        List<TableFieldConfig> recordList = fieldConfigs.getRecords();
        if(!CollectionUtils.isEmpty(recordList)){
            List<BaseTableFieldConfigResponse> resultList = recordList.stream().map(t -> {
                BaseTableFieldConfigResponse response = new BaseTableFieldConfigResponse();
                BeanUtils.copyProperties(t, response);
                response.setTableName(tableConfig.getTableName());
                response.setSchemaName(tableConfig.getSchemaName());
                response.setTableConfigId(request.getTableConfigId());
                return response;
            }).toList();
            result.setRecords(resultList);
            result.setTotal(fieldConfigs.getTotal());
        }
        result.setSize(request.getLimit());
        result.setCurrent(request.getPage());
        return result;
    }

    @Override
    public List<TableFieldDTO> getSimpleTableField(GetSimpleFieldRequest request) {
        return tableConfigDao.getSimpleFiledList(request.getSchemaName(), request.getTableName());
    }

    @Override
    public boolean syncTableFieldConfig(SyncTableFieldConfigRequest request) {
        // 查询表配置
        TableConfig tableConfig = getTableConfig(request.getTableName(), request.getSchemaName());

        // 获取数据库中的字段列表
        List<TableFieldDTO> dbFieldNames = tableConfigDao.getFiledList(request.getSchemaName(), request.getTableName(), tableConfig.getTableType());

        if (dbFieldNames.isEmpty()) {
            throw MyException.builder().businessError(MessageConstants.TableFieldConfig.FIELD_NOT_FOUND).build();
        }

        // 获取已经存在的字段配置
        LambdaQueryWrapper<TableFieldConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableFieldConfig::getTableConfigId, tableConfig.getId());
        wrapper.eq(TableFieldConfig::getDelFlag, false);
        List<TableFieldConfig> existingFields = list(wrapper);

        // 处理新增字段
        List<TableFieldConfig> newFields = new ArrayList<>();
        for (TableFieldDTO tableFieldDTO : dbFieldNames) {
            boolean exists = false;
            for (TableFieldConfig existingField : existingFields) {
                if (existingField.getColumnName().equals(tableFieldDTO.getColumnName())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                // 创建新的字段配置
                TableFieldConfig newField = new TableFieldConfig();
                newField.setTableConfigId(tableConfig.getId());
                newField.setColumnName(tableFieldDTO.getColumnName());
                newField.setColumnComment(tableFieldDTO.getColumnComment());
                if(StringUtils.isBlank(tableFieldDTO.getColumnComment())){
                    newField.setBusinessName(tableFieldDTO.getColumnName());
                }else{
                    newField.setBusinessName(tableFieldDTO.getColumnComment());
                }
                newField.setColumnType(tableFieldDTO.getColumnType());
                Long length = null;
                if (tableFieldDTO.getCharLength() != null) {
                    length = tableFieldDTO.getCharLength();
                } else {
                    if(tableFieldDTO.getNumberLength() != null){
                        length = tableFieldDTO.getNumberLength();
                    }
                }
                newField.setColumnLength(length);
                // 设置默认值
                newField.setJavaType(getJavaType(tableFieldDTO.getColumnType())); // 设置Java类型
                newField.setJavaFieldName(toCamelCase(tableFieldDTO.getColumnName())); // 驼峰命名
                newField.setIsUnique(false);
                newField.setIsNullable(tableFieldDTO.getIsNullable());
                newField.setIsListVisible(true);
                newField.setIsAddVisible(true);
                newField.setIsEditVisible(true);
                newField.setIsDetailVisible(true);
                newField.setIsQueryField(false);
                newField.setSort(existingFields.size() + newFields.size() + 1); // 设置排序
                FormComponentType formComponentType = getFormComponentType(tableFieldDTO.getColumnType());
                if(formComponentType != null){
                    newField.setFormComponentType(formComponentType.getCode());
                }
                newFields.add(newField);
            }
        }

        // 批量保存新字段
        if (!newFields.isEmpty()) {
            saveBatch(newFields);
        }

        return true;
    }

    @Override
    public boolean updateTableFieldConfig(UpdateTableFieldConfigRequest request) {
        // 查询字段配置
        TableFieldConfig fieldConfig = getById(request.getId());
        if (fieldConfig == null) {
            throw MyException.builder().businessError(MessageConstants.TableFieldConfig.FIELD_CONFIG_NOT_FOUND).build();
        }
        // 查询表配置
        TableConfig tableConfig = tableConfigDao.selectById(fieldConfig.getTableConfigId());
        // 验证关联表是否存在
        if(tableConfig == null){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_NO_EXISTS).build();
        }

        TableFieldDTO field = tableConfigDao.getFieldDetail(tableConfig.getSchemaName(),tableConfig.getTableName(),fieldConfig.getColumnName());
        if(field == null){
            throw MyException.builder().businessError(MessageConstants.TableFieldConfig.FIELD_NOT_FOUND).build();
        }
        // 设置业务名称
        fieldConfig.setBusinessName(request.getBusinessName());
        // 验证表单组件类型
        if (StringUtils.isNotBlank(request.getFormComponentType())) {
            try {
                FormComponentType.valueOf(request.getFormComponentType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw MyException.builder().businessError(MessageConstants.TableFieldConfig.FORM_COMPONENT_TYPE_INVALID).build();
            }
            List<String> typeList = GenCodeConstants.DB_TO_FRONT_TYPE.get(field.getColumnType());
            if(!typeList.contains(request.getFormComponentType().toUpperCase())){
                throw MyException.builder().businessError(MessageConstants.TableFieldConfig.TABLE_FIELD_CONFIG_FRONT_TYPE_NOT_MATCH,request.getFormComponentType(),field.getColumnType()).build();
            }
            fieldConfig.setFormComponentType(request.getFormComponentType().toUpperCase());

            // 验证字典类型
            if (FormComponentType.SELECT.getCode().equalsIgnoreCase(request.getFormComponentType()) ||
                    FormComponentType.RADIO.getCode().equalsIgnoreCase(request.getFormComponentType()) ||
                    FormComponentType.CHECKBOX.getCode().equalsIgnoreCase(request.getFormComponentType())) {
                // 如果关联类型为空
                if (request.getAssociatedType() == null) {
                    throw MyException.builder().businessError(MessageConstants.TableFieldConfig.ASSOCIATED_TYPE_REQUIRED).build();
                } else {
                    fieldConfig.setAssociatedType(request.getAssociatedType());
                }
                // 验证关联类型的值
                this.checkAssociatedType(tableConfig,fieldConfig,request);
            }
        } else {
            fieldConfig.setFormComponentType(null);
            fieldConfig.setAssociatedType(request.getAssociatedType());
            if (request.getAssociatedType() != null) {
                this.checkAssociatedType(tableConfig,fieldConfig,request);
            }
        }


        // 处理排序
        if (request.getSort() != null) {
            if (request.getSort() == 0 || request.getSort() == 1) {
                // 获取当前表里的字段的sort最大值然后加1
                LambdaQueryWrapper<TableFieldConfig> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TableFieldConfig::getTableConfigId, fieldConfig.getTableConfigId());
                wrapper.eq(TableFieldConfig::getDelFlag, false);
                wrapper.ne(TableFieldConfig::getId, fieldConfig.getId());
                wrapper.orderByDesc(TableFieldConfig::getSort);
                wrapper.last("LIMIT 1");

                TableFieldConfig maxSortField = getOne(wrapper);
                if (maxSortField != null) {
                    fieldConfig.setSort(maxSortField.getSort() + 1);
                } else {
                    fieldConfig.setSort(1);
                }
            } else {
                fieldConfig.setSort(request.getSort());
            }
        }

        if(request.getIsUnique() != null){
            fieldConfig.setIsUnique(request.getIsUnique());
        }

        if(request.getIsNullable() != null){
            Boolean oldResult = field.getIsNullable();
            if(!oldResult){
                if(request.getIsNullable()){
                    throw MyException.builder().businessError(MessageConstants.TableFieldConfig.FIELD_NOT_NULLABLE).build();
                }
            }
            fieldConfig.setIsNullable(request.getIsNullable());
        }

        // 设置其他字段
        if (request.getIsListVisible() != null) {
            fieldConfig.setIsListVisible(request.getIsListVisible());
        }

        if (request.getIsAddVisible() != null) {
            fieldConfig.setIsAddVisible(request.getIsAddVisible());
        }

        if (request.getIsEditVisible() != null) {
            fieldConfig.setIsEditVisible(request.getIsEditVisible());
        }

        if (request.getIsDetailVisible() != null) {
            fieldConfig.setIsDetailVisible(request.getIsDetailVisible());
        }


        if (request.getIsQueryField() != null) {
            fieldConfig.setIsQueryField(request.getIsQueryField());
            if(request.getIsQueryField()) {
                if (StringUtils.isBlank(request.getQueryType())) {
                    throw MyException.builder().businessError(MessageConstants.TableFieldConfig.QUERY_TYPE_NOT_NULL).build();
                }
                try {
                    QueryType.valueOf(request.getQueryType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw MyException.builder().businessError(MessageConstants.TableFieldConfig.QUERY_TYPE_INVALID).build();
                }
                fieldConfig.setQueryType(request.getQueryType().toLowerCase());
            }else{
                fieldConfig.setQueryType(null);
            }
        }
        fieldConfig.setValidationRules(request.getValidationRules());
        fieldConfig.setRemarks(request.getRemarks());
        return updateById(fieldConfig);
    }

    private void checkAssociatedType(TableConfig tableConfig,TableFieldConfig fieldConfig,UpdateTableFieldConfigRequest request){
        // 验证关联类型的值
        try {
            AssociatedType.getByCode(request.getAssociatedType());
        } catch (IllegalArgumentException e) {
            throw MyException.builder().businessError(MessageConstants.TableFieldConfig.ASSOCIATED_TYPE_INVALID).build();
        }
        // 如果关联类型是字典类型，则需要校验字典类型是否存在
        if(Objects.equals(AssociatedType.DICT.getCode(), request.getAssociatedType())){
            // 如果关联字典类型为空
            if(StringUtils.isBlank(request.getAssociatedDictType())){
                throw MyException.builder().businessError(MessageConstants.TableFieldConfig.DICT_TYPE_REQUIRED).build();
            } else {
                fieldConfig.setAssociatedDictType(request.getAssociatedDictType());
            }
            // 验证字典类型是否存在
            if (dictService.getCountByType(request.getAssociatedDictType()) <= 0) {
                throw MyException.builder().businessError(MessageConstants.TableFieldConfig.DICT_TYPE_NOT_FOUND).build();
            }
        }
        // 如果关联类型是数据表类型，则需要校验数据表是否存在，以及数据表对应的显示字段是否存在
        if(Objects.equals(AssociatedType.TABLE.getCode(), request.getAssociatedType())){
            // 如果关联表名字为空
            if(StringUtils.isBlank(request.getAssociatedTable())){
                throw MyException.builder().businessError(MessageConstants.TableFieldConfig.TABLE_TYPE_REQUIRED).build();
            } else {
                fieldConfig.setAssociatedTable(request.getAssociatedTable());
            }
            List<String> tableNameList = tableConfigDao.getTableNameList(tableConfig.getSchemaName());
            if(!tableNameList.contains(request.getAssociatedTable())){
                throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,tableConfig.getSchemaName(),request.getAssociatedTable()).build();
            }
            // 验证字段是否存在
            List<TableFieldDTO> tableFieldDTOList = tableConfigDao.getFiledList(tableConfig.getSchemaName(), request.getAssociatedTable(),tableConfig.getTableType());
            List<String> fieldNames = tableFieldDTOList.stream().map(TableFieldDTO::getColumnName).toList();
            if(!fieldNames.contains(request.getAssociatedTableField())){
                throw MyException.builder().businessError(MessageConstants.TableFieldConfig.FIELD_NOT_FOUND).build();
            } else {
                fieldConfig.setAssociatedTableField(request.getAssociatedTableField());
            }
        }
    }

    @Override
    public void sortFields(List<Long> ids) {
        // 查询所有指定的配置
        List<TableFieldConfig> fieldConfigs = listByIds(ids);

        // 获取有效的配置ID（未删除的）
        Set<Long> validIds = fieldConfigs.stream()
                .filter(config -> !config.getDelFlag())
                .map(TableFieldConfig::getId)
                .collect(Collectors.toSet());

        // 找出无效的ID
        List<Long> invalidIds = ids.stream()
                .filter(id -> !validIds.contains(id))
                .toList();

        // 如果存在无效ID，抛出异常
        if (!invalidIds.isEmpty()) {
            throw MyException.builder()
                    .businessError(MessageConstants.TableFieldConfig.FIELD_IDS_INVALID,invalidIds.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                    .build();
        }

        // 创建ID到配置的映射，用于快速查找
        Map<Long, TableFieldConfig> configMap = fieldConfigs.stream()
                .collect(Collectors.toMap(TableFieldConfig::getId, config -> config));

        // 根据传入的ids顺序重新排序配置列表
        List<TableFieldConfig> sortedConfigs = ids.stream()
                .map(configMap::get)
                .collect(Collectors.toList());

        // 获取这些配置所属的同一个tableConfigId
        Long tableConfigId = sortedConfigs.get(0).getTableConfigId();

        // 验证所有配置是否属于同一个表
        boolean allSameTable = sortedConfigs.stream()
                .allMatch(config -> config.getTableConfigId().equals(tableConfigId));

        if (!allSameTable) {
            throw MyException.builder()
                    .businessError(MessageConstants.TableFieldConfig.ALL_FIELDS_MUST_SAME_TABLE)
                    .build();
        }
        List<Integer> sortList = sortedConfigs.stream().map(TableFieldConfig::getSort).sorted().toList();

        // 批量更新排序
        for (int i = 0; i < sortedConfigs.size(); i++) {
            TableFieldConfig config = sortedConfigs.get(i);
            config.setSort(sortList.get(i));
        }

        // 批量更新
        updateBatchById(sortedConfigs);

    }

    /**
     * 获取表配置
     * @param tableName 表名
     * @param schemaName 数据库名
     * @return 表配置
     */
    private TableConfig getTableConfig(String tableName, String schemaName) {
        LambdaQueryWrapper<TableConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableConfig::getTableName, tableName);
        wrapper.eq(TableConfig::getSchemaName, schemaName);
        wrapper.eq(TableConfig::getDelFlag, false);

        TableConfig tableConfig = tableConfigDao.selectOne(wrapper);
        if (tableConfig == null) {
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_CONFIG_NO_EXISTS).build();
        }

        return tableConfig;
    }

    /**
     * 将下划线命名转换为驼峰命名
     * @param name 下划线命名
     * @return 驼峰命名
     */
    private String toCamelCase(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }
}
