/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:36:47
 * @ Description: 数据表控制器 提供数据表的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.SqlInjectionCheck;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.service.TableConfigService;
import com.mysiteforme.admin.service.TableService;
import com.mysiteforme.admin.util.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mysiteforme.admin.util.GenCodeConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/table")
@RequiredArgsConstructor
@SqlInjectionCheck
public class TableController {

    private final TableService tableService;

    private final TableConfigService tableConfigService;

    /**
     * 分页展示数据表
     * @param request 请求列表页参数
     * @return 返回Result对象
     */
    @GetMapping("list")
    public Result list(@Valid PageListTableRequest request){
        return Result.success(tableService.selectPageTable(request));
    }

    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.TABLE_ADD)
    public Result add(@RequestBody @Valid AddTableRequest request){
        if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
            return Result.businessMsgError(MessageConstants.Table.TABLE_NAME_CAN_NOT_BEGIN_WITH,GenCodeConstants.TABLE_NAME_FILTER_PREFIX.toString());
        }
        List<String> schemaNameList = tableConfigService.getSchemaNameList();
        if(!schemaNameList.contains(request.getSchemaName())){
            return Result.businessMsgError(MessageConstants.Table.TABLE_SCHEMA_NAME_NOT_MATCH,request.getSchemaName());
        }
        if(ArrayUtils.contains(GenCodeConstants.JAVA_KEY_WORKS,request.getTableName())){
            return Result.businessMsgError(MessageConstants.Table.TABLE_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(request.getFieldList() == null || request.getFieldList().isEmpty()){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELDS_NOT_EMPTY);
        }
        if(tableService.existTable(request.getSchemaName(), request.getTableName()) > 0){
            return Result.businessMsgError(MessageConstants.Table.TABLE_EXISTS,request.getTableName());
        }
        List<BaseTableFieldRequest> fieldList = request.getFieldList();
        for (BaseTableFieldRequest f : fieldList) {
            if (ArrayUtils.contains(JAVA_KEY_WORKS, f.getTableName())) {
                return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_NAME_CONTAINS_JAVA_KEYWORDS,request.getTableName());
            }
            try {
                ColumnLengthType dbColumnType = ColumnLengthType.getByType(f.getType().toLowerCase());
                if (dbColumnType == null) {
                    return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_TYPE_INVALID,request.getTableName(),f.getColumnName());
                }
                if (dbColumnType.getLength() == 0) {
                    f.setLength(0);
                } else {
                    if (dbColumnType.getLength() < f.getLength()) {
                        return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_LEGTH_TOO_LONG,f.getColumnName(),f.getLength(),dbColumnType.getLength());
                    }
                }
            } catch (IllegalArgumentException e) {
                return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_TYPE_INVALID,request.getTableName(),f.getColumnName());
            }
        }
        tableService.creatTable(request);
        return Result.success();
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.TABLE_EDIT)
    public Result edit(@RequestBody @Valid UpdateTableRequest request){
        if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getOldTableName()::startsWith)){
            return Result.businessMsgError(MessageConstants.Table.TABLE_NAME_CAN_NOT_BEGIN_WITH,GenCodeConstants.TABLE_NAME_FILTER_PREFIX.toString());
        }
        List<String> schemaNameList = tableConfigService.getSchemaNameList();
        if(!schemaNameList.contains(request.getSchemaName())){
            return Result.businessMsgError(MessageConstants.Table.TABLE_SCHEMA_NAME_NOT_MATCH,request.getSchemaName());
        }
        if(StringUtils.isNotBlank(request.getOldTableName()) && StringUtils.isNotBlank(request.getTableName())){
            if(!request.getOldTableName().equalsIgnoreCase(request.getTableName())){
                if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
                    return Result.businessMsgError(MessageConstants.Table.TABLE_CAN_NOT_CHANGE,request.getTableName());
                }
                if(ArrayUtils.contains(GenCodeConstants.JAVA_KEY_WORKS,request.getTableName())){
                    return Result.businessMsgError(MessageConstants.Table.TABLE_NAME_CONTAINS_JAVA_KEYWORDS);
                }
                if(tableService.existTable(request.getSchemaName(), request.getTableName()) > 0){
                    return Result.businessMsgError(MessageConstants.Table.TABLE_EXISTS,request.getTableName());
                }
                tableService.changeTableName(request);
            }
        }
        if(StringUtils.isNotBlank(request.getTableName()) && StringUtils.isNotBlank(request.getComment())){
            if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
                return Result.businessMsgError(MessageConstants.Table.TABLE_CAN_NOT_CHANGE,request.getTableName());
            }
            tableService.changeTableComment(request);
        }
        return Result.success();
    }

    /**
     * 分页展示字段列表
     * @param request 参数
     * @return Result数据对象
     */
    @GetMapping("getFieldList")
    public Result getFieldList(@Valid PageListTableFieldRequest request){
        try{
            TableType.getByCode(request.getTableType());
        }catch ( IllegalArgumentException e ){
            return Result.businessMsgError(MessageConstants.Table.TABLE_TYPE_INVALID);
        }
        if(tableService.existTable(request.getSchemaName(), request.getTableName()) == 0){
            return Result.businessMsgError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,request.getSchemaName(),request.getTableName());
        }
        return Result.success(tableService.selectPageTableField(request));
    }

    /**
     * 展示全部字段
     * @param request 参数
     * @return Result数据对象
     */
    @GetMapping("showFields")
    public Result showFields(@Valid ShowTableFieldRequest request){
        try{
            TableType.getByCode(request.getTableType());
        }catch ( IllegalArgumentException e ){
            return Result.businessMsgError(MessageConstants.Table.TABLE_TYPE_INVALID);
        }
        if(tableService.existTable(request.getSchemaName(), request.getTableName()) == 0){
            return Result.businessMsgError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,request.getTableName());
        }
        return Result.success(tableService.selectListTableField(request));
    }

    @PostMapping("addField")
    @SysLog(MessageConstants.SysLog.FIELD_ADD)
    public Result addField(@RequestBody @Valid AddTableFieldRequest request){
        if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_CAN_NOT_CHANGE,request.getTableName());
        }
        if(ArrayUtils.contains(GenCodeConstants.JAVA_KEY_WORKS,request.getColumnName())){
            return Result.paramMsgError(MessageConstants.Table.TABLE_FIELD_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(tableService.existTable(request.getSchemaName(), request.getTableName()) == 0){
            return Result.businessMsgError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,request.getTableName());
        }
        try{
            TableType.getByCode(request.getTableType());
        }catch ( IllegalArgumentException e ){
            return Result.businessMsgError(MessageConstants.Table.TABLE_TYPE_INVALID);
        }
        try {
            ColumnLengthType dbColumnType =  ColumnLengthType.getByType(request.getType().toLowerCase());
            if (dbColumnType == null){
                return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_TYPE_INVALID,request.getTableName(),request.getColumnName());
            }
            if(dbColumnType.getLength() == 0) {
                request.setLength(0);
            } else {
                if(dbColumnType.getLength() < request.getLength()){
                    return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_LEGTH_TOO_LONG,request.getColumnName(),request.getLength(),dbColumnType.getLength());
                }
            }
        }catch (IllegalArgumentException e){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_TYPE_INVALID,request.getTableName(),request.getColumnName());
        }
        if(tableService.existTableField(request.getSchemaName(),request.getTableName(),request.getColumnName())>0){
            return Result.businessMsgError(MessageConstants.Table.FIELD_HAS_EXIST);
        }
        if(TableType.DATA_TABLE.getCode().equals(request.getTableType())){
            if(DATA_TABLE_COMMON_FIELD.contains(request.getColumnName())){
                return Result.businessMsgError(MessageConstants.Table.BASE_TABLE_NOT_CONTAINS_COMMON_FIELD,request.getColumnName());
            }
        }
        if(TableType.TREE_TABLE.getCode().equals(request.getTableType())){
            if(TREE_TABLE_COMMON_FIELD.contains(request.getColumnName())){
                return Result.businessMsgError(MessageConstants.Table.TREE_TABLE_NOT_CONTAINS_COMMON_FIELD,request.getColumnName());
            }
        }
        tableService.addColumn(request);
        return Result.success();
    }

    @PutMapping("editField")
    @SysLog(MessageConstants.SysLog.FIELD_EDIT)
    public Result editField(@RequestBody @Valid UpdateTableFieldRequest request){
        if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_CAN_NOT_CHANGE,request.getTableName());
        }
        if(ArrayUtils.contains(GenCodeConstants.JAVA_KEY_WORKS,request.getColumnName())){
            return Result.paramMsgError(MessageConstants.Table.TABLE_FIELD_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(tableService.existTable(request.getSchemaName(), request.getTableName()) == 0){
            return Result.businessMsgError(MessageConstants.TableConfig.TABLE_NOT_EXISTS,request.getTableName());
        }
        try{
            TableType.getByCode(request.getTableType());
        }catch ( IllegalArgumentException e ){
            return Result.businessMsgError(MessageConstants.Table.TABLE_TYPE_INVALID);
        }
        try {
            ColumnLengthType dbColumnType =  ColumnLengthType.getByType(request.getType().toLowerCase());
            if (dbColumnType == null){
                return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_TYPE_INVALID,request.getTableName(),request.getColumnName());
            }
            if(dbColumnType.getLength() == 0) {
                request.setLength(0);
            } else {
                if(dbColumnType.getLength() < request.getLength()){
                    return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_LEGTH_TOO_LONG,request.getColumnName(),request.getLength(),dbColumnType.getLength());
                }
            }
        }catch (IllegalArgumentException e){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_TYPE_INVALID,request.getTableName(),request.getColumnName());
        }
        if(TableType.DATA_TABLE.getCode().equals(request.getTableType())){
            if(DATA_TABLE_COMMON_FIELD.contains(request.getColumnName())){
                return Result.businessMsgError(MessageConstants.Table.BASE_TABLE_NOT_CONTAINS_COMMON_FIELD,request.getColumnName());
            }
        }
        if(TableType.TREE_TABLE.getCode().equals(request.getTableType())){
            if(TREE_TABLE_COMMON_FIELD.contains(request.getColumnName())){
                return Result.businessMsgError(MessageConstants.Table.TREE_TABLE_NOT_CONTAINS_COMMON_FIELD,request.getColumnName());
            }
        }
        tableService.updateColumn(request);
        return Result.success();
    }

    @GetMapping("fieldIsExist")
    public Result fieldIsExist(@Valid FieldIsExistRequest request){
        if(ArrayUtils.contains(GenCodeConstants.JAVA_KEY_WORKS,request.getFieldName())){
            return Result.paramMsgError(MessageConstants.Table.TABLE_FIELD_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(ArrayUtils.contains(GenCodeConstants.JAVA_KEY_WORKS,request.getTableName())){
            return Result.businessMsgError(MessageConstants.Table.TABLE_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(tableService.existTableField(request.getSchemaName(),request.getTableName(),request.getFieldName())>0){
            return Result.businessMsgError(MessageConstants.Table.FIELD_HAS_EXIST);
        }
        return Result.success();
    }


    @DeleteMapping("deleteField")
    @SysLog(MessageConstants.SysLog.FIELD_DELETE)
    public Result deleteField(@Valid DeleteFieldRequest request){
        if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELD_CAN_NOT_DELETE,request.getTableName());
        }
        tableService.dropTableField(request);
        return Result.success();
    }

    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.TABLE_DELETE)
    public Result delete(@RequestParam(value = "tableName",required = false)String tableName){
        if(StringUtils.isBlank(tableName)){
            return Result.paramMsgError(MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY);
        }
        if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(tableName::startsWith)){
            return Result.businessMsgError(MessageConstants.Table.SYSTEM_TABLE_CAN_NOT_DELETE,tableName);
        }
        tableService.dropTable(tableName);
        return Result.success();
    }

}
