package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum AssociatedType {
    DICT(1, "字典类型"),
    TABLE(2, "关联表名");

    private final Integer code;
    private final String desc;

    AssociatedType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据code获取枚举实例的静态方法
    public static AssociatedType getByCode(int code) {
        for (AssociatedType status : AssociatedType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "AssociatedType{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
