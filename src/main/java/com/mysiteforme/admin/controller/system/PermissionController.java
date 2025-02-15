/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 22:45:18
 * @ Description:
 */

package com.mysiteforme.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.DTO.PermissionDTO;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *   前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Slf4j
@RestController
@RequestMapping("/admin/system/permission")
public class PermissionController extends BaseController{

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    @SysLog("请求权限列表数据")
    public Result list(@RequestBody PermissionDTO permissionDTO){
        
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        IPage<Permission> pageData = permissionService.page(new Page<>(permissionDTO.getPage(),permissionDTO.getLimit()),wrapper);
        Result result = Result.success(pageData);
        return result;
    }

    @PostMapping("/add")
    @SysLog("保存新增权限数据")
    public Result add(@RequestBody Permission permission){
        permissionService.save(permission);
        return Result.success();
    }

    @PostMapping("/edit")
    @SysLog("保存编辑权限数据")
    public Result edit(@RequestBody Permission permission){
        if(null == permission.getId() || 0 == permission.getId()){
            return Result.idIsNullError();
        }
        permissionService.updateById(permission);
        return Result.success();
    }

    @PostMapping("/delete")
    @ResponseBody
    @SysLog("删除数据")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        Permission permission = permissionService.getById(id);
        permission.setDelFlag(true);
        permissionService.updateById(permission);
        return Result.success();
    }

}