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
import com.mysiteforme.admin.service.TableService;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.util.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

import static com.mysiteforme.admin.util.GenCodeConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/table")
@RequiredArgsConstructor
@SqlInjectionCheck
public class TableController{

    private final CreateTableFiles createTableFiles;

    private final TableService tableService;

    private final SiteService siteService;
    @GetMapping("list")
    public Result list(@Valid PageListTableRequest request){
        return Result.success(tableService.selectPageTable(request));
    }

    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.TABLE_ADD)
    public Result add(@RequestBody @Valid AddTableRequest request){
        if(ArrayUtils.contains(JAVA_KEYWORKS,request.getTableName())){
            return Result.businessMsgError(MessageConstants.Table.TABLE_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(request.getFieldList() == null || request.getFieldList().isEmpty()){
            return Result.businessMsgError(MessageConstants.Table.TABLE_FIELDS_NOT_EMPTY);
        }
        if(tableService.existTable(request.getSchemaName(), request.getTableName()) > 0){
            return Result.businessMsgError(MessageConstants.Table.TABLE_EXISTS,request.getTableName());
        }
        tableService.creatTable(request);
        return Result.success();
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.TABLE_EDIT)
    public Result edit(@RequestBody @Valid UpdateTableRequest request){
        if(StringUtils.isNotBlank(request.getOldTableName()) && StringUtils.isNotBlank(request.getTableName())){
            if(!request.getOldTableName().equalsIgnoreCase(request.getTableName())){
                if(GenCodeConstants.TABLE_NAME_FILTER_PREFIX.stream().anyMatch(request.getTableName()::startsWith)){
                    return Result.businessMsgError(MessageConstants.Table.TABLE_CAN_NOT_CHANGE,request.getTableName());
                }
                if(ArrayUtils.contains(JAVA_KEYWORKS,request.getTableName())){
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
        if(ArrayUtils.contains(JAVA_KEYWORKS,request.getColumnName())){
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
        if(ArrayUtils.contains(JAVA_KEYWORKS,request.getColumnName())){
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
        if(ArrayUtils.contains(JAVA_KEYWORKS,request.getFieldName())){
            return Result.paramMsgError(MessageConstants.Table.TABLE_FIELD_NAME_CONTAINS_JAVA_KEYWORDS);
        }
        if(ArrayUtils.contains(JAVA_KEYWORKS,request.getTableName())){
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

    @PostMapping("download")
    @SysLog(MessageConstants.SysLog.DOWNLOAD_JAVA_CODE)
    public RestResponse download(@RequestParam(required = false)String[] baseTables,
            @RequestParam(required = false)String[] treeTables,
            HttpServletResponse response) throws Exception {
        if(baseTables != null && treeTables != null){
            if(baseTables.length == 0 && treeTables.length == 0){
                return RestResponse.failure("数据表不能为空");
            }
        }

        synchronized(this){
            File baseFloder = new File(createTableFiles.baseDic);
            ZipUtil.deleteDir(baseFloder);
            if(baseTables != null && baseTables.length>0){
                createTableFiles.createFile(baseTables,1, siteService.getCurrentSite());
            }
            if(treeTables != null && treeTables.length>0){
                createTableFiles.createFile(treeTables,2, siteService.getCurrentSite());
            }
            File f = new File(createTableFiles.zipFile);
            try {
                if(f.exists() && !f.delete()){
                    log.error("下载JAVA源码---删除文件失败:{}", f.getName());
                }
                cn.hutool.core.util.ZipUtil.zip(createTableFiles.baseDic, createTableFiles.zipFile);
            } catch (SecurityException e) {
                log.error("下载JAVA源码---压缩文件失败:{}", e.getMessage());
            }
            String filename = new String(f.getName().getBytes("GB2312"), "ISO8859-1");
            response.setCharacterEncoding("UTF-8");
            response.setContentLength((int) f.length());
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);

            try (BufferedInputStream br = new BufferedInputStream(Files.newInputStream(f.toPath()));
                 OutputStream out = response.getOutputStream()) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = br.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }

            if(!f.delete()) {
                log.error("删除文件失败:{}", f.getName());
            }
        }
        return RestResponse.success();
    }
}
