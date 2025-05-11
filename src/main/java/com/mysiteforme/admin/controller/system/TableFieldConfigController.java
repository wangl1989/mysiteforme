package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;
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

    /**
     * 根据表名获取字段对象集合
     * @param request 查询参数对象
     * @return 字段对象集合
     */
    @GetMapping("list")
    public Result getFieldsByTableConfigId(@Valid PageListTableFieldsRequest request){
        return Result.success(tableFieldConfigService.selectPageTableFieldConfig(request));
    }

    @GetMapping("getSimpleTableField")
    public Result getSimpleTableField(@Valid GetSimpleFieldRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        return Result.success(tableFieldConfigService.getSimpleTableField(request));
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
    @SysLog(MessageConstants.SysLog.TABLE_FIELD_CONFIG_SORT)
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

    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.TABLE_FIELD_CONFIG_DELETE)
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        TableFieldConfig tableFieldConfig = tableFieldConfigService.getById(id);
        if(tableFieldConfig == null){
            return Result.businessMsgError(MessageConstants.TableFieldConfig.FIELD_CONFIG_NOT_FOUND);
        }
        tableFieldConfig.setDelFlag(true);
        tableFieldConfigService.updateById(tableFieldConfig);
        return Result.success();
    }
}