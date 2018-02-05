package com.mysiteforme.admin.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @GetMapping("list")
    @SysLog("跳转角色列表页面")
    public String list(){
        return "admin/system/role/list";
    }

    @PostMapping("list")
    @ResponseBody
    @SysLog("请求角色列表数据")
    public LayerData<Role> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<Role> roleLayerData = new LayerData<>();
        EntityWrapper<Role> roleEntityWrapper = new EntityWrapper<>();
        roleEntityWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("key");
            if(StringUtils.isNotBlank(keys)) {
                roleEntityWrapper.like("name", keys);
            }
        }
        Page<Role> rolePage = roleService.selectPage(new Page<>(page,limit),roleEntityWrapper);
        roleLayerData.setCount(rolePage.getTotal());
        roleLayerData.setData(setUserToRole(rolePage.getRecords()));
        return roleLayerData;
    }

    private List<Role> setUserToRole(List<Role> roles){
        for(Role r : roles){
            if(r.getCreateId() != null && r.getCreateId() != 0){
                User u = userService.findUserById(r.getCreateId());
                if(StringUtils.isBlank(u.getNickName())){
                    u.setNickName(u.getLoginName());
                }
                r.setCreateUser(u);
            }
            if(r.getUpdateId() != null && r.getUpdateId() != 0){
                User u  = userService.findUserById(r.getUpdateId());
                if(StringUtils.isBlank(u.getNickName())){
                    u.setNickName(u.getLoginName());
                }
                r.setUpdateUser(u);
            }
        }
        return roles;
    }

    @GetMapping("add")
    @SysLog("跳转角色新增页面")
    public String add(Model model){
        Map<String,Object> map = Maps.newHashMap();
        map.put("parentId",null);
        map.put("isShow",true);
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
    @SysLog("跳转编辑角色页面")
    public String edit(Long id,Model model){
        Role role = roleService.getRoleById(id);
        StringBuilder menuIds = new StringBuilder();
        if(role != null) {
            Set<Menu> menuSet = role.getMenuSet();
            if (menuSet != null && menuSet.size() > 0) {
                for (Menu m : menuSet) {
                    menuIds.append(m.getId().toString()).append(",");
                }
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("parentId",null);
        map.put("isShow",true);
        List<Menu> menuList = menuService.selectAllMenus(map);
        LOGGER.info(JSONObject.toJSONString(menuList));
        model.addAttribute("role",role);
        model.addAttribute("menuList",menuList);
        model.addAttribute("menuIds",menuIds.toString());
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
        if(roles == null || roles.size()==0){
            return RestResponse.failure("请选择需要删除的角色");
        }
        for (Role r : roles){
            roleService.deleteRole(r);
        }
        return RestResponse.success();
    }


}
