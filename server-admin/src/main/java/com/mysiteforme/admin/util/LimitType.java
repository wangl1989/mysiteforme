package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum LimitType {
    IP("按IP限流"),
    USER("按用户ID限流 (需要用户登录)"),
    API("按接口路径限流 (全局，不区分用户/IP");

    private final String desc;

    LimitType(String desc){
        this.desc = desc;
    }
}
