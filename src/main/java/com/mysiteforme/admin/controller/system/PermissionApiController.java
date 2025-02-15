package com.mysiteforme.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;
import com.mysiteforme.admin.entity.PermissionApi;
import com.mysiteforme.admin.service.PermissionApiService;
import com.mysiteforme.admin.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * PermissionApi  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Slf4j
@RestController
@RequestMapping("/admin/system/permission/api")
public class PermissionApiController extends BaseController{

    @Autowired
    private PermissionApiService permissionApiService;


    @GetMapping("list")
    @SysLog("请求PermissionApi列表数据")
    public Result list(@RequestBody PermissionApiDTO permissionApiDTO){
        QueryWrapper<PermissionApi> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        //if(!map.isEmpty()){
       // }
        IPage<PermissionApi> pageData = permissionApiService.page(new Page<>(permissionApiDTO.getPage(),permissionApiDTO.getLimit()),wrapper);
        Result result = Result.success(pageData);
        return result;
    }

    @PostMapping("add")
    @SysLog("保存新增PermissionApi数据")
    public Result add(@RequestBody PermissionApiDTO permissionApiDTO){
        permissionApiService.savePermissionApiDTO(permissionApiDTO);
        return Result.success();
    }

    @PostMapping("edit")
    @SysLog("保存编辑数据")
    public Result edit(PermissionApi permissionApi){
        if(null == permissionApi.getId() || 0 == permissionApi.getId()){
            return Result.idIsNullError();
        }
        permissionApiService.getById(permissionApi);
        return Result.success();
    }

    @PostMapping("delete")
    @SysLog("删除数据")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        PermissionApi permissionApi = permissionApiService.getById(id);
        permissionApi.setDelFlag(true);
        permissionApiService.updateById(permissionApi);
        return Result.success();
    }

}