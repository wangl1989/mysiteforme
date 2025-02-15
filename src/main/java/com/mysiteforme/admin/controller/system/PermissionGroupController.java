package com.mysiteforme.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.DTO.PermissionGroupDTO;
import com.mysiteforme.admin.entity.PermissionGroup;
import com.mysiteforme.admin.service.PermissionGroupService;
import com.mysiteforme.admin.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * PermissionGroup  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Slf4j
@RestController
@RequestMapping("/permission/group")
public class PermissionGroupController extends BaseController{

    @Autowired
    private PermissionGroupService permissionGroupService;


    @GetMapping("list")
    @SysLog("请求PermissionGroup列表数据")
    public Result list(@RequestBody PermissionGroupDTO permissionGroupDTO){
        QueryWrapper<PermissionGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        //if(!map.isEmpty()){
       // }
        IPage<PermissionGroup> pageData = permissionGroupService.page(new Page<>(permissionGroupDTO.getPage(),permissionGroupDTO.getLimit()),wrapper);
        Result result = Result.success(pageData);
        return result;
    }

    @PostMapping("add")
    @SysLog("保存新增PermissionGroup数据")
    public Result add(@RequestBody PermissionGroupDTO permissionGroupDTO){
        permissionGroupService.savePermissionGroupDTO(permissionGroupDTO);
        return Result.success();
    }

    @PostMapping("edit")
    @SysLog("保存编辑数据")
    public Result edit(PermissionGroup permissionGroup){
        if(null == permissionGroup.getId() || 0 == permissionGroup.getId()){
            return Result.idIsNullError();
        }
        permissionGroupService.getById(permissionGroup);
        return Result.success();
    }

    @PostMapping("delete")
    @SysLog("删除数据")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        PermissionGroup permissionGroup = permissionGroupService.getById(id);
        permissionGroup.setDelFlag(true);
        permissionGroupService.updateById(permissionGroup);
        return Result.success();
    }

}