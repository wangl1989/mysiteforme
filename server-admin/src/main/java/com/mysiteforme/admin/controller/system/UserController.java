/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:36:57
 * @ Description: 用户控制器 提供用户的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.DTO.LocationDTO;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.UpdateCurrentUserRequest;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.LocationResponse;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.util.ToolUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;

import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final MenuService menuService;

    private final UserCacheService userCacheService;

    @GetMapping("list")
    public Result list(PageListUserRequest request){
        return  Result.success(userService.selectPageUser(request));
    }

    @GetMapping("detail")
    public Result detail(@RequestParam(required = false) Long id){
        if(id == null || id == 0){
            return Result.idIsNullError();
        }
        return Result.success(userService.getUserDetailById(id));
    }

    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.USER_ADD)
    public Result add(@RequestBody AddUserRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(request.getRoles() == null || request.getRoles().isEmpty()){
            return  Result.businessMsgError(MessageConstants.User.ROLE_MUST_SELECT_ONE);
        }
        if(userService.userCounByLoginName(request.getLoginName(),null)>0){
            return Result.businessMsgError(MessageConstants.User.LOGIN_NAME_HAS_EXIST);
        }
        if(StringUtils.isNotBlank(request.getEmail())){
            if(userService.userCounByEmail(request.getEmail(),null)>0){
                return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
            }
        }
        if(StringUtils.isNoneBlank(request.getTel())){
            if(userService.userCounByTel(request.getTel(),null)>0){
                return Result.businessMsgError(MessageConstants.User.TEL_HAS_EXIST);
            }
        }
        request.setPassword(Constants.DEFAULT_PASSWORD);
        User user = new User();
        BeanUtils.copyProperties(request,user);
        Set<BaseRoleRequest> rolesRequest = request.getRoles();
        user.setRoles(rolesRequest.stream().map(r -> new Role(r.getId())).collect(Collectors.toSet()));
        UserVO userVO = userService.saveUser(user);
        if(userVO == null || userVO.getId() == null || userVO.getId() == 0){
            return Result.businessMsgError(MessageConstants.User.SAVE_USER_ERROR);
        }
        return Result.success();
    }

    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.USER_EDIT)
    public Result edit(@RequestBody @Valid UpdateUserRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(request.getRoleSet() == null || request.getRoleSet().isEmpty()){
            return  Result.businessMsgError(MessageConstants.User.ROLE_MUST_SELECT_ONE);
        }
        if(StringUtils.isNotBlank(request.getEmail())){
            if(userService.userCounByEmail(request.getEmail(),request.getId())>0){
                return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
            }
        }
        if(StringUtils.isNotBlank(request.getTel())){
            if (userService.userCounByTel(request.getTel(),request.getId()) > 0) {
                return Result.businessMsgError(MessageConstants.User.TEL_HAS_EXIST);
            }
        }
        User user = userService.getById(request.getId());
        if(user == null){
            return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
        }
        request.setLoginName(user.getLoginName());
        BeanUtils.copyProperties(request,user);
        Set<BaseRoleRequest> roles = request.getRoleSet();
        if(!roles.isEmpty()) {
            user.setRoles(roles.stream().map(r -> new Role(r.getId())).collect(Collectors.toSet()));
        }
        userService.updateUser(user);
        return Result.success();
    }

    @PutMapping("editCurrentUser")
    public Result editCurrent(@RequestBody @Valid UpdateCurrentUserRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        Long id = MySecurityUser.id();
        if(id == null){
            return Result.unauthorized();
        }
        if(StringUtils.isNotBlank(request.getEmail())) {
            if(userService.userCounByEmail(request.getEmail(),id)>0){
                return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
            }
        }
        if(StringUtils.isNotBlank(request.getTel())) {
            if (userService.userCounByTel(request.getTel(),id) > 0) {
                return Result.businessMsgError(MessageConstants.User.TEL_HAS_EXIST);
            }
        }
        request.setId(id);
        User user = new User();
        BeanUtils.copyProperties(request,user);
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return result返回结果
     */
    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.USER_DELETE)
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0){
            return Result.idIsNullError();
        }
        if(1L == id){
            return Result.businessMsgError(MessageConstants.User.SYSTEM_USER_CAN_NOT_DELETE);
        }
        User user = userService.getById(id);
        if(user == null){
            return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
        }
        userService.deleteUser(user);
        return Result.success();
    }

    /**
     * 修改密码（个人中心）
     * @param request 密码参数
     * @return result返回结果
     */
    @PostMapping("changePassword")
    @SysLog(MessageConstants.SysLog.USER_CHANGE_PASSWORD)
    @ResponseBody
    public Result changePassword(@RequestBody @Valid ChangePasswordRequest request){
        Long id = MySecurityUser.id();
        if(id == null){
            return Result.unauthorized();
        }
        request.setUserId(MySecurityUser.id());
        userService.changePassword(request);
        return Result.success();
    }

    /**
     * 当前用户
     * @return result返回结果
     */
    @GetMapping("currentUser")
	public Result currentUser(){
		return Result.success(userService.getCurrentUser());
	}

    /**
     * 给用户单独分配权限
     * @param request 单独分配权限参数
     * @return Result 包含当前用户可见菜单列表的Result对象，Result.success表示操作成功
     */
    @PostMapping("assignUserPermission")
    @SysLog(MessageConstants.SysLog.PERMISSION_ASSIGN_USER)
    public Result assignUserPermission(@RequestBody AssignUserPermissionRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        User user = userService.getById(request.getUserId());
        if(user == null || user.getDelFlag()){
            return Result.paramMsgError(MessageConstants.Permission.ASSIGN_PERMISSION_USER_NOT_FOUND);
        }
        userService.assignUserPermission(request);
        return Result.success();
    }


    /**
     * 获取当前用户的菜单信息
     * 该函数通过当前登录用户的ID，调用菜单服务获取该用户可见的菜单列表，并返回成功结果。
     *
     * @return Result 包含当前用户可见菜单列表的Result对象，Result.success表示操作成功
     */
    @GetMapping("currentMenu")
    public Result currentMenu(){
        // 获取当前登录用户的ID
        Long userId = MySecurityUser.id();
        // 根据用户ID获取可见菜单列表，并返回成功结果
        return Result.success(menuService.getShowMenuByUser(userId,false));
    }

    /**
     * 根据用户ID获取已经给他单独分配的权限ID集合
     * @param userId 用户ID
     * @return Result 包含当前用户可见菜单列表的Result对象，Result.success表示操作成功
     */
    @GetMapping("userPermission")
    public Result assinUserPermission(Long userId){
        if(null == userId || 0L == userId){
            return Result.idIsNullError();
        }
        return Result.success(userService.getAssinUserPermission(userId));
    }

    @GetMapping("location")
    public Result getLocation(HttpServletRequest request){
        LocationDTO location = userCacheService.getLocationByIp(ToolUtil.getClientIp(request));
        if(location == null){
            return Result.businessMsgError(MessageConstants.User.GET_LOCATION_ERROR);
        }
        LocationResponse response = new LocationResponse();
        BeanUtils.copyProperties(location,response);
        response.setDistrict(location.getRegion());
        return Result.success(response);
    }

}
