/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:52:58
 * @ Description: 定时任务基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("quartz_task")
@Data
public class QuartzTask extends DataEntity {

    /**
     * 任务名称
     */
	private String name;
    /**
     * 任务表达式
     */
	private String cron;
	/**
	 * 任务组名称
	 */
	@TableField("group_name")
	private String groupName;
    /**
     * 执行的类
     */
	@TableField("target_bean")
	private String targetBean;
    /**
     * 执行方法
     */
	@TableField("trget_method")
	private String trgetMethod;
    /**
     * 执行参数
     */
	private String params;
    /**
     * 任务状态 0:正常  1：暂停
     */
	private Integer status;


	@Override
	public String toString() {
		return "QuartzTask{" +
			", name=" + name +
			", cron=" + cron +
			", targetBean=" + targetBean +
			", trgetMethod=" + trgetMethod +
			", params=" + params +
			", status=" + status +
			"}";
	}
}
