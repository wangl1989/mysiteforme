/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:45
 * @ Description: 表服务实现类 提供表的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.dao.TableConfigDao;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.BaseTableFieldResponse;
import com.mysiteforme.admin.entity.response.BaseTableResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.TableService;
import com.mysiteforme.admin.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableConfigDao tableConfigDao;

    @Override
    public IPage<BaseTableResponse> selectPageTable(PageListTableRequest request) {
        IPage<BaseTableResponse> page = tableConfigDao.selectPageTable(new Page<>(request.getPage(),request.getLimit()),request);
        List<BaseTableResponse> list = page.getRecords();
        list = list.stream()
                .peek(t -> {
                    if(!t.getHasDelFlag()){
                        t.setTableType(TableType.ASSOCIATED_TABLE.getCode());
                    }else{
                        if(t.getHasParentId()){
                            t.setTableType(TableType.TREE_TABLE.getCode());
                        }else{
                            t.setTableType(TableType.DATA_TABLE.getCode());
                        }
                    }
                }).toList();
        page.setRecords(list);
        return page;
    }

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 存在返回1，不存在返回0
     */
    @Override
    public Integer existTable(String schemaName, String tableName) {
        return tableConfigDao.existTable(tableName,schemaName);
    }

    @Override
    public void creatTable(AddTableRequest request) {
        try {
            TableType.getByCode(request.getTableType());
        }catch ( IllegalArgumentException e ){
            throw MyException.builder().businessError(MessageConstants.Table.TABLE_TYPE_INVALID).build();
        }

        tableConfigDao.creatTable(request);
    }

    @Override
    public void changeTableName(UpdateTableRequest request) {
        tableConfigDao.changeTableName(request);
    }

    @Override
    public void changeTableComment(UpdateTableRequest request) {
        tableConfigDao.changeTableComment(request);
    }

    @Override
    public IPage<BaseTableFieldResponse> selectPageTableField(PageListTableFieldRequest request) {
        return tableConfigDao.selectPageTableField(new Page<>(request.getPage(),request.getLimit()),request);
    }

    @Override
    public List<BaseTableFieldResponse> selectListTableField(ShowTableFieldRequest request) {
        return tableConfigDao.selectPageTableField(request);
    }

    @Override
    public void addColumn(AddTableFieldRequest request) {
        tableConfigDao.addColumn(request);
    }

    @Override
    public void updateColumn(UpdateTableFieldRequest request) {
        if(request.getOldName().equals(request.getColumnName())){
            tableConfigDao.updateColumnSameName(request);
        }else{
            if(this.existTableField(request.getSchemaName(),request.getTableName(),request.getColumnName())>0){
                throw MyException.builder().businessError(MessageConstants.Table.FIELD_HAS_EXIST).build();
            }
            tableConfigDao.updateColumnDiffName(request);
        }
    }

    @Override
    public Integer existTableField(String schemaName, String tableName, String fieldName) {
        return tableConfigDao.existTableField(schemaName,tableName,fieldName);
    }

    @Override
    public void dropTableField(DeleteFieldRequest request) {
        tableConfigDao.dropTableField(request.getTableName(),request.getFieldName());
    }

    /**
     * 删除数据表
     * @param tableName 表名
     */
    @Override
    public void dropTable(String tableName){
        tableConfigDao.dropTable(tableName);
    }

}
