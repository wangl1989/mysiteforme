/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:35:48
 * @ Description: 角色控制器 提供角色的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.request.AddRoleRequest;
import com.mysiteforme.admin.entity.request.PageListRoleRequest;
import com.mysiteforme.admin.entity.request.SaveRoleMenuPerRequest;
import com.mysiteforme.admin.entity.request.UpdateRoleRequest;
import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.service.RoleService;
import com.mysiteforme.admin.util.Result;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("list")
    public Result list(PageListRoleRequest request){
        return Result.success(roleService.selectPageUser(request));
    }

    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.ROLE_ADD)
    public Result add(@RequestBody @Valid AddRoleRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(roleService.getRoleNameCount(request.getName())>0){
            return Result.paramMsgError(MessageConstants.Role.ROLE_NAME_HAS_EXIST);
        }
        // 如果前端选择了默认，则判断当前系统中是否有其他角色已经是默认状态了。
        if(request.getIsDefault()){
            if(roleService.getIsDefaultRoleCount(null) > 0){
                return Result.businessMsgError(MessageConstants.Role.ROLE_HAS_OTHER_ROLE_IS_DEFAULT);
            }
        }
        Role role = new Role();
        BeanUtils.copyProperties(request,role);
        roleService.saveRole(role);
        return Result.success();
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.ROLE_EDIT)
    public Result edit(@RequestBody @Valid UpdateRoleRequest request){
        Role oldRole = roleService.getRoleById(request.getId());
        if(oldRole.getIsDefault()){
            Long currentId = MySecurityUser.id();
            if(currentId == null){
                return Result.unauthorized();
            }
            if(1L != currentId) {
                return Result.businessMsgError(MessageConstants.Role.ROLE_CURRENT_ROLE_IS_DEFAULT_UPDATE);
            }
        }
        if(!oldRole.getName().equals(request.getName())){
            if(roleService.getRoleNameCount(request.getName())>0){
                return Result.paramMsgError(MessageConstants.Role.ROLE_NAME_HAS_EXIST);
            }
        }
        // 如果前端选择了默认，则判断当前系统中是否有其他角色已经是默认状态了。
        if(request.getIsDefault()){
            if(roleService.getIsDefaultRoleCount(request.getId()) > 0){
                return Result.businessMsgError(MessageConstants.Role.ROLE_HAS_OTHER_ROLE_IS_DEFAULT);
            }
        }
        Role role = new Role();
        BeanUtils.copyProperties(request,role);
        roleService.updateRole(role);
        return Result.success();
    }

    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.ROLE_DELETE)
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0){
            return Result.idIsNullError();
        }
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取当前用户可分配的角色集合
     * @return Result
     */
    @GetMapping("userAllRole")
    public Result userAllRole(){
        return Result.success(roleService.userAllRole(MySecurityUser.id()));
    }

    /**
     * 根据角色ID获取这个角色对应的菜单和权限集合。
     * @param roleId 角色ID
     * @return Result数据对象
     */
    @GetMapping("getRoleMenusPers")
    public Result getUserRoleMenusPermissions(@RequestParam(value = "roleId",required = false)Long roleId){
        if(roleId == null || roleId == 0){
            return Result.idIsNullError();
        }
        return Result.success(roleService.getUserRoleMenusPermissions(roleId));
    }

    /**
     * 保存角色权限菜单关系
     * @param request 分配参数对象
     * @return Result
     */
    @PostMapping("saveRoleMenusPers")
    @SysLog(MessageConstants.SysLog.PERMISSION_ASSIGN_ROLE)
    public Result saveUserRoleMenusPermissions(@RequestBody @Valid SaveRoleMenuPerRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(request.getMenuIds() == null || request.getMenuIds().isEmpty()){
            return Result.paramMsgError(MessageConstants.Role.ROLE_MUST_ASSIGN_ONE_MENU);
        }
        if(request.getPermissionIds() == null || request.getPermissionIds().isEmpty()){
            return Result.paramMsgError(MessageConstants.Role.ROLE_MUST_ASSIGN_ONE_PERMISSION);
        }
        roleService.assignRoleMenusPermissions(request);
        return Result.success();
    }

}
