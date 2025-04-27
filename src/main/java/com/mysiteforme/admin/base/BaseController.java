/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 22:46:31
 * @ Description: 基础控制器,提供通用的Controller功能
 */

package com.mysiteforme.admin.base;
import com.mysiteforme.admin.service.*;

public class BaseController {


	protected SiteService siteService;

	protected BlogArticleService blogArticleService;

	protected BlogChannelService blogChannelService;

	protected BlogCommentService blogCommentService;

	protected BlogTagsService blogTagsService;

}
