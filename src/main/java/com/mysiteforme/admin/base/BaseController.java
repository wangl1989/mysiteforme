package com.mysiteforme.admin.base;

import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.realm.AuthRealm.ShiroUser;
import com.mysiteforme.admin.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
	@Autowired
	protected UserService userService;

	/**
	 * 菜单服务接口
	 */
	@Autowired
	protected MenuService menuService;

	@Autowired
	protected RoleService roleService;

	@Autowired
	protected DictService dictService;

	@Autowired
	protected RescourceService rescourceService;

	@Autowired
	protected TableService tableService;

	@Autowired
	protected SiteService siteService;

	@Autowired
	protected LogService logService;

	@Autowired
	protected BlogArticleService blogArticleService;

	@Autowired
	protected BlogChannelService blogChannelService;

	@Autowired
	protected BlogCommentService blogCommentService;

	@Autowired
	protected BlogTagsService blogTagsService;

	@Autowired
	protected UserCacheService userCacheService;

	@Autowired
	protected UploadInfoService uploadInfoService;

	@Autowired
	protected @Qualifier("localService")  UploadService localService;

	@Autowired
	protected @Qualifier("qiniuService")  UploadService qiniuService;

	@Autowired
	protected @Qualifier("ossService")  UploadService ossService;

}
