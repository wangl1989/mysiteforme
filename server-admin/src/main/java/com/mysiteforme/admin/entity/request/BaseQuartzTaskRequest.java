package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BaseQuartzTaskRequest {

    /**
     * 任务名称
     */
    @NotBlank(message = MessageConstants.QuartzTask.TASK_NAME_NOT_EMPTY)
    private String name;
    /**
     * 任务表达式
     */
    @NotBlank(message = MessageConstants.QuartzTask.TASK_CRON_NOT_EMPTY)
    private String cron;

    @Pattern(regexp = "^[a-zA-Z0-9_\\-.]+$" , message = MessageConstants.QuartzTask.TASK_GROUP_NAME_NOT_MATCH)
    @NotBlank(message = MessageConstants.QuartzTask.TASK_GROUP_NAME_NOT_EMPTY)
    private String groupName;
    /**
     * 执行的类
     */
    @NotBlank(message = MessageConstants.QuartzTask.TASK_TARGET_BEAN_NOT_EMPTY)
    private String targetBean;
    /**
     * 执行方法
     */
    @NotBlank(message = MessageConstants.QuartzTask.TASK_TRGET_METHOD_NOT_EMPTY)
    private String trgetMethod;
    /**
     * 执行参数
     */
    private String params;
    /**
     * 任务状态 0:正常  1：暂停
     */
    @NotNull(message = MessageConstants.QuartzTask.TASK_STATUS_NOT_EMPTY)
    private Integer status;
    /**
     * 任务备注
     */
    @NotBlank(message = MessageConstants.QuartzTask.TASK_REMARKS_NOT_EMPTY)
    private String remarks;
}
