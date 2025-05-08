package com.mysiteforme.admin.entity.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;


/**
 * <p>
 *
 * 新增 访问记录表 表单参数对象
 * </p>
 *
 * @author 昵称
 * @since 2025-05-04
 */
@Data
public class AddVisitLogsRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 昵称
     */
    private String nickName;
    /**
     * 来源页面（从那里点进当前页面的）
     */
    private String referrer;
    /**
     * 当前页面
     */
    @NotBlank(message = "当前页面不能为空")
    private String entryPage;

    /**
     * 当前页面的标题
     */
    private String title;

    /**
     * 当前页面的屏幕长宽度
     */
    private String screenSize;

    /**
     * 页面停留时间
     */
    private Integer timeOnPage;

    /**
     * 当前页面请求语言
     */
    private String language;

    /**
     * 设备ID
     */
    private String deviceId;


}