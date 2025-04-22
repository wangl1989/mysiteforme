package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.DTO.TableFieldDTO;
import com.mysiteforme.admin.entity.request.PageListTableConfigRequest;
import com.mysiteforme.admin.entity.response.PageListTableConfigResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.GenCodeConstants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.TableConfig;
import com.mysiteforme.admin.dao.TableConfigDao;
import com.mysiteforme.admin.service.TableConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TableConfigServiceImpl extends ServiceImpl<TableConfigDao, TableConfig> implements TableConfigService {

    @Override
    public IPage<PageListTableConfigResponse> selectPageTableConfig(PageListTableConfigRequest request) {
        return baseMapper.selectPageTableConfig(new Page<>(request.getPage(),request.getLimit()),request);
    }

    @Override
    public void saveOrUpdateTableConfig(TableConfig tableConfig) {
        // 校验表名是否在sys_table_config表中数据是否存在
        LambdaQueryWrapper<TableConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableConfig::getTableName,tableConfig.getTableName());
        if(tableConfig.getId() != null && tableConfig.getId() != 0){
            wrapper.ne(TableConfig::getId,tableConfig.getId());
        }
        // 校验表名是否在sys_table_config表中数据是否存在
        if(count(wrapper) != 0){
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
        // 检查数据库名称是否存在
        if(!baseMapper.getSchemaNameList().contains(tableConfig.getSchemaName())){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_SCHEMA_NAME_NOT_EXISTS,tableConfig.getSchemaName()).build();
        }
        // 校验表名是否在数据库中
        if(baseMapper.existTable(tableConfig.getTableName(),tableConfig.getSchemaName()) == 0){
            throw MyException.builder().businessError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,tableConfig.getSchemaName(),tableConfig.getTableName()).build();
        }
        // 如何生成路径存在，则校验是否合规
        if(StringUtils.isNotBlank(tableConfig.getGeneratePath())){
            if(ToolUtil.isValidPath(tableConfig.getGeneratePath())){
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
            tableConfig.setTableType(GenCodeConstants.TREE_TABLE_TYPE);
        } else if(new HashSet<>(filedNamesList).containsAll(GenCodeConstants.DATA_TABLE_COMMON_FIELD)){
            tableConfig.setTableType(GenCodeConstants.DATA_TABLE_TYPE);
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
}
