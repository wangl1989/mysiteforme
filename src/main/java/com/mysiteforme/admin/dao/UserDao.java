/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:45:16
 * @ Description: 用户数据层接口 提供用户的增删改查功能
 */

package com.mysiteforme.admin.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.request.PageListUserRequest;
import com.mysiteforme.admin.entity.response.PageListUserResponse;
import com.mysiteforme.admin.entity.response.UserDetailResponse;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.DTO.PermissionForUserDTO;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;


@Mapper
public interface UserDao extends BaseMapper<User> {

	IPage<PageListUserResponse> selectPageUser(IPage<PageListUserResponse> page, @Param("request")PageListUserRequest request);

	void saveUserRoles(@Param("userId")Long id, @Param("roleIds")Set<Role> roles);

	void dropUserRolesByUserId(@Param("userId")Long userId);

	@MapKey("")
    Map<String,Object> selectUserMenuCount();

    UserVO findUserByLoginNameDetails(String name);

	UserVO findUserByIdDetails(Long id);

}