package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum QueryType {
    EQ("eq", "等于"),
    NE("ne", "不等于"),
    GT("gt", "大于"),
    LT("lt", "小于"),
    GTE("gte", "大于等于"),
    LTE("lte", "小于等于"),
    LIKE("like", "模糊匹配"),
    LEFT_LIKE("left_like", "左模糊匹配"),
    RIGHT_LIKE("right_like", "右模糊匹配"),
    BETWEEN("between", "区间匹配"),
    IN("in", "包含"),
    NOT_IN("not_in", "不包含"),
    IS_NULL("is_null", "为空"),
    IS_NOT_NULL("is_not_null", "不为空");

    private final String expression;
    private final String desc;

    QueryType(String expression, String desc) {
        this.expression = expression;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "QueryType{" +
                "expression='" + expression + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
