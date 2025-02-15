package com.mysiteforme.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;
import com.mysiteforme.admin.entity.PermissionPage;
import com.mysiteforme.admin.service.PermissionPageService;
import com.mysiteforme.admin.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * PermissionPage  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Slf4j
@RestController
@RequestMapping("/admin/system/permission/page")
public class PermissionPageController extends BaseController{

    @Autowired
    private PermissionPageService permissionPageService;


    @GetMapping("list")
    @SysLog("请求PermissionPage列表数据")
    public Result list(@RequestBody PermissionPageDTO permissionPageDTO){
        QueryWrapper<PermissionPage> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        //if(!map.isEmpty()){
       // }
        IPage<PermissionPage> pageData = permissionPageService.page(new Page<>(permissionPageDTO.getPage(),permissionPageDTO.getLimit()),wrapper);
        Result result = Result.success(pageData);
        return result;
    }

    @PostMapping("add")
    @SysLog("保存新增PermissionPage数据")
    public Result add(@RequestBody PermissionPageDTO permissionPageDTO){
        permissionPageService.savePermissionPageDTO(permissionPageDTO);
        return Result.success();
    }

    @PostMapping("edit")
    @SysLog("保存编辑数据")
    public Result edit(PermissionPage permissionPage){
        if(null == permissionPage.getId() || 0 == permissionPage.getId()){
            return Result.idIsNullError();
        }
        permissionPageService.getById(permissionPage);
        return Result.success();
    }

    @PostMapping("delete")
    @SysLog("删除数据")
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        PermissionPage permissionPage = permissionPageService.getById(id);
        permissionPage.setDelFlag(true);
        permissionPageService.updateById(permissionPage);
        return Result.success();
    }

}