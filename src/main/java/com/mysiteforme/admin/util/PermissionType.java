/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:34:07
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 02:15:10
 * @ Description: 权限类型枚举类 用于定义权限类型
 */

package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum PermissionType {

    PAGE(1, "页面"),
    BUTTON(2, "按钮"),
    API(3, "API");



    private final Integer code;
    private final String desc;

    PermissionType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    // 根据code获取枚举实例的静态方法
    public static PermissionType getByCode(int code) {
        for (PermissionType status : PermissionType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    // 重写toString方法
    @Override
    public String toString() {
        return "Status{" +
                "code=" + code +
                ", desc='" + desc +
                '}';
    }
}
