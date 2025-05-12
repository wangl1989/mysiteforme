package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum UserStatusType {

    ONLINE(1,"在线"),
    OFFLINE(2,"离线"),
    NORMAL(3,"正常"),
    DEVICE_LOCKED(4,"设备锁定"),
    ACCOUNT_LOCKED(5,"账号锁定"),
    DELED(6,"注销");

    private final Integer code;

    private final String desc;

    UserStatusType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatusType getByCode(int code) {
        for (UserStatusType status : UserStatusType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "UserStatusType{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
