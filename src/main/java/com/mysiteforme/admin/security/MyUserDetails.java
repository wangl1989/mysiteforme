/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 20:46:26
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 02:24:33
 * @ Description: 实现Security用户的接口
 */

package com.mysiteforme.admin.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.entity.VO.PermissionVO;
import com.mysiteforme.admin.entity.VO.UserVO;

@JsonIgnoreProperties({"enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired",
        "username", "password","myPassword"})
public class MyUserDetails extends UserVO implements UserDetails{


    private String myPassword;


    public MyUserDetails() {
    }

    public MyUserDetails(UserVO user) {
        this.setId(user.getId());
        this.setLoginName(user.getLoginName());
        this.myPassword = user.getPassword();
        this.setRoles(user.getRoles());
        this.setPermissions(user.getPermissions());
        this.setNickName(user.getNickName());
        this.setDelFlag(user.getDelFlag());
    }
 
    @Override
    @JsonDeserialize
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
        Set<PermissionVO> permissions = this.getPermissions();
        for(PermissionVO permission : permissions){
            if(StringUtils.isNotBlank(permission.getPermissionCode())){
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionCode()));
            }
        }
        return authorities;
    }
 
    @Override
    public String getPassword() {
        return this.myPassword;
    }
 
    @Override
    public String getUsername() {
        return this.getLoginName();
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return this.getDelFlag();
    }
    
    
}
