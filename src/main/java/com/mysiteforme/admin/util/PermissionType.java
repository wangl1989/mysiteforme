/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:34:07
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:38:31
 * @ Description: 权限类型枚举类 用于定义权限类型
 */

package com.mysiteforme.admin.util;

public enum PermissionType {

    PAGE(1, "页面"),
    BUTTON(2, "按钮"),
    API(3, "API");

    private final Integer value;
    private final String desc;

    PermissionType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    // 根据code获取枚举实例的静态方法
    public static PermissionType getByCode(int value) {
        for (PermissionType status : PermissionType.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    // 重写toString方法
    @Override
    public String toString() {
        return "Status{" +
                "value=" + value +
                ", desc='" + desc +
                '}';
    }
}
