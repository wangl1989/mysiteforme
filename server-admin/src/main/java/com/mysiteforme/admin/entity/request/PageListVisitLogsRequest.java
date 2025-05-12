package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 *
 *  访问记录表 分页列表请求参数对象
 * </p>
 *
 * @author 昵称
 * @since 2025-05-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageListVisitLogsRequest extends BasePageRequest {

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String region;

    /**
     * 系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;
    /**
     * 创建时间排序
     */
    private Boolean sortByCreateDateAsc;


}