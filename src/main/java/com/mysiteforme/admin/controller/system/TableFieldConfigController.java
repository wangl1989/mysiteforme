package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;
import com.mysiteforme.admin.util.ResultCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.TableFieldConfig;
import com.mysiteforme.admin.service.TableFieldConfigService;
import com.mysiteforme.admin.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * TableFieldConfig  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/tableFieldConfig")
@RequiredArgsConstructor
public class TableFieldConfigController{

    private final TableFieldConfigService tableFieldConfigService;

    @DeleteMapping("delete")
    @SysLog("删除数据")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        TableFieldConfig tableFieldConfig = tableFieldConfigService.getById(id);
        tableFieldConfig.setDelFlag(true);
        tableFieldConfigService.updateById(tableFieldConfig);
        return Result.success();
    }

    @GetMapping("getSimpleTableField")
    public Result getSimpleTableField(@Valid GetSimpleFieldRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        return Result.success(tableFieldConfigService.getSimpleTableField(request));
    }

    /**
     * 根据表名获取字段对象集合
     * @param tableConfigId 表配置ID
     * @return 字段对象集合
     */
    @GetMapping("getFieldsByTableConfigId")
    public Result getFieldsByTableConfigId(@RequestParam(name = "tableConfigId",required = false)Long tableConfigId){
        if(tableConfigId == null || tableConfigId == 0){
            return Result.error(ResultCode.INVALID_PARAM,MessageConstants.TableFieldConfig.TABLE_CONFIG_ID_NOT_NULL);
        }
        return Result.success(tableFieldConfigService.getFieldsByTableConfigId(tableConfigId));
    }

    /**
     * 根据表名同步表字段数据
     * @param request 请求参数
     * @return 同步结果
     */
    @PostMapping("syncFieldsByTableName")
    @SysLog(MessageConstants.SysLog.TABLE_FIELD_CONFIG_SYNC)
    public Result syncFieldsByTableName(@RequestBody @Valid SyncTableFieldConfigRequest request){
        boolean result = tableFieldConfigService.syncTableFieldConfig(request);
        if(result){
            return Result.success(MessageUtil.getMessage(MessageConstants.TableFieldConfig.SYNC_SUCCESS));
        }else{
            return Result.businessMsgError(MessageConstants.TableFieldConfig.SYNC_FAILED);
        }
    }

    /**
     * 编辑字段配置
     * @param request 请求参数
     * @return 编辑结果
     */
    @PutMapping("updateFieldConfig")
    @SysLog(MessageConstants.SysLog.TABLE_FIELD_CONFIG_EDIT)
    public Result updateFieldConfig(@RequestBody @Valid UpdateTableFieldConfigRequest request){
        return Result.success(tableFieldConfigService.updateTableFieldConfig(request));
    }

    @PostMapping("sortFields")
    public Result sortFields(@RequestBody SortFieldsRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if (CollectionUtils.isEmpty(request.getIds())) {
            return Result.paramMsgError(MessageConstants.TableFieldConfig.FIELD_IDS_EMPTY);
        }
        tableFieldConfigService.sortFields(request.getIds());
        return Result.success();
    }
}