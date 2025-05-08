package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 *
 * 访问记录表
 * </p>
 *
 * @author wangl1989
 * @since 2025-05-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("analytics_visit_logs")
public class VisitLogs extends DataEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID，未登录用户为NULL
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 登录名
     */
    @TableField("login_name")
    private String loginName;

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;
    /**
     * 设备ID
     */
    @TableField("device_id")
    private String deviceId;
    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;
    /**
     * 浏览器UA信息
     */
    @TableField("user_agent")
    private String userAgent;
    /**
     * 来源页面
     */
    @TableField("referrer")
    private String referrer;
    /**
     * 访问页面
     */
    @TableField("entry_page")
    private String entryPage;
    /**
     * 访问时间
     */
    @TableField("visit_time")
    private LocalDateTime visitTime;

    /**
     * 当前页面的标题
     */
    @TableField("title")
    private String title;

    /**
     * 当前页面的屏幕长宽度
     */
    @TableField("screen_size")
    private String screenSize;

    /**
     * 页面停留时间
     */
    @TableField("time_on_page")
    private Integer timeOnPage;

    /**
     * 当前页面请求语言
     */
    @TableField("language")
    private String language;
    /**
     * 国家
     */
    @TableField("country")
    private String country;
    /**
     * 省
     */
    @TableField("province")
    private String province;
    /**
     * 城市
     */
    @TableField("city")
    private String city;
    /**
     * 地区
     */
    @TableField("region")
    private String region;
    /**
     * 系统类型
     */
    @TableField("os")
    private String os;

    /**
     * 浏览器信息
     */
    @TableField("browser")
    private String browser;


}