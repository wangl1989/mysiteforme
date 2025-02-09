package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 任务执行日志
 * </p>
 *
 * @author wangl
 * @since 2018-01-25
 */
@EqualsAndHashCode(callSuper = true)
@TableName("quartz_task_log")
@Data
@Getter
@Setter
public class QuartzTaskLog extends DataEntity {

    /**
     * 任务ID
     */
	@TableField("job_id")
	private Long jobId;
    /**
     * 定时任务名称
     */
	private String name;
    /**
     * 定制任务执行类
     */
	@TableField("target_bean")
	private String targetBean;
    /**
     * 定时任务执行方法
     */
	@TableField("trget_method")
	private String trgetMethod;
    /**
     * 执行参数
     */
	private String params;
    /**
     * 任务状态
     */
	private Integer status;
    /**
     * 异常消息
     */
	private String error;
    /**
     * 执行时间
     */
	private Integer times;



	@Override
	public String toString() {
		return "QuartzTaskLog{" +
			", jobId=" + jobId +
			", name=" + name +
			", targetBean=" + targetBean +
			", trgetMethod=" + trgetMethod +
			", params=" + params +
			", status=" + status +
			", error=" + error +
			", times=" + times +
			"}";
	}
}
