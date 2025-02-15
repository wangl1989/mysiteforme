package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.Set;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
	User selectUserByMap(Map<String, Object> map);

	User selectUserByLoginNameDetails(String name);

	void saveUserRoles(@Param("userId")Long id, @Param("roleIds")Set<Role> roles);

	void dropUserRolesByUserId(@Param("userId")Long userId);

	Map<String,Object> selectUserMenuCount();

    UserVO findUserByLoginNameDetails(String name);
}