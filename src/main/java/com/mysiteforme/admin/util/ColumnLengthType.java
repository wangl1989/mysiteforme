package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum ColumnLengthType {

    BIT("bit",1),
    BYTE("byte",4),
    SHORT("short", 6),
    CHAR("char", 255),
    VARCHAR("varchar",255),

    DATE("date",0),
    TIME("time", 0),
    DATETIME("datetime", 0),
    YEAR("year", 0),
    TIMESTAMP("timestamp", 0),

    BLOB("blob",0),
    TINYTEXT("tinytext",0),
    TEXT("text",0),
    LONGTEXT("longtext",0),
    DECIMAL("decimal", 10),
    DOUBLE("double",0),
    FLOAT("float", 0),
    TINYINT("tinyint",0),
    SMALLINT("smallint",0),
    MEDIUMINT("mediumint",0),
    INT("int",0),
    BIGINT("bigint", 0),
    LONGBLOB("longblob", 0);

    private final String type;

    private final Integer length;

    ColumnLengthType(String type,Integer length){
        this.type = type;
        this.length = length;
    }

    public static ColumnLengthType getByType(String type) {
        for (ColumnLengthType dbColumnType : ColumnLengthType.values()) {
            if (dbColumnType.getType().equals(type)) {
                return dbColumnType;
            }
        }
        return null;
    }

    // 重写toString方法
    @Override
    public String toString() {
        return "ColumnLengthType{" +
                "type=" + type +
                ", length='" + length +
                '}';
    }
}
