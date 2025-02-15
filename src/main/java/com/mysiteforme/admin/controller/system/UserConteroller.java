/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:36:57
 * @ Description: 用户控制器 提供用户的增删改查功能
 */

package com.mysiteforme.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.ShowMenu;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.RoleService;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.ServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.mysiteforme.admin.base.MySecurityUser;


@Controller
@RequestMapping("admin/system/user")
public class UserConteroller extends BaseController{

    public UserConteroller(UserService userService, UserCacheService userCacheService, RoleService roleService, MenuService menuService) {
        this.userService = userService; 
        this.userCacheService = userCacheService;
        this.roleService = roleService;
        this.menuService = menuService;
    }

    @GetMapping("list")
    @SysLog("跳转系统用户列表页面")
    public String list(){
        return "admin/system/user/list";
    }

    @PostMapping("list")
    @ResponseBody
    public LayerData<User> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<User> userLayerData = new LayerData<>();
        QueryWrapper<User> userEntityWrapper = new QueryWrapper<>();
        if(!map.isEmpty()){
            String keys = (String) map.get("key");
            if(StringUtils.isNotBlank(keys)) {
                userEntityWrapper.like("login_name", keys).or().like("tel", keys).or().like("email", keys);
            }
        }
        IPage<User> userPage = userService.page(new Page<>(page,limit),userEntityWrapper);
        userLayerData.setCount((int)userPage.getTotal());
        userLayerData.setData(userPage.getRecords());
        return  userLayerData;
    }

    @GetMapping("add")
    public String add(Model model){
        List<Role> roleList = roleService.selectAll();
        model.addAttribute("roleList",roleList);
        return "admin/system/user/add";
    }

    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增系统用户数据")
    public RestResponse add(@RequestBody  User user){
        if(StringUtils.isBlank(user.getLoginName())){
            return RestResponse.failure("登录名不能为空");
        }
        if(user.getRoles() == null || user.getRoles().isEmpty()){
            return  RestResponse.failure("用户角色至少选择一个");
        }
        if(userService.userCount(user.getLoginName())>0){
            return RestResponse.failure("登录名称已经存在");
        }
        if(StringUtils.isNotBlank(user.getEmail())){
            if(userService.userCount(user.getEmail())>0){
                return RestResponse.failure("该邮箱已被使用");
            }
        }
        if(StringUtils.isNoneBlank(user.getTel())){
            if(userService.userCount(user.getTel())>0){
                return RestResponse.failure("该手机号已被绑定");
            }
        }
        user.setPassword(Constants.DEFAULT_PASSWORD);
        userService.saveUser(user);
        if(user.getId() == null || user.getId() == 0){
            return RestResponse.failure("保存用户信息出错");
        }
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        User user = userCacheService.findUserById(id);
        List<Long> roleIdList = Lists.newArrayList();
        if(user != null) {
            Set<Role> roleSet = user.getRoles();
            if (roleSet != null && !roleSet.isEmpty()) {
                for (Role r : roleSet) {
                    roleIdList.add(r.getId());
                }
            }
        }
        List<Role> roleList = roleService.selectAll();
        model.addAttribute("localuser",user);
        model.addAttribute("roleIds",roleIdList);
        model.addAttribute("roleList",roleList);
        return "admin/system/user/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存系统用户编辑数据")
    public RestResponse edit(@RequestBody  User user){
        if(user.getId() == 0 || user.getId() == null){
            return RestResponse.failure("用户ID不能为空");
        }
        if(StringUtils.isBlank(user.getLoginName())){
            return RestResponse.failure("登录名不能为空");
        }
        if(user.getRoles() == null || user.getRoles().isEmpty()){
            return  RestResponse.failure("用户角色至少选择一个");
        }
        User oldUser = userCacheService.findUserById(user.getId());
        if(StringUtils.isNotBlank(user.getEmail())){
            if(!user.getEmail().equals(oldUser.getEmail())){
                if(userService.userCount(user.getEmail())>0){
                    return RestResponse.failure("该邮箱已被使用");
                }
            }
        }
        if(StringUtils.isNotBlank(user.getLoginName())){
            if(!user.getLoginName().equals(oldUser.getLoginName())) {
                if (userService.userCount(user.getLoginName()) > 0) {
                    return RestResponse.failure("该登录名已存在");
                }
            }
        }
        if(StringUtils.isNotBlank(user.getTel())){
            if(!user.getTel().equals(oldUser.getTel())) {
                if (userService.userCount(user.getTel()) > 0) {
                    return RestResponse.failure("该手机号已经被绑定");
                }
            }
        }
        user.setIcon(oldUser.getIcon());
        userService.updateUser(user);
        return RestResponse.success();
    }

    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除系统用户数据(单个)")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0 || id == 1){
            return RestResponse.failure("参数错误");
        }
        User user = userCacheService.findUserById(id);
        if(user == null){
            return RestResponse.failure("用户不存在");
        }
        userService.deleteUser(user);
        return RestResponse.success();
    }

    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("删除系统用户数据(多个)")
    public RestResponse deleteSome(@RequestBody List<User> users){
        if(users == null || users.isEmpty()){
            return RestResponse.failure("请选择需要删除的用户");
        }
        for (User u : users){
            if(u.getId() == 1){
                return RestResponse.failure("不能删除超级管理员");
            }else{
                userService.deleteUser(u);
            }
        }
        return RestResponse.success();
    }

    /***
     * 获得用户所拥有的菜单列表
     */
    @GetMapping("getUserMenu")
    @ResponseBody
    public List<ShowMenu> getUserMenu(){
        Long userId = MySecurityUser.id();
        return menuService.getShowMenuByUser(userId);
    }

    @GetMapping("userinfo")
    public String toEditMyInfo(Model model){
        Long userId = MySecurityUser.id();
        User user = userCacheService.findUserById(userId);
        model.addAttribute("userinfo",user);
        model.addAttribute("userRole",user.getRoles());
        return "admin/system/user/userInfo";
    }

    @PostMapping("saveUserinfo")
    @SysLog("系统用户个人信息修改")
    @ResponseBody
    public RestResponse saveUserInfo(User user){
        if(user.getId() == 0 || user.getId() == null){
            return RestResponse.failure("用户ID不能为空");
        }
        if(StringUtils.isBlank(user.getLoginName())){
            return RestResponse.failure("登录名不能为空");
        }
        User oldUser = userCacheService.findUserById(user.getId());
        if(StringUtils.isNotBlank(user.getEmail())){
            if(!user.getEmail().equals(oldUser.getEmail())){
                if(userService.userCount(user.getEmail())>0){
                    return RestResponse.failure("该邮箱已被使用");
                }
            }
        }
        if(StringUtils.isNotBlank(user.getTel())){
            if(!user.getTel().equals(oldUser.getTel())) {
                if (userService.userCount(user.getTel()) > 0) {
                    return RestResponse.failure("该手机号已经被绑定");
                }
            }
        }
        user.setRoles(oldUser.getRoles());
        userService.updateUser(user);
        return RestResponse.success();
    }

    @GetMapping("changePassword")
    public String changePassword(){
        return "admin/system/user/changePassword";
    }

    @PostMapping("changePassword")
    @SysLog("用户修改密码")
    @ResponseBody
    public RestResponse changePassword(@RequestParam(value = "oldPwd",required = false)String oldPwd,
                                       @RequestParam(value = "newPwd",required = false)String newPwd,
                                       @RequestParam(value = "confirmPwd",required = false)String confirmPwd){
        if(StringUtils.isBlank(oldPwd)){
            return RestResponse.failure("旧密码不能为空");
        }
        if(StringUtils.isBlank(newPwd)){
            return RestResponse.failure("新密码不能为空");
        }
        if(StringUtils.isBlank(confirmPwd)){
            return RestResponse.failure("确认密码不能为空");
        }
        if(!confirmPwd.equals(newPwd)){
            return RestResponse.failure("确认密码与新密码不一致");
        }
        User user = userCacheService.findUserById(MySecurityUser.id());

        //旧密码不能为空
        String pw = Objects.requireNonNull(ToolUtil.entryptPassword(oldPwd, user.getSalt())).split(",")[0];
        if(!user.getPassword().equals(pw)){
            return RestResponse.failure("旧密码错误");
        }
        user.setPassword(newPwd);
        ToolUtil.entryptPassword(user);
        userService.updateUser(user);
        return RestResponse.success();
    }

}
