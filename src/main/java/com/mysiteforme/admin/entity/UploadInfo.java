package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.mysiteforme.admin.base.DataEntity;

/**
 * <p>
 * 文件上传配置
 * </p>
 *
 * @author wangl
 * @since 2018-07-06
 */
@TableName("upload_info")
public class UploadInfo extends DataEntity<UploadInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 本地window系统上传路径
     */
	@TableField(value = "local_window_url")
	private String localWindowUrl;
    /**
     * 本地LINUX系统上传路径
     */
	@TableField(value = "local_linux_url")
	private String localLinuxUrl;
    /**
     * 七牛前缀路径
     */
	@TableField(value = "qiniu_base_path")
	private String qiniuBasePath;
    /**
     * 七牛bucket的目录名称
     */
	@TableField(value = "qiniu_bucket_name")
	private String qiniuBucketName;
    /**
     * 七牛文件存储目录
     */
	@TableField(value = "qiniu_dir",strategy= FieldStrategy.IGNORED)
	private String qiniuDir;
    /**
     * 七牛qiniuAccess值
     */
	@TableField(value = "qiniu_access_key")
	private String qiniuAccessKey;
    /**
     * 七牛qiniuKey的值
     */
	@TableField(value = "qiniu_secret_key")
	private String qiniuSecretKey;
	/**
	 * 七牛上传测试结果
	 */
	@TableField("qiniu_test_access")
	private Boolean qiniuTestAccess;
    /**
     * 阿里云前缀路径
     */
	@TableField(value = "oss_base_path")
	private String ossBasePath;
    /**
     * 阿里云bucket的目录名称
     */
	@TableField(value = "oss_bucket_name")
	private String ossBucketName;
    /**
     * 阿里云文件上传目录
     */
	@TableField(value = "oss_dir",strategy= FieldStrategy.IGNORED)
	private String ossDir;
    /**
     * 阿里云ACCESS_KEY_ID值
     */
	@TableField(value = "oss_key_id")
	private String ossKeyId;
    /**
     * 阿里云ACCESS_KEY_SECRET
     */
	@TableField(value = "oss_key_secret")
	private String ossKeySecret;
	/**
	 * 阿里云ENDPOINT值
	 */
	@TableField(value = "oss_endpoint")
	private String ossEndpoint;
	/**
	 * 阿里云上传测试结果
	 */
	@TableField(value = "oss_test_access")
	private Boolean ossTestAccess;

	public String getLocalWindowUrl() {
		return localWindowUrl;
	}

	public void setLocalWindowUrl(String localWindowUrl) {
		this.localWindowUrl = localWindowUrl;
	}
	public String getLocalLinuxUrl() {
		return localLinuxUrl;
	}

	public void setLocalLinuxUrl(String localLinuxUrl) {
		this.localLinuxUrl = localLinuxUrl;
	}
	public String getQiniuBasePath() {
		return qiniuBasePath;
	}

	public void setQiniuBasePath(String qiniuBasePath) {
		this.qiniuBasePath = qiniuBasePath;
	}
	public String getQiniuBucketName() {
		return qiniuBucketName;
	}

	public void setQiniuBucketName(String qiniuBucketName) {
		this.qiniuBucketName = qiniuBucketName;
	}
	public String getQiniuDir() {
		return qiniuDir;
	}

	public void setQiniuDir(String qiniuDir) {
		this.qiniuDir = qiniuDir;
	}
	public String getQiniuAccessKey() {
		return qiniuAccessKey;
	}

	public void setQiniuAccessKey(String qiniuAccessKey) {
		this.qiniuAccessKey = qiniuAccessKey;
	}
	public String getQiniuSecretKey() {
		return qiniuSecretKey;
	}

	public void setQiniuSecretKey(String qiniuSecretKey) {
		this.qiniuSecretKey = qiniuSecretKey;
	}
	public String getOssBasePath() {
		return ossBasePath;
	}

	public void setOssBasePath(String ossBasePath) {
		this.ossBasePath = ossBasePath;
	}
	public String getOssBucketName() {
		return ossBucketName;
	}

	public void setOssBucketName(String ossBucketName) {
		this.ossBucketName = ossBucketName;
	}
	public String getOssDir() {
		return ossDir;
	}

	public void setOssDir(String ossDir) {
		this.ossDir = ossDir;
	}
	public String getOssKeyId() {
		return ossKeyId;
	}

	public void setOssKeyId(String ossKeyId) {
		this.ossKeyId = ossKeyId;
	}
	public String getOssKeySecret() {
		return ossKeySecret;
	}

	public void setOssKeySecret(String ossKeySecret) {
		this.ossKeySecret = ossKeySecret;
	}

	public String getOssEndpoint() {
		return ossEndpoint;
	}

	public void setOssEndpoint(String ossEndpoint) {
		this.ossEndpoint = ossEndpoint;
	}

	public Boolean getQiniuTestAccess() {
		return qiniuTestAccess;
	}

	public void setQiniuTestAccess(Boolean qiniuTestAccess) {
		this.qiniuTestAccess = qiniuTestAccess;
	}

	public Boolean getOssTestAccess() {
		return ossTestAccess;
	}

	public void setOssTestAccess(Boolean ossTestAccess) {
		this.ossTestAccess = ossTestAccess;
	}

	@Override
	public String toString() {
		return "UploadInfo{" +
			", localWindowUrl=" + localWindowUrl +
			", localLinuxUrl=" + localLinuxUrl +
			", qiniuBasePath=" + qiniuBasePath +
			", qiniuBucketName=" + qiniuBucketName +
			", qiniuDir=" + qiniuDir +
			", qiniuAccessKey=" + qiniuAccessKey +
			", qiniuSecretKey=" + qiniuSecretKey +
			", ossBasePath=" + ossBasePath +
			", ossBucketName=" + ossBucketName +
			", ossDir=" + ossDir +
			", ossKeyId=" + ossKeyId +
			", ossKeySecret=" + ossKeySecret +
			"}";
	}
}
