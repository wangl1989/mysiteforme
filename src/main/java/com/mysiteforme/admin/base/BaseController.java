package com.mysiteforme.admin.base;

import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.realm.AuthRealm.ShiroUser;
import com.mysiteforme.admin.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 基础控制器
 * 提供通用的Controller功能
 */
public class BaseController {
	
	/**
	 * 获取当前登录用户
	 * @return 当前登录的用户对象,未登录返回null
	 */
	public User getCurrentUser() {

		Subject subject = SecurityUtils.getSubject();
		if (subject == null || !subject.isAuthenticated()) {
			return null;
		}
		Object principal = subject.getPrincipal();
		if (principal instanceof ShiroUser) {
			ShiroUser shiroUser = (ShiroUser) principal;
			return userService.getById(shiroUser.getId());
		}
		return null;
	}

	/**
	 * 用户服务接口
	 */
	protected UserService userService;

	/**
	 * 菜单服务接口
	 */
	protected MenuService menuService;

	protected RoleService roleService;

	protected DictService dictService;

	protected RescourceService rescourceService;

	protected TableService tableService;

	protected SiteService siteService;

	protected LogService logService;

	protected BlogArticleService blogArticleService;

	protected BlogChannelService blogChannelService;

	protected BlogCommentService blogCommentService;

	protected BlogTagsService blogTagsService;

	protected UserCacheService userCacheService;

	protected UploadInfoService uploadInfoService;

	protected UploadService localService;

	protected UploadService qiniuService;

	protected UploadService ossService;

}
