/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:51:32
 * @ Description: 日志基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log")
public class Log extends DataEntity {

    @Serial
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
