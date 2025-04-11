/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:02:46
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:31:37
 * @ Description: 用户Service
 */

package com.mysiteforme.admin.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.AssignUserPermission;
import com.mysiteforme.admin.entity.DTO.PermissionForUserDTO;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.PageListUserResponse;
import com.mysiteforme.admin.entity.response.UserDetailResponse;


public interface UserService extends IService<User> {

	/**
	 * 分页获取用户列表
	 * @param request 查询用户参数
	 * @return 分页数据
	 */
	IPage<PageListUserResponse> selectPageUser(PageListUserRequest request);


	/**
	 * 根据ID获取用户详情
	 * @param id 用户ID
	 * @return 用户对象
	 */
	UserDetailResponse getUserDetailById(Long id);

	/**
	 * 根据姓名查询用户详细信息
	 * @param name 用户登录名
	 * @return 用户详细信息
	 */
	UserVO findUserByLoginNameDetails(String name);

	/**
	 * 保存用户信息
	 * 包含密码加密、角色关系保存
	 * @param request 用户对象，包含角色列表
	 */
	User saveUser(AddUserRequest request);


	/**
	 * 更新用户密码
	 * @param request 更新用户密码参数对象
	 */
	User changePassword(ChangePasswordRequest request);

	/**
	 * 更新用户信息
	 * 包含更新角色关系
	 * @param request 请求对象，包含更新后的角色列表
	 */
	User updateUser(UpdateUserRequest request);

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

	// 给用户分配权限
    void assignUserPermission(AssignUserPermissionRequest request);

	/**
	 * 根据用户ID获取他对应的单独分配的权限集合
	 * @param userId
	 * @return
	 */
	List<Long> getAssinUserPermission(Long userId);
}
