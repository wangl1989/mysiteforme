package com.mysiteforme.admin.util;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public enum FormComponentType {

    INPUT("INPUT", "输入框"),
    INPUT_NUMBER("INPUT_NUMBER", "数字框"),
    TEXTAREA("TEXTAREA", "文本域"),
    SELECT("SELECT", "下拉框"),
    RADIO("RADIO", "单选框"),
    CHECKBOX("CHECKBOX", "复选框"),
    DATE_PICKER("DATE_PICKER", "日期选择器"),
    TIME_PICKER("TIME_PICKER", "时间选择器"),
    DATETIME_PICKER("DATETIME_PICKER", "日期时间选择器"),
    COLOR_PICKER("COLOR_PICKER", "颜色选择器"),
    ICON_PICKER("ICON_PICKER", "图标选择器"),
    SWITCH("SWITCH", "开关"),
    IMAGE_UPLOAD("IMAGE_UPLOAD", "图片上传"),
    FILE_UPLOAD("FILE_UPLOAD", "文件上传"),
    RICH_TEXT("RICH_TEXT", "富文本编辑器"),
    ;

    private final String code;
    private final String desc;

    FormComponentType(String code, String desc) {
        this.code = code;
        this.desc = desc;
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
