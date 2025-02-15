package com.mysiteforme.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;
import com.mysiteforme.admin.entity.PermissionButton;
import com.mysiteforme.admin.service.PermissionButtonService;
import com.mysiteforme.admin.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * PermissionButton  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Slf4j
@RestController
@RequestMapping("/admin/system/permission/button")
public class PermissionButtonController extends BaseController{

    @Autowired
    private PermissionButtonService permissionButtonService;


    @GetMapping("list")
    @SysLog("请求PermissionButton列表数据")
    public Result list(@RequestBody PermissionButtonDTO permissionButtonDTO){
        QueryWrapper<PermissionButton> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        //if(!map.isEmpty()){
       // }
        IPage<PermissionButton> pageData = permissionButtonService.page(new Page<>(permissionButtonDTO.getPage(),permissionButtonDTO.getLimit()),wrapper);
        Result result = Result.success(pageData);
        return result;
    }

    @PostMapping("add")
    @SysLog("保存新增PermissionButton数据")
    public Result add(@RequestBody PermissionButtonDTO permissionButtonDTO){
        permissionButtonService.savePermissionButtonDTO(permissionButtonDTO);
        return Result.success();
    }

    @PostMapping("edit")
    @SysLog("保存编辑数据")
    public Result edit(PermissionButton permissionButton){
        if(null == permissionButton.getId() || 0 == permissionButton.getId()){
            return Result.idIsNullError();
        }
        permissionButtonService.getById(permissionButton);
        return Result.success();
    }

    @PostMapping("delete")
    @SysLog("删除数据")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        PermissionButton permissionButton = permissionButtonService.getById(id);
        permissionButton.setDelFlag(true);
        permissionButtonService.updateById(permissionButton);
        return Result.success();
    }

}