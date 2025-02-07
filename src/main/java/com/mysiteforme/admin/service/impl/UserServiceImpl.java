package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@Service("userService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

	private final UserCacheServiceImpl userCacheService;

	@Autowired
	public UserServiceImpl(UserCacheServiceImpl userCacheService) {
		// TODO Auto-generated constructor stub
		this.userCacheService = userCacheService;
	}

	/* 这里caching不能添加put 因为添加了总会执行该方法
	 * @see com.mysiteforme.service.UserService#findUserByLoginName(java.lang.String)
	 */
	@Cacheable(value = "user", key = "'user_name_'+#name",unless = "#result == null")
	@Override
	public User findUserByLoginName(String name) {
		// TODO Auto-generated method stub
		Map<String,Object> map = Maps.newHashMap();
		map.put("loginName", name);
		return baseMapper.selectUserByMap(map);
	}




	@Override
	@Caching(put = {
			@CachePut(value = "user", key = "'user_id_'+T(String).valueOf(#result.id)",condition = "#result.id != null and #result.id != 0"),
			@CachePut(value = "user", key = "'user_name_'+#user.loginName", condition = "#user.loginName !=null and #user.loginName != ''"),
			@CachePut(value = "user", key = "'user_email_'+#user.email", condition = "#user.email != null and #user.email != ''"),
			@CachePut(value = "user", key = "'user_tel_'+#user.tel", condition = "#user.tel != null and #user.tel != ''")
	})
	@Transactional(rollbackFor = Exception.class)
	public void saveUser(User user) {
		ToolUtil.entryptPassword(user);
		user.setLocked(false);
		baseMapper.insert(user);
		//保存用户角色关系
		this.saveUserRoles(user.getId(),user.getRoleLists());
		userCacheService.findUserById(user.getId());
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = "user", key = "'user_id_'+T(String).valueOf(#user.id)",condition = "#user.id != null and #user.id != 0"),
			@CacheEvict(value = "user", key = "'user_name_'+#user.loginName", condition = "#user.loginName !=null and #user.loginName != ''"),
			@CacheEvict(value = "user", key = "'user_email_'+#user.email", condition = "#user.email != null and #user.email != ''"),
			@CacheEvict(value = "user", key = "'user_tel_'+#user.tel", condition = "#user.tel != null and #user.tel != ''" ),
	})
	@Transactional(rollbackFor = Exception.class)
	public void updateUser(User user) {
		baseMapper.updateById(user);
		//先解除用户跟角色的关系
		this.dropUserRolesByUserId(user.getId());
		this.saveUserRoles(user.getId(),user.getRoleLists());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveUserRoles(Long id, Set<Role> roleSet) {
		baseMapper.saveUserRoles(id,roleSet);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void dropUserRolesByUserId(Long id) {
		baseMapper.dropUserRolesByUserId(id);
	}

	@Override
	public long userCount(String param) {
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("login_name",param).or().eq("email",param).or().eq("tel",param);
		return baseMapper.selectCount(wrapper);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Caching(evict = {
			@CacheEvict(value = "user", key = "'user_id_'+T(String).valueOf(#user.id)",condition = "#user.id != null and #user.id != 0"),
			@CacheEvict(value = "user", key = "'user_name_'+#user.loginName", condition = "#user.loginName !=null and #user.loginName != ''"),
			@CacheEvict(value = "user", key = "'user_email_'+#user.email", condition = "#user.email != null and #user.email != ''"),
			@CacheEvict(value = "user", key = "'user_tel_'+#user.tel", condition = "#user.tel != null and #user.tel != ''" )
	})
	public void deleteUser(User user) {
		user.setDelFlag(true);
		updateUser(user);
	}

	/**
	 * 查询用户拥有的每个菜单具体数量
     */
	@Override
	public Map<String,Object> selectUserMenuCount() {
		return baseMapper.selectUserMenuCount();
	}

}
