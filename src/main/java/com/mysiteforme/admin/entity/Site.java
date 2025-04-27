/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:53:39
 * @ Description: 系统站点基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@TableName("sys_site")
@Data
public class Site extends DataEntity {

	/**
	 * 网站名称
	 */
	private String name;
	/**
	 * 当前版本
	 */
	private String version;
	/**
	 * 开发作者
	 */
	private String author;
	/**
	 * 作者头像
	 */
	@TableField("author_icon")
	private String authorIcon;
	/**
	 * 文件上传方式
	 */
	@TableField("file_upload_type")
	private String fileUploadType;
	/**
	 * 微博
	 */
	private String weibo;
	/**
	 * qq
	 */
	private String qq;
	/**
	 * gitee地址
	 */
	private String git;
	/**
	 * github地址
	 */
	private String github;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * logo
	 */
	private String logo;
	/**
	 * 服务器
	 */
	private String server;
	/**
	 * 数据库
	 */
	@TableField("my_database")
	private String myDatabase;
	/**
	 * 最大上传文件大小
	 */
	@TableField("max_upload")
	private Integer maxUpload;
	/**
	 * 网站关键字
	 */
	private String keywords;
	/**
	 * 网站描述
	 */
	private String description;
	/**
	 * 版权
	 */
	private String powerby;
	/**
	 * 网站备案
	 */
	private String record;
	/**
	 * 网站网址
	 */
	private String url;

	/**
	 * 是否开放系统评论
	 */
	@TableField("open_message")
	private Boolean openMessage = false;

	/**
	 * 是否匿名评论
	 */
	@TableField("is_no_name")
	private Boolean isNoName = false;

	@Override
	public String toString() {
		return "Site{" +
			", name=" + name +
			", version=" + version +
			", author=" + author +
			", phone=" + phone +
			", email=" + email +
			", fileUploadType=" + fileUploadType +
			", logo=" + logo +
			", server=" + server +
			", myDatabase=" + myDatabase +
			", maxUpload=" + maxUpload +
			", keywords=" + keywords +
			", description=" + description +
			", powerby=" + powerby +
			", record=" + record +
			"}";
	}
}
