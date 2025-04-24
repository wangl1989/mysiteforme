package com.mysiteforme.admin.util;

import lombok.Getter;

@Getter
public enum JobStatus {
    NORMAL(0, "正常"),
    PAUSED(1, "暂停"),
    COMPLETE(2, "完成"),
    ERROR(3, "错误"),
    BLOCKED(4, "阻塞");

    private final Integer value;

    private final String description;

    JobStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static JobStatus getByValue(int value) {
        for (JobStatus status : JobStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "JobStatus{" +
                "value=" + value +
                ", description='" + description + '\'' +
                '}';
    }
}
