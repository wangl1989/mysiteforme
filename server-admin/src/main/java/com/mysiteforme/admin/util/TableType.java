package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum TableType {
    DATA_TABLE(1,"数据表"),
    TREE_TABLE(2,"树形结构表"),
    ASSOCIATED_TABLE(3,"关联表");

    private final Integer code;

    private final String desc;

    TableType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TableType getByCode(Integer code) {
        for (TableType type : TableType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TableType{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
