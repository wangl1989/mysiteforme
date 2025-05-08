/**
 * @ Author:: wangl
 * @ Create Time:: 2025-02-12 04::02::46
 * @ Modified by:: wangl
 * @ Modified time:: 2025-02-18 00::06::36
 * @ Description:: 用户服务实现类 提供用户的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.DTO.LocationDTO;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.AnalyticsUserResponse;
import com.mysiteforme.admin.entity.response.PageListUserResponse;
import com.mysiteforme.admin.entity.response.UserDetailResponse;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.service.RoleService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.UserDeviceService;
import com.mysiteforme.admin.util.ToolUtil;
import jakarta.servlet.http.HttpServletRequest;
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
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.MessageConstants;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service("userService")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

	private final PermissionDao permissionDao;

	private final PasswordEncoder passwordEncoder;

	private final UserCacheService userCacheService;

	private final RoleService roleService;

	private final UserDeviceService userDeviceService;

	@Override
	public IPage<PageListUserResponse> selectPageUser(PageListUserRequest request) {
		LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		if(request != null){
			if(StringUtils.isNotBlank(request.getLoginName())){
				lambdaQueryWrapper.like(User::getLoginName,request.getLoginName());
			}
			if(StringUtils.isNotBlank(request.getLocation())){
				lambdaQueryWrapper.like(User::getLocation,request.getLocation());
			}
			if(StringUtils.isNotBlank(request.getEmail())){
				lambdaQueryWrapper.like(User::getEmail,request.getEmail());
			}
			if(request.getSortByCreateDateAsc() != null){
				lambdaQueryWrapper.orderBy(true,request.getSortByCreateDateAsc(),User::getCreateDate);
			}
			if(request.getSortByUpdateDateAsc() != null){
				lambdaQueryWrapper.orderBy(true,request.getSortByUpdateDateAsc(),User::getUpdateDate);
			}
			if(request.getSortByLoginNameAsc() != null){
				lambdaQueryWrapper.orderBy(true,request.getSortByLoginNameAsc(),User::getLoginName);
			}
		}else{
			request = new PageListUserRequest();
			lambdaQueryWrapper.orderByDesc(User::getUpdateDate);
		}
		IPage<User> userPage = baseMapper.selectPage(new Page<>(request.getPage(),request.getLimit()),lambdaQueryWrapper);
		// 获取主表ID集合
		List<Long> userIds = userPage.getRecords().stream().map(User::getId).toList();
		IPage<PageListUserResponse> result = new Page<>();
		if(!CollectionUtils.isEmpty(userIds)){
			List<PageListUserResponse> page = baseMapper.selectPageUser(userIds);
			List<PageListUserResponse> newList = page.stream().map(user -> {
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
			result.setRecords(newList);
		}
		result.setCurrent(request.getPage());
		result.setSize(request.getLimit());
		result.setTotal(userPage.getTotal());
		return result;
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
	@Caching(put = @CachePut(value = "system::user::details", key = "'id:'+#result.id", condition = "#result != null"),
			evict = @CacheEvict(value = RedisConstants.ANALYTICS_USER_LIST_KEY, key = "'list'" , condition = "#user.id != null")
	)
	@Transactional(rollbackFor = MyException.class)
	public UserVO saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setLocked(false);
		String location = getLocation();
		if(StringUtils.isNotBlank(location)){
			user.setLocation(location);
		}
		baseMapper.insert(user);
		if(user.getCreateId() != null && user.getCreateId() != 0){
			user.setCreateId(user.getId());
			baseMapper.updateById(user);
		}
		Set<Role> roleSet = user.getRoles();
		if(!roleSet.isEmpty()) {
			// 检查当前登录用户是否拥有这些角色
			this.checkUserRole(roleSet);
		}else{
			roleSet = new HashSet<>();
			roleSet.add(roleService.getDefaultRole());
		}
		//保存用户角色关系
		this.saveUserRoles(user.getId(), roleSet);
		return baseMapper.findUserDetailById(user.getId());
	}

	private String getLocation(){
		try {
			HttpServletRequest request = ToolUtil.getCurrentRequest();
			LocationDTO dto = userCacheService.getLocationByIp(ToolUtil.getClientIp(request));
			if(dto != null){
				String location = dto.getProvince();
				if(StringUtils.isNotBlank(dto.getCity())){
					location = location + "," + dto.getCity();
				}
				if(StringUtils.isNotBlank(dto.getRegion())){
					location = location + "," +dto.getRegion();
				}
				return location;
			}
		}catch (Exception e){
			log.error("获取地理位置出现异常:{}",e.getMessage());
			return null;
		}
		return null;
	}

	@Caching(evict = {
			@CacheEvict(value = "system::user::details", key = "'id:'+#request.userId", condition = "#request.userId != null"),
			@CacheEvict(value = "system::menu::userMenu", key = "#request.userId", condition = "#request.userId != null"),
	})
	@Override
	public void changePassword(ChangePasswordRequest request) {
		User user = this.getById(request.getUserId());
		if(user == null){
			throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
		}
		boolean matches = passwordEncoder.matches(request.getOldPwd(), user.getPassword());
		if(!matches){
			throw MyException.builder().businessError(MessageConstants.User.OLD_PASSWORD_ERROR).build();
		}
		user.setPassword(passwordEncoder.encode(request.getNewPwd()));
		baseMapper.updateById(user);
	}

	@Caching(evict = {
			@CacheEvict(value = "system::user::details", key = "'id:'+#user.id", condition = "#user.id != null"),
			@CacheEvict(value = "system::menu::userMenu", key = "#user.id", condition = "#user.id != null"),
	})
	@Override
    public void emailChangePassword(User user){
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		baseMapper.updateById(user);
	}

	@Caching(evict = {
			@CacheEvict(value = "system::user::details", key = "'id:'+#user.id", condition = "#user.id != null"),
			@CacheEvict(value = RedisConstants.ANALYTICS_USER_LIST_KEY, key = "'list'" , condition = "#user.id != null"),
			@CacheEvict(value = "system::menu::userMenu", key = "#user.id", condition = "#user.id != null")
	})
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateUser(User user){
		Set<Role> roles = user.getRoles();
		if(!roles.isEmpty()) {
			// 检查当前登录用户是否拥有这些角色
			this.checkUserRole(user.getRoles());
			//先解除用户跟角色的关系
			this.dropUserRolesByUserId(user.getId());
			//保存用户角色关系
			this.saveUserRoles(user.getId(), user.getRoles());
		}

		baseMapper.updateById(user);
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

	@Override
	public Long userCounByEmail(String email,Long id){
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getEmail,email);
		if(id != null && 0 != id){
			wrapper.ne(User::getId,id);
		}
		return baseMapper.selectCount(wrapper);
	}

	@Override
	public Long userCounByTel(String tel,Long id){
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getTel,tel);
		if(id != null && 0 != id){
			wrapper.ne(User::getId,id);
		}
		return baseMapper.selectCount(wrapper);
	}

	@Override
	public Long userCounByLoginName(String loginName,Long id){
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getLoginName,loginName);
		if(id != null && 0 != id){
			wrapper.ne(User::getId,id);
		}
		return baseMapper.selectCount(wrapper);
	}

	@Override
	public User getUserByEmail(String email) {
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getEmail,email);
		List<User> list = list(wrapper);
		return list.isEmpty()?null:list.get(0);
	}

	/**
	 * 删除用户（软删除）
	 * 同时清除多个维度的用户缓存
	 * @param user 要删除的用户对象
	 */
	@Caching(evict = {
			@CacheEvict(value = "system::user::details", key = "'id:'+#user.id", condition = "#user.id != null"),
			@CacheEvict(value = RedisConstants.ANALYTICS_USER_LIST_KEY ,key = "'list'" , condition = "#user.id != null"),
			@CacheEvict(value = "system::menu::userMenu", key = "#user.id", condition = "#user.id != null")
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
	}

	/**
	 * 查询用户详细信息
	 * @param name 用户登录名
	 * @return 用户详细信息
	 */
	@Override
	public UserVO findUserByLoginNameDetails(String name) {
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		if(name.contains("@")) {
			wrapper.eq(User::getEmail, name);
		}else if(StringUtils.isNumeric(name)) {
			wrapper.eq(User::getTel, name);
		}else {
			wrapper.eq(User::getLoginName,name);
		}
		wrapper.eq(User::getDelFlag, false);
		User user = baseMapper.selectOne(wrapper);
		return userCacheService.findUserByIdDetails(user.getId());
	}

	/*
	  给用户分配额外权限
	 */
	@Caching(evict = {
		@CacheEvict(value = "system::user::details", key = "'id:'+#request.userId", condition = "#request.userId != null"),
		@CacheEvict(value = "system::menu::userMenu", key = "#request.userId", condition = "#request.userId != null")
	})
	@Transactional(rollbackFor = MyException.class)
	@Override
	public void assignUserPermission(AssignUserPermissionRequest request) {
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
	public UserVO getCurrentUser(){
		String loginName = MySecurityUser.loginName();
		if(StringUtils.isBlank(loginName)){
			throw MyException.builder().unauthorized().build();
		}
		UserVO user = this.findUserByLoginNameDetails(loginName);
		user.setPassword(null);
		if(StringUtils.isNotBlank(user.getTel())) {
			user.setTel(DesensitizedUtil.mobilePhone(user.getTel()));
		}
		if(StringUtils.isNotBlank(user.getEmail())) {
			user.setEmail(DesensitizedUtil.email(user.getEmail()));
		}
		UserDevice userDevice = userDeviceService.getCurrentUserDevice();
		if(userDevice != null){
			String lastLoginIp = userDevice.getLastLoginIp();
			if(StringUtils.isNotBlank(lastLoginIp)){
				user.setLastLoginIp(lastLoginIp);
				LocationDTO location =  userCacheService.getLocationByIp(lastLoginIp);
				if(location != null){
					user.setLastLoginLocation(StrUtil.join(",",location.getCountry(),location.getProvince(),location.getCity(),location.getRegion()));
				}
			}
			LocalDateTime lastLoginTime = userDevice.getLastLoginTime();
			if(lastLoginTime != null) {
				user.setLastLoginTime(lastLoginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			}
		}
		return user;
	}

	@Override
	public List<Long> getAssinUserPermission(Long userId) {

		return permissionDao.getAssinUserPermission(userId);
	}

	@Cacheable(value = RedisConstants.ANALYTICS_USER_LIST_KEY, key = "'list'", unless = "#result == null or #result.size() == 0")
	@Override
	public List<AnalyticsUserResponse> getAnalyticsUserResponseList(Integer limit){
		LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(User::getDelFlag,false);
		lambdaQueryWrapper.eq(User::getLocked,false);
		lambdaQueryWrapper.orderByDesc(User::getCreateDate);
		Page<User> userPage = page(new Page<>(1,limit),lambdaQueryWrapper);
		List<User> userList = userPage.getRecords();
		if(!CollectionUtils.isEmpty(userList)){
			return userList.stream().map(u -> {
                AnalyticsUserResponse response = new AnalyticsUserResponse();
                BeanUtils.copyProperties(u,response);
				response.setEmail(DesensitizedUtil.email(response.getEmail()));
				// 设置基础属性为0
				int baseAttr = 0;
				if(StringUtils.isNotBlank(u.getIcon())){
					baseAttr = baseAttr + 1;
				}
				if(StringUtils.isNotBlank(u.getNickName())){
					baseAttr = baseAttr + 1;
				} else if (u.getNickName().startsWith("用户")) {
					baseAttr = baseAttr + 1;
				}
				if(StringUtils.isNotBlank(u.getTel())){
					baseAttr = baseAttr + 1;
				}
				if(StringUtils.isNotBlank(u.getEmail())){
					baseAttr = baseAttr + 1;
				}
				if(StringUtils.isNotBlank(u.getLocation())){
					baseAttr = baseAttr + 1;
				}
				if(StringUtils.isNotBlank(u.getRemarks())){
					baseAttr = baseAttr + 1;
				}
				int percent = (baseAttr * 100) / 6;
				response.setPercent(percent);
                return response;
            }).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

}
