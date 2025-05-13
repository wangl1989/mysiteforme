/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:33:55
 * @ Description: 菜单控制器 提供菜单的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.request.AddMenuRequest;
import com.mysiteforme.admin.entity.request.UpdateMenuRequest;
import com.mysiteforme.admin.util.LimitType;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/api/admin/menu")
@RequiredArgsConstructor
@RateLimit(limit = 30, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class MenuController{

    private final MenuService menuService;

    @GetMapping("tree")
    public Result tree(){
        Long userId = MySecurityUser.id();
        if(userId == null || userId == 0){
            return Result.unauthorized();
        }
        return Result.success(menuService.getShowMenuByUser(userId,true));
    }

    @SysLog(MessageConstants.SysLog.MENU_ADD)
    @PostMapping("add")
    public Result add(@RequestBody @Valid AddMenuRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(menuService.getCountByName(request.getName(),null)>0){
            return Result.paramMsgError(MessageConstants.Menu.MENU_NAME_EXISTS);
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(request,menu);
        menuService.saveMenu(menu);
        return Result.success();
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.MENU_UPDATE)
    public Result edit(@RequestBody @Valid UpdateMenuRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(menuService.getCountByName(request.getName(),request.getId())>0){
            return Result.paramMsgError(MessageConstants.Menu.MENU_NAME_EXISTS);
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(request,menu);
        menuService.updateMenu(menu);
        return Result.success();
    }

    @SysLog(MessageConstants.SysLog.MENU_DELETE)
    @DeleteMapping("delete")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(id == null){
            return Result.idIsNullError();
        }
        menuService.deleteMenu(id);
        return Result.success();
    }

}
