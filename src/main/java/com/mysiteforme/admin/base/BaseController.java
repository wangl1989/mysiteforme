/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 22:46:31
 * @ Description: 基础控制器,提供通用的Controller功能
 */

package com.mysiteforme.admin.base;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.*;
import org.springframework.security.authentication.AuthenticationManager;

public class BaseController {

	protected AuthenticationManager authenticationManager;
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

	protected RedisUtils redisUtils;
}
