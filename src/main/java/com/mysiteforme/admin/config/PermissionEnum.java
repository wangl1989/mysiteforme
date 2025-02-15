package com.mysiteforme.admin.config;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

//    权限值是将二进制与十进制相互转换来判断的
public enum PermissionEnum {
 
    GET_DEPARTMENT(1, "单位获取", "ROLE_GET_DEPARTMENT", 0x0000000000000001L),
    INSERT_DEPARTMENT(2, "单位增加", "ROLE_INSERT_DEPARTMENT", 0x0000000000000002L),
    UPDATE_DEPARTMENT(3, "单位修改", "ROLE_UPDATE_DEPARTMENT", 0x0000000000000004L),
    DELETE_DEPARTMENT(4, "单位删除", "ROLE_DELETE_DEPARTMENT", 0x0000000000000008L),
    ;
 
    private int id;
 
    private String permissions;
 
    private String permissionNames;
 
    private Long value;
 
    PermissionEnum(int id, String permissions, String permissionNames, Long value) {
        this.id = id;
        this.permissions = permissions;
        this.permissionNames = permissionNames;
        this.value = value;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getPermissions() {
        return permissions;
    }
 
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
 
    public String getPermissionNames() {
        return permissionNames;
    }
 
    public void setPermissionNames(String permissionNames) {
        this.permissionNames = permissionNames;
    }
 
    public Long getValue() {
        return value;
    }
 
    public void setValue(Long value) {
        this.value = value;
    }
 
    public static List<GrantedAuthority> fromCode(Long code) {
        List<GrantedAuthority> list = Lists.newArrayList();
        PermissionEnum[] codes = PermissionEnum.values();
        for (PermissionEnum state : codes) {
            if ((state.getValue() & code) > 0) {
                list.add(new SimpleGrantedAuthority(state.getPermissionNames()));
            }
        }
        return list;
    }
 
    public static List<PermissionEnum> getAuthList(Long code) {
        List<PermissionEnum> list = Lists.newArrayList();
        PermissionEnum[] codes = PermissionEnum.values();
        for (PermissionEnum state : codes) {
            if ((state.getValue() & code) > 0) {
                list.add(state);
            }
        }
        return list;
    }
 
    //  获取权限值
    public static Long getPermissionCode(Integer[] auths) {
        Long code = 0x0000000000000000L;
        PermissionEnum[] codes = PermissionEnum.values();
        for (Integer auth : auths) {
            for (PermissionEnum permissionCode : codes) {
                if (auth.equals(permissionCode.getId())) {
                    code += permissionCode.getValue();
                    break;
                }
            }
        }
        return code;
    }
 
    //    获取权限数组
    public static String[] getAuths(Long code) {
        List<String> lists = Lists.newArrayList();
        PermissionEnum[] codes = PermissionEnum.values();
        for (PermissionEnum state : codes) {
            if ((state.getValue() & code) > 0) {
                lists.add(state.getPermissions());
            }
        }
        return Iterables.toArray(lists,String.class);
    }
 
    //    获取权限值
    public static Long getPermissionCode(String[] auths) {
        Long code = 0x0000000000000000L;
        PermissionEnum[] codes = PermissionEnum.values();
        for (String auth : auths) {
            for (PermissionEnum permissionCode : codes) {
                if (auth.equals(permissionCode.getPermissions())) {
                    code += permissionCode.getValue();
                    break;
                }
            }
        }
        return code;
    }
}
