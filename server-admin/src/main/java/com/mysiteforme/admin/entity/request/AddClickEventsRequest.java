package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.entity.ClickEvents;
import lombok.Data;

import java.util.List;

@Data
public class AddClickEventsRequest {

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

    private List<ClickEvents> events;
}
