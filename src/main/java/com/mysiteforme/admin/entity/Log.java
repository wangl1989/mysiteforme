package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mysiteforme.admin.base.DataEntity;

/**
 * <p>
 * 系统日志
 * </p>
 *
 * @author wangl
 * @since 2018-01-13
 */
@TableName("sys_log")
public class Log extends DataEntity<Log> {

    private static final long serialVersionUID = 1L;

    /**
     * 请求类型
     */
	private String type;
    /**
     * 日志标题
     */
	private String title;
    /**
     * 操作IP地址
     */
	@TableField("remote_addr")
	private String remoteAddr;
    /**
     * 操作用户昵称
     */
	private String username;
    /**
     * 请求URI
     */
	@TableField("request_uri")
	private String requestUri;
    /**
     * 操作方式
     */
	@TableField("http_method")
	private String httpMethod;
    /**
     * 请求类型.方法
     */
	@TableField("class_method")
	private String classMethod;
    /**
     * 操作提交的数据
     */
	private String params;
    /**
     * sessionId
     */
	@TableField("session_id")
	private String sessionId;
    /**
     * 返回内容
     */
	private String response;
    /**
     * 方法执行时间
     */
	@TableField("use_time")
	private Long useTime;
    /**
     * 浏览器信息
     */
	private String browser;
    /**
     * 地区
     */
	private String area;
    /**
     * 省
     */
	private String province;
    /**
     * 市
     */
	private String city;
    /**
     * 网络服务提供商
     */
	private String isp;
    /**
     * 异常信息
     */
	private String exception;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getClassMethod() {
		return classMethod;
	}

	public void setClassMethod(String classMethod) {
		this.classMethod = classMethod;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Long getUseTime() {
		return useTime;
	}

	public void setUseTime(Long useTime) {
		this.useTime = useTime;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}


	@Override
	public String toString() {
		return "Log{" +
			", type=" + type +
			", title=" + title +
			", remoteAddr=" + remoteAddr +
			", username=" + username +
			", requestUri=" + requestUri +
			", httpMethod=" + httpMethod +
			", classMethod=" + classMethod +
			", params=" + params +
			", sessionId=" + sessionId +
			", response=" + response +
			", useTime=" + useTime +
			", browser=" + browser +
			", area=" + area +
			", province=" + province +
			", city=" + city +
			", isp=" + isp +
			", exception=" + exception +
			"}";
	}
}
