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

import com.mysiteforme.admin.entity.response.PageListUserResponse;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;


public interface UserDao extends BaseMapper<User> {

	List<PageListUserResponse> selectPageUser(@Param("ids")List<Long> ids);

	void saveUserRoles(@Param("userId")Long id, @Param("roleIds")Set<Role> roles);

	void dropUserRolesByUserId(@Param("userId")Long userId);

	@MapKey("")
    Map<String,Object> selectUserMenuCount();

    UserVO findUserDetailById(Long id);

}