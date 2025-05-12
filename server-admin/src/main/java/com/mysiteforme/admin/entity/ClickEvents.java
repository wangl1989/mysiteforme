package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

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
@TableName("analytics_click_events")
public class ClickEvents extends DataEntity {

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
     * 当前页面地址
     */
    @TableField("page_url")
    private String pageUrl;
    /**
     * 点击元素ID
     */
    @TableField("element_id")
    private String elementId;
    /**
     * 点击元素类名
     */
    @TableField("element_class")
    private String elementClass;

    /**
     * 元素文字内容
     */
    @TableField("element_text")
    private String elementText;

    /**
     * 元素DOM路径
     */
    @TableField("element_path")
    private String elementPath;

    /**
     * 元素链接路径
     */
    @TableField("element_href")
    private String elementHref;
    /**
     * 元素类型(button/link/image等)
     */
    @TableField("event_type")
    private String eventType;
    /**
     * 访问时间
     */
    @TableField("click_time")
    private LocalDateTime clickTime;


}