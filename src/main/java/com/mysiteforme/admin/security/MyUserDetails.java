/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 20:46:26
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 15:32:07
 * @ Description: 实现Security用户的接口
 */

package com.mysiteforme.admin.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mysiteforme.admin.entity.VO.RoleVO;
import com.mysiteforme.admin.entity.VO.UserVO;

@JsonIgnoreProperties({"enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired",
        "username", "password","myPassword"})
public class MyUserDetails extends UserVO implements UserDetails{


    private String myPassword;

    private String deviceId;


    public MyUserDetails() {
    }

    public MyUserDetails(UserVO user) {
        this.setId(user.getId());
        this.setLoginName(user.getLoginName());
        this.setIcon(user.getIcon());
        this.myPassword = user.getPassword();
        this.setRoles(user.getRoles());
        this.setPermissions(user.getPermissions());
        this.setNickName(user.getNickName());
        this.setDelFlag(user.getDelFlag());
    }
 
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<RoleVO> roles = this.getRoles();
        Set<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    
}
