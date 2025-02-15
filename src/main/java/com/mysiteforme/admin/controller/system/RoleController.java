package com.mysiteforme.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.service.RoleService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.ServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangl on 2017/12/2.
 * todo:
 */
@Controller
@RequestMapping("admin/system/role")
public class RoleController extends BaseController{

    public RoleController(RoleService roleService, UserCacheService userCacheService, MenuService menuService) {
        this.roleService = roleService;
        this.userCacheService = userCacheService;
        this.menuService = menuService;
    }

    @GetMapping("list")
    @SysLog("跳转角色列表页面")
    public String list(){
        return "admin/system/role/list";
    }

    @PostMapping("list")
    @ResponseBody
    public LayerData<Role> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                ServletRequest request){
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<Role> roleLayerData = new LayerData<>();
        QueryWrapper<Role> roleEntityWrapper = new QueryWrapper<>();
        roleEntityWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("key");
            if(StringUtils.isNotBlank(keys)) {
                roleEntityWrapper.like("name", keys);
            }
        }
        IPage<Role> rolePage = roleService.page(new Page<>(page,limit),roleEntityWrapper);
        long total = rolePage.getTotal();
        roleLayerData.setCount((int) total);
        roleLayerData.setData(setUserToRole(rolePage.getRecords()));
        return roleLayerData;
    }

    private List<Role> setUserToRole(List<Role> roles){
        for(Role r : roles){
            if(r.getCreateId() != null && r.getCreateId() != 0){
                User u = userCacheService.findUserById(r.getCreateId());
                if(StringUtils.isBlank(u.getNickName())){
                    u.setNickName(u.getLoginName());
                }
                r.setCreateUser(u);
            }
            if(r.getUpdateId() != null && r.getUpdateId() != 0){
                User u  = userCacheService.findUserById(r.getUpdateId());
                if(StringUtils.isBlank(u.getNickName())){
                    u.setNickName(u.getLoginName());
                }
                r.setUpdateUser(u);
            }
        }
        return roles;
    }

    @GetMapping("add")
    public String add(Model model){
        Map<String,Object> map = Maps.newHashMap();
        map.put("parentId",null);
        map.put("isShow",false);
        List<Menu> menuList = menuService.selectAllMenus(map);
        model.addAttribute("menuList",menuList);
        return "admin/system/role/add";
    }

    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增角色数据")
    public RestResponse add(@RequestBody Role role){
        if(StringUtils.isBlank(role.getName())){
            return RestResponse.failure("角色名称不能为空");
        }
        if(roleService.getRoleNameCount(role.getName())>0){
            return RestResponse.failure("角色名称已存在");
        }
        roleService.saveRole(role);
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        Role role = roleService.getRoleById(id);
        List<Long> menuIds = Lists.newArrayList();
        if(role != null) {
            Set<Menu> menuSet = role.getMenuSet();
            if (menuSet != null && !menuSet.isEmpty()) {
                for (Menu m : menuSet) {
                    menuIds.add(m.getId());
                }
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("parentId",null);
        map.put("isShow",false);
        List<Menu> menuList = menuService.selectAllMenus(map);
        model.addAttribute("role",role);
        model.addAttribute("menuList",menuList);
        model.addAttribute("menuIds",menuIds);
        return "admin/system/role/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑角色数据")
    public RestResponse edit(@RequestBody Role role){
        if(role.getId() == null || role.getId() == 0){
            return RestResponse.failure("角色ID不能为空");
        }
        if(StringUtils.isBlank(role.getName())){
            return RestResponse.failure("角色名称不能为空");
        }
        Role oldRole = roleService.getRoleById(role.getId());
        if(!oldRole.getName().equals(role.getName())){
            if(roleService.getRoleNameCount(role.getName())>0){
                return RestResponse.failure("角色名称已存在");
            }
        }
        roleService.updateRole(role);
        return RestResponse.success();
    }

    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除角色数据")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0){
            return RestResponse.failure("角色ID不能为空");
        }
        Role role = roleService.getRoleById(id);
        roleService.deleteRole(role);
        return RestResponse.success();
    }

    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除角色数据")
    public RestResponse deleteSome(@RequestBody List<Role> roles){
        if(roles == null || roles.isEmpty()){
            return RestResponse.failure("请选择需要删除的角色");
        }
        for (Role r : roles){
            roleService.deleteRole(r);
        }
        return RestResponse.success();
    }


}
