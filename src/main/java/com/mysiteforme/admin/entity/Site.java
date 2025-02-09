package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2017-12-30
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_site")
@Data
@Getter
@Setter
public class Site extends DataEntity {

	private String name;
	private String version;
	private String author;
	@TableField("author_icon")
	private String authorIcon;
	@TableField("file_upload_type")
	private String fileUploadType;
	private String weibo;
	private String qq;
	private String git;
	private String github;
	private String phone;
	private String email;
	private String address;
	private String logo;
	private String server;
	@TableField("my_database")
	private String myDatabase;
	@TableField("max_upload")
	private Integer maxUpload;
	private String keywords;
	private String description;
	private String powerby;
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
