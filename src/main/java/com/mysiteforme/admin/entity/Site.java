package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mysiteforme.admin.base.DataEntity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2017-12-30
 */
@TableName("sys_site")
public class Site extends DataEntity<Site> {

    private static final long serialVersionUID = 1L;

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
	private String database;
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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorIcon() {
		return authorIcon;
	}

	public void setAuthorIcon(String authorIcon) {
		this.authorIcon = authorIcon;
	}

	public String getFileUploadType() {
		return fileUploadType;
	}

	public void setFileUploadType(String fileUploadType) {
		this.fileUploadType = fileUploadType;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getGit() {
		return git;
	}

	public void setGit(String git) {
		this.git = git;
	}

	public String getGithub() {
		return github;
	}

	public void setGithub(String github) {
		this.github = github;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Integer getMaxUpload() {
		return maxUpload;
	}

	public void setMaxUpload(Integer maxUpload) {
		this.maxUpload = maxUpload;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPowerby() {
		return powerby;
	}

	public void setPowerby(String powerby) {
		this.powerby = powerby;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getOpenMessage() {
		return openMessage;
	}

	public void setOpenMessage(Boolean openMessage) {
		this.openMessage = openMessage;
	}

	public Boolean getNoName() {
		return isNoName;
	}

	public void setNoName(Boolean noName) {
		isNoName = noName;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

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
			", database=" + database +
			", maxUpload=" + maxUpload +
			", keywords=" + keywords +
			", description=" + description +
			", powerby=" + powerby +
			", record=" + record +
			"}";
	}
}
