package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
public interface UserService extends IService<User> {

	User findUserByLoginName(String name);

	/**
	 * 保存用户信息
	 * 包含密码加密、角色关系保存
	 * @param user 用户对象，包含角色列表
	 */
	void saveUser(User user);

	/**
	 * 更新用户信息
	 * 包含更新角色关系
	 * @param user 用户对象，包含更新后的角色列表
	 */
	void updateUser(User user);

	/**
	 * 保存用户角色关系
	 * @param id 用户ID
	 * @param roleSet 角色集合
	 */
	void saveUserRoles(Long id, Set<Role> roleSet);

	/**
	 * 删除用户的所有角色关系
	 * @param id 用户ID
	 */
	void dropUserRolesByUserId(Long id);

	/**
	 * 统计指定参数的用户数量
	 * 可以通过登录名、邮箱、电话查询
	 * @param param 查询参数
	 * @return 用户数量
	 */
	long userCount(String param);

	/**
	 * 删除用户（软删除）
	 * @param user 要删除的用户对象
	 */
	void deleteUser(User user);

	Map<String,Object> selectUserMenuCount();
}
