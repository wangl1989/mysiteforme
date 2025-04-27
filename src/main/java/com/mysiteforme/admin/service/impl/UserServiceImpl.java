/**
 * @ Author:: wangl
 * @ Create Time:: 2025-02-12 04::02::46
 * @ Modified by:: wangl
 * @ Modified time:: 2025-02-18 00::06::36
 * @ Description:: 用户服务实现类 提供用户的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.PageListUserResponse;
import com.mysiteforme.admin.entity.response.UserDetailResponse;
import com.mysiteforme.admin.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.PermissionVO;
import com.mysiteforme.admin.entity.VO.RoleVO;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.CacheUtils;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.MessageConstants;

@Slf4j
@Service("userService")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

	private final PermissionDao permissionDao;

	private final PasswordEncoder passwordEncoder;

	private final CacheUtils cacheUtils;

	private final UserCacheService userCacheService;

	@Override
	public IPage<PageListUserResponse> selectPageUser(PageListUserRequest request) {
		IPage<PageListUserResponse> page = baseMapper.selectPageUser(new Page<>(request.getPage(),request.getLimit()),request);
		List<PageListUserResponse> newList = page.getRecords().stream().map(user -> {
			// 创建新对象避免修改原始数据
			PageListUserResponse newUser = new PageListUserResponse();
			BeanUtils.copyProperties(user, newUser);
			// 手机号脱敏
			if (StringUtils.isNotBlank(user.getTel())) {
				newUser.setTel(DesensitizedUtil.mobilePhone(user.getTel()));
			}

			// 邮箱脱敏
			if (StringUtils.isNotBlank(user.getEmail())) {
				newUser.setEmail(DesensitizedUtil.email(user.getEmail()));
			}
			return newUser;
		}).collect(Collectors.toList());
		page.setRecords(newList);
		return page;
	}

	@Override
	public UserDetailResponse getUserDetailById(Long id) {
		UserVO user = userCacheService.findUserByIdDetails(id);
		if(user == null){
			throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
		}
		UserDetailResponse response = new UserDetailResponse();
		BeanUtils.copyProperties(user, response);
		return response;
	}

	@Override
	@Caching(put = {
			@CachePut(value = "system::user", key = "'id:'+	#result.id", condition = "#result != null"),
			@CachePut(value = "system::user", key = "'loginName:'+	#request.loginName", condition = "#request.loginName != null"),
			@CachePut(value = "system::user", key = "'email:'+	#request.email", condition = "#request.email != null"),
			@CachePut(value = "system::user", key = "'tel:'+	#request.tel", condition = "#request.tel != null")
	})
	@Transactional(rollbackFor = MyException.class)
	public User saveUser(AddUserRequest request) {
		User user = new User();
		BeanUtils.copyProperties(request,user);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setLocked(false);
		baseMapper.insert(user);
		// 检查当前登录用户是否拥有这些角色
		this.checkUserRole(user.getRoles());
		//保存用户角色关系
		this.saveUserRoles(user.getId(),user.getRoles());
		return user;
	}

	@Caching(evict = {
			@CacheEvict(value = "system::user", key = "'id:'+#result.id", condition = "#result.id != null"),
			@CacheEvict(value = "system::user", key = "'loginName:'+#result.loginName", condition = "#result.loginName != null"),
			@CacheEvict(value = "system::user", key = "'email:'+#result.email", condition = "#result.email != null"),
			@CacheEvict(value = "system::user", key = "'tel:'+#result.tel", condition = "#result.tel != null"),
			@CacheEvict(value = "system::user::details", key = "'loginName:'+#result.loginName", condition = "#result.loginName != null"),
			@CacheEvict(value = "system::user::details", key = "'id:'+#result.id", condition = "#result.id != null"),
			@CacheEvict(value = "system::menu::userMenu", key = "'id:'+#result.id", condition = "#result.id != null"),
	})
	@Override
	public User changePassword(ChangePasswordRequest request) {
		Long currentUserId = MySecurityUser.id();
		if(currentUserId == null){
			throw MyException.builder().unauthorized().build();
		}
		User user = this.getById(currentUserId);
		boolean matches = passwordEncoder.matches(request.getOldPwd(), user.getPassword());
		if(!matches){
			throw MyException.builder().businessError(MessageConstants.User.OLD_PASSWORD_ERROR).build();
		}
		user.setPassword(passwordEncoder.encode(request.getNewPwd()));
		return updateUser(user);
	}

	@Caching(evict = {
			@CacheEvict(value = "system::user", key = "'id:'+#request.id", condition = "#request.id != null"),
			@CacheEvict(value = "system::user", key = "'loginName:'+#request.loginName", condition = "#request.loginName != null"),
			@CacheEvict(value = "system::user", key = "'email:'+#request.email", condition = "#request.email != null"),
			@CacheEvict(value = "system::user", key = "'tel:'+#request.tel", condition = "#request.tel != null"),
			@CacheEvict(value = "system::user::details", key = "'loginName:'+#request.loginName", condition = "#result != null"),
			@CacheEvict(value = "system::user::details", key = "'id:'+#request.id", condition = "#request.id != null"),
			@CacheEvict(value = "system::menu::userMenu", key = "#request.id", condition = "#request.id != null"),
	})
	@Transactional(rollbackFor = Exception.class)
	@Override
	public User updateUser(UpdateUserRequest request){
		User user = baseMapper.selectById(request.getId());
		if(user == null){
			throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
		}
		BeanUtils.copyProperties(request,user);
		Set<BaseRoleRequest> roles = request.getRoleSet();
		user.setRoles(roles.stream().map(r -> new Role(r.getId())).collect(Collectors.toSet()));
		// 检查当前登录用户是否拥有这些角色
		this.checkUserRole(user.getRoles());
		//先解除用户跟角色的关系
		this.dropUserRolesByUserId(user.getId());
		//保存用户角色关系
		this.saveUserRoles(user.getId(),user.getRoles());

		baseMapper.updateById(user);
		log.debug("#result.id的值为{}",user.getId());
		log.debug("#result.loginName的值为{}",user.getLoginName());
		return user;
	}

	private User updateUser(User user){
		// 检查当前登录用户是否拥有这些角色
		this.checkUserRole(user.getRoles());
		//先解除用户跟角色的关系
		this.dropUserRolesByUserId(user.getId());
		//保存用户角色关系
		this.saveUserRoles(user.getId(),user.getRoles());

		baseMapper.updateById(user);
		log.debug("#result.id的值为{}",user.getId());
		log.debug("#result.loginName的值为{}",user.getLoginName());
		return user;
	}

	/**
	 * 保存用户角色关系
	 * @param id 用户ID
	 * @param roleSet 角色集合
	 */
	@Override
	public void saveUserRoles(Long id, Set<Role> roleSet) {
		baseMapper.saveUserRoles(id,roleSet);
	}

	/**
	 * 检查当前登录用户是否拥有这些角色
	 * @param roleSet 前端传来的角色集合
	 */
	private void checkUserRole(Set<Role> roleSet){
		Long currentUserId = MySecurityUser.id();
		if(currentUserId == null){
			throw MyException.builder().unauthorized().build();
		}
		// 如果不是超级管理员，则需要检查用户当前是否拥有这些角色
		if(!currentUserId.equals(1L)) {
			UserVO securityUser = userCacheService.findUserByIdDetails(MySecurityUser.id());
			Set<RoleVO> roles = securityUser.getRoles();
			Set<Long> securityRoleIds = roles.stream().map(RoleVO::getId).collect(Collectors.toSet());
			Set<Long> preAssignedRoleIds = roleSet.stream().map(Role::getId).collect(Collectors.toSet());
			if(!securityRoleIds.containsAll(preAssignedRoleIds)){
				throw MyException.builder().businessError(MessageConstants.User.ASSIGN_ROLE_MORE).build();
			}
		}
	}

	/**
	 * 删除用户的所有角色关系
	 * @param id 用户ID
	 */
	@Override
	public void dropUserRolesByUserId(Long id) {
		baseMapper.dropUserRolesByUserId(id);
	}

	/**
	 * 统计指定参数的用户数量
	 * 可以通过登录名、邮箱、电话查询
	 * @param param 查询参数
	 * @return 用户数量
	 */
	@Override
	public long userCount(String param) {
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("login_name",param).or().eq("email",param).or().eq("tel",param);
		return baseMapper.selectCount(wrapper);
	}

	/**
	 * 删除用户（软删除）
	 * 同时清除多个维度的用户缓存
	 * @param user 要删除的用户对象
	 */
	@Caching(evict = {
			@CacheEvict(value = "system::user", key = "'id:'+#user.id", condition = "#user.id != null"),
			@CacheEvict(value = "system::user", key = "'loginName:'+#user.loginName", condition = "#user.loginName != null"),
			@CacheEvict(value = "system::user", key = "'email:'+#user.email", condition = "#user.email != null"),
			@CacheEvict(value = "system::user", key = "'tel:'+#user.tel", condition = "#user.tel != null"),
			@CacheEvict(value = "system::user::details", key = "'loginName:'+#user.loginName", condition = "#user.loginName != null"),
			@CacheEvict(value = "system::user::details", key = "'id:'+#user.id", condition = "#user.id != null")
	})
	@Transactional(rollbackFor = MyException.class)
	@Override
	public void deleteUser(User user) {
		user.setDelFlag(true);
		// 更新用户del_flag字段
		baseMapper.updateById(user);
		// 删除用户角色关系
		baseMapper.dropUserRolesByUserId(user.getId());
		// 删除用户权限关系
		permissionDao.removeUserPermissionByUserId(user.getId());
		// 清除用户其他相关缓存
		cacheUtils.evictCacheOnUserChange(user.getId());
	}

	/**
	 * 查询用户详细信息
	 * @param name 用户登录名
	 * @return 用户详细信息
	 */
	@Override
	@Cacheable(value = "system::user::details", key = "'loginName:'+#name", unless = "#result == null")
	public UserVO findUserByLoginNameDetails(String name) {
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("login_name", name);
		wrapper.eq("del_flag", false);
		User user = baseMapper.selectOne(wrapper);
		if(user == null){
			throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
		}
		// 检查用户ID是否是超级管理员
		if(user.getId().equals(1L)){
			return userCacheService.getSuperAdminUserDetail(user);
		}else{
			return baseMapper.findUserByLoginNameDetails(name);
		}
	}

	/*
	  给用户分配额外权限
	 */
	@Caching(evict = {
		@CacheEvict(value = "system::user::details", key = "'loginName:'+#request.loginName", condition = "#request.loginName != null"),
		@CacheEvict(value = "system::user::details", key = "'id:'+#request.userId", condition = "#request.userId != null")
	})
	@Transactional(rollbackFor = MyException.class)
	@Override
	public void assignUserPermission(AssignUserPermissionRequest request) {
		User user = baseMapper.selectById(request.getUserId());
		if(user == null || user.getDelFlag()){
			throw MyException.builder().businessError(MessageConstants.Permission.ASSIGN_PERMISSION_USER_NOT_FOUND).build();
		}
		Long currentUserId = MySecurityUser.id();
		if(currentUserId == null){
			throw MyException.builder().unauthorized().build();
		}
		if(!currentUserId.equals(1L)) {
			// 如果不是超级管理员,则需要进行权限检查：当前用户只能分配当前用户拥有的权限。
			// 获取当前登录用户的权限集合
			UserVO securityUser = userCacheService.findUserByIdDetails(MySecurityUser.id());
			Set<PermissionVO> permissionVOList = securityUser.getPermissions();
			// 当前登录用户拥有的权限ID集合
			Set<Long> permissionIds = permissionVOList.stream().map(PermissionVO::getId).collect(Collectors.toSet());
			if(!permissionIds.containsAll(request.getPermissionIds())){
				throw MyException.builder().businessError(MessageConstants.Permission.ASSIGN_PERMISSION_USER_MORE).build();
			}
		}
		// 先移除 用户-权限数据
		permissionDao.removeUserPermissionByUserId(request.getUserId());
		// 再插入数据
		permissionDao.saveUserPermission(request);

	}

	@Override
	public List<Long> getAssinUserPermission(Long userId) {

		return permissionDao.getAssinUserPermission(userId);
	}

}
