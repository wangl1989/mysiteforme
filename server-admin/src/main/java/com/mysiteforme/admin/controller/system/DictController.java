/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:33:18
 * @ Description: 字典控制器 提供字典的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.service.DictService;
import com.mysiteforme.admin.util.LimitType;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/dict")
@RequiredArgsConstructor
@RateLimit(limit = 40, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class DictController {

    private final DictService dictService;

    @SysLog(MessageConstants.SysLog.DICT_DELETE)
    @DeleteMapping("delete")
    public Result deleteById(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0){
            return Result.idIsNullError();
        }
        dictService.deleteDict(id);
        return Result.success();
    }

    @GetMapping("list")
    public Result list(PageListDictRequest request){
        return Result.success(dictService.selectPageDict(request));
    }

    @SysLog(MessageConstants.SysLog.DICT_ADD)
    @PostMapping("add")
    public Result add(@RequestBody @Valid AddDictRequest request) {
        if(dictService.getCountByType(request.getType())==0){
            request.setSort(1);
        }else{
            if(dictService.getCountByAll(request.getType(),request.getLabel(),null,null)>0){
                return Result.businessMsgError(MessageConstants.Dict.DICT_EXISTS_TYPE_AND_LABEL);
            }
            if(dictService.getCountByAll(request.getType(),request.getLabel(),request.getValue(),null)>0){
                return Result.businessMsgError(MessageConstants.Dict.DICT_EXISTS_TYPE_AND_LABEL_AND_VALUE);
            }
            request.setSort(dictService.getMaxSortByType(request.getType(),null)+1);
        }
        dictService.saveDict(request);
        return Result.success();
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.DICT_UPDATE)
    public Result edit(@RequestBody @Valid UpdateDictRequest request) {
        if(request.getSort() == null || request.getSort() <= 1 ){
            request.setSort(dictService.getMaxSortByType(request.getType(),request.getId())+1);
        }
        Dict oldDict = dictService.getById(request.getId());
        if(oldDict==null){
            return Result.businessMsgError(MessageConstants.Dict.DICT_ID_INVALID);
        }
        if(!oldDict.getType().equals(request.getType())){
            return Result.businessMsgError(MessageConstants.Dict.DICT_TYPE_CAN_NOT_UPDATE);
        }
        if(!oldDict.getLabel().equals(request.getLabel())){
            if(dictService.getCountByAll(request.getType(),request.getLabel(),null,request.getId())>0){
                return Result.businessMsgError(MessageConstants.Dict.DICT_EXISTS_TYPE_AND_LABEL);
            }
        }
        if(!oldDict.getValue().equals(request.getValue())) {
            if (dictService.getCountByAll(request.getType(), request.getLabel(), request.getValue(),request.getId()) > 0) {
                return Result.businessMsgError(MessageConstants.Dict.DICT_EXISTS_TYPE_AND_LABEL_AND_VALUE);
            }
        }
        dictService.updateDict(request);
        return Result.success();
    }

    @PutMapping("editType")
    @SysLog(MessageConstants.SysLog.DICT_UPDATE_TYPE)
    public Result editType(@RequestBody @Valid UpdateDictTypeRequest request){
        if(request.getNewType().equals(request.getOldType())){
            return Result.paramMsgError(MessageConstants.Dict.DICT_TYPE_EQUAL);
        }
        if(dictService.getCountByType(request.getNewType())>0){
            return Result.paramMsgError(MessageConstants.Dict.DICT_NEW_TYPE_EXISTS);
        }
        dictService.updateByType(request.getOldType(),request.getNewType());
        return Result.success();
    }

    @GetMapping("getDictTypeList")
    public Result getDictTypeList(){
        return Result.success(dictService.getDictTypeList());
    }

    @GetMapping("getDictListByType")
    public Result getDictListByType(@RequestParam(value = "type",required = false)String type){
        if(StringUtils.isBlank(type)){
            return Result.paramMsgError(MessageConstants.Dict.DICT_TYPE_EMPTY);
        }
        return Result.success(dictService.getDictByType(type));
    }
}
