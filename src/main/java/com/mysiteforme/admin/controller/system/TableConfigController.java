package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.TableConfig;
import com.mysiteforme.admin.service.TableConfigService;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * TableConfig  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/tableConfig")
@RequiredArgsConstructor
public class TableConfigController {

    private final TableConfigService tableConfigService;

    @GetMapping("list")
    public Result list(PageListTableConfigRequest request){
        return Result.success(tableConfigService.selectPageTableConfig(request));
    }

    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.TABLE_CONFIG_ADD)
    public Result add(@RequestBody @Valid AddTableConfigRequest request){
        return saveOrUpdate(request);
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.TABLE_CONFIG_UPDATE)
    public Result edit(@RequestBody @Valid UpdateTableConfigRequest request){
        return saveOrUpdate(request);
    }

    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.TABLE_CONFIG_DELETE)
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        tableConfigService.deleteTableConfig(id);
        return Result.success();
    }

    /**
     * 恢复删除的配置
     * @param id 配置ID
     * @return 返回执行结果
     */
    @PostMapping("recover")
    @SysLog(MessageConstants.SysLog.TABLE_CONFIG_RECOVER)
    public Result recover(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        tableConfigService.recoverTableConfig(id);
        return Result.success();
    }

    /**
     * 获取表名列表
     * @param schemaName 模式名
     * @return 表名列表
     */
    @GetMapping("getTableNameList")
    public Result getTableNameList(@RequestParam(value = "schemaName",required = false)String schemaName){
        if(StringUtils.isBlank(schemaName)){
            return Result.paramMsgError(MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY);
        }
        return Result.success(tableConfigService.getTableNameList(schemaName));
    }

    /**
     * 获取模式名列表
     * @return 模式名列表
     */
    @GetMapping("getSchemaNameList")
    public Result getSchemaNameList(){
        return Result.success(tableConfigService.getSchemaNameList());
    }

    public Result saveOrUpdate(BaseTableConfigRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(StringUtils.isNotBlank(request.getTablePrefix())) {
            if (!request.getTableName().startsWith(request.getTablePrefix())) {
                return Result.businessMsgError(MessageConstants.TableConfig.TABLE_PREFIX_NOT_MATCH);
            }
        }
        if(StringUtils.isNotBlank(request.getGeneratePath())){
            if(!ToolUtil.isValidPath(request.getGeneratePath())){
                return Result.businessMsgError(MessageConstants.TableConfig.PATH_NOT_VALID_BY_SYSTEM);
            }
        }
        if(StringUtils.isBlank(request.getAuthor())){
            request.setAuthor(MySecurityUser.nickName());
        }
        TableConfig tableConfig = new TableConfig();
        BeanUtils.copyProperties(request,tableConfig);
        tableConfigService.saveOrUpdateTableConfig(tableConfig);
        return Result.success();
    }

    @PostMapping("downloadCode")
    @SysLog(MessageConstants.SysLog.DOWNLOAD_JAVA_CODE)
    public Result download(@RequestBody DownloadCodeRequest request, HttpServletResponse response) {
        if(request == null){
            throw MyException.builder().businessError(MessageConstants.OBJECT_NOT_NULL).build();
        }
        if (CollectionUtils.isEmpty(request.getIds())) {
            throw MyException.builder().businessError(MessageConstants.Validate.VALIDATE_ID_ERROR).build();
        }
        tableConfigService.downloadCode(request.getIds(),response);
        return Result.success();
    }

}