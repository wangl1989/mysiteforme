/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:36:57
 * @ Description: 用户控制器 提供用户的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.exception.MyException;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import cn.hutool.core.util.DesensitizedUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final MenuService menuService;

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
        if(userService.userCount(request.getLoginName())>0){
            return Result.businessMsgError(MessageConstants.User.LOGIN_NAME_HAS_EXIST);
        }
        if(StringUtils.isNotBlank(request.getEmail())){
            if(userService.userCount(request.getEmail())>0){
                return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
            }
        }
        if(StringUtils.isNoneBlank(request.getTel())){
            if(userService.userCount(request.getTel())>0){
                return Result.businessMsgError(MessageConstants.User.TEL_HAS_EXIST);
            }
        }
        request.setPassword(Constants.DEFAULT_PASSWORD);
        User user = userService.saveUser(request);
        if(user == null || user.getId() == null || user.getId() == 0){
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
        User oldUser = userService.getById(request.getId());
        if(oldUser == null){
            return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
        }
        if(StringUtils.isNotBlank(request.getEmail())){
            if(!request.getEmail().equals(oldUser.getEmail())){
                if(userService.userCount(request.getEmail())>0){
                    return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
                }
            }
        }
        if(StringUtils.isNotBlank(request.getTel())){
            if(!request.getTel().equals(oldUser.getTel())) {
                if (userService.userCount(request.getTel()) > 0) {
                    return Result.businessMsgError(MessageConstants.User.TEL_HAS_EXIST);
                }
            }
        }
        request.setLoginName(oldUser.getLoginName());
        return Result.success(userService.updateUser(request));
    }

    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.USER_DELETE)
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0 || id == 1){
            return Result.idIsNullError();
        }
        User user = userService.getById(id);
        if(user == null){
            return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
        }
        userService.deleteUser(user);
        return Result.success();
    }

    @PostMapping("changePassword")
    @SysLog(MessageConstants.SysLog.USER_CHANGE_PASSWORD)
    @ResponseBody
    public Result changePassword(@RequestBody ChangePasswordRequest request){
        if(!request.getConfirmPwd().equals(request.getNewPwd())){
            return Result.paramMsgError(MessageConstants.User.NEW_PASSWORD_IS_NOT_EQUAL_CONFIRM_PASSWORD);
        }
        userService.changePassword(request);
        return Result.success();
    }

    @GetMapping("currentUser")
	public Result currentUser(){
        UserVO user = userService.findUserByLoginNameDetails(MySecurityUser.loginName());
        user.setPassword(null);
        if(StringUtils.isNotBlank(user.getTel())) {
            user.setTel(DesensitizedUtil.mobilePhone(user.getTel()));
        }
        if(StringUtils.isNotBlank(user.getEmail())) {
            user.setEmail(DesensitizedUtil.email(user.getEmail()));
        }
		return Result.success(user);
	}

    /**
     * 给用户单独分配权限
     * @param request 单独分配权限参数
     * @return Result 包含当前用户可见菜单列表的Result对象，Result.success表示操作成功
     */
    @PostMapping("assignUserPermission")
    public Result assignUserPermission(@RequestBody AssignUserPermissionRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(request.getPermissionIds() == null || request.getPermissionIds().isEmpty()){
            return Result.paramMsgError(MessageConstants.User.ASSIGN_PERMISSION_COLLECTS_EMPTY);
        }
        User user = userService.getById(request.getUserId());
        if(user == null || user.getDelFlag()){
            return Result.paramMsgError(MessageConstants.Permission.ASSIGN_PERMISSION_USER_NOT_FOUND);
        }
        request.setUserName(user.getLoginName());
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
        return Result.success(menuService.getShowMenuByUser(userId));
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

}
