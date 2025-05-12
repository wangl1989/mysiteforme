package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum UploadType {
    LOCAL("local", "本地"),
    QINIU("qiniu", "七牛云"),
    OSS("oss", "阿里云"),
    COS("cos", "腾讯云COS"),
    BITIFUL("bitiful", "滨纷云");
    
    

    private final String code;
    private final String desc;

    UploadType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UploadType getByCode(String code) {
        for (UploadType type : UploadType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "UploadType{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
