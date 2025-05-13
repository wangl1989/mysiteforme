/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 11:35:30
 * @ Description: 权限控制器 提供权限的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.entity.request.AddPermissionRequest;
import com.mysiteforme.admin.entity.request.PageListPermissionRequest;
import com.mysiteforme.admin.entity.request.UpdatePermissionRequest;
import com.mysiteforme.admin.util.LimitType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/admin/permission")
@RequiredArgsConstructor
@RateLimit(limit = 30, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class PermissionController{

    private final PermissionService permissionService;

    private final MenuService menuService;

    @GetMapping("list")
    public Result list(@RequestBody PageListPermissionRequest request){
        return Result.success(permissionService.selectPagePermission(request));
    }

    @SysLog(MessageConstants.SysLog.PERMISSION_ADD)
    @PostMapping("add")
    public Result add(@RequestBody @Valid AddPermissionRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        // 校验权限编码唯一性
        if (!permissionService.checkPermissionCode(null,request.getPermissionCode())) {
            return Result.paramMsgError(MessageConstants.Permission.CODE_EXISTS);
        }
        Menu menu = menuService.getById(request.getMenuId());
        if(menu == null || menu.getDelFlag()){
            return Result.paramMsgError(MessageConstants.Permission.MENU_NOT_FOUND);
        }
        // 保存权限
        permissionService.saveOrUpdatePermission(request);
        return Result.success();
    }

    @SysLog(MessageConstants.SysLog.PERMISSION_UPDATE)
    @PutMapping("edit")
    public Result edit(@RequestBody @Valid UpdatePermissionRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        // 校验权限编码唯一性(排除自身)
        if (!permissionService.checkPermissionCode(request.getId(),request.getPermissionCode())) {
            return Result.error(ResultCode.INVALID_PARAM,MessageConstants.Permission.CODE_EXISTS);
        }
        Menu menu = menuService.getById(request.getMenuId());
        if(menu == null || menu.getDelFlag()){
            return Result.paramMsgError(MessageConstants.Permission.MENU_NOT_FOUND);
        }
        permissionService.saveOrUpdatePermission(request);
        return Result.success();
    }

    @SysLog(MessageConstants.SysLog.PERMISSION_DELETE)
    @DeleteMapping("delete")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        permissionService.deletePermission(id);
        return Result.success();
    }

}