/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:53:57
 * @ Description: 文件上传配置基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@TableName("sys_upload_base_info")
@Data
public class UploadBaseInfo extends DataEntity {

	/**
	 * 类型
	 */
	@TableField(value = "type")
	private String type;

    /**
     * 前缀路径，例子如下:
	 * (https://weizheng0301-1257429161.cos.ap-shanghai.myqcloud.com/)
	 * (https://weizhengzs.s3.bitiful.net/)
	 * (https://weizheng0301-1257429161.cos.ap-shanghai.myqcloud.com/)
     */
	@TableField(value = "base_path")
	private String basePath;
    /**
     * bucket的目录名称
     */
	@TableField(value = "bucket_name")
	private String bucketName;
    /**
     * 文件存储目录
     */	
	@TableField(value = "dir")
	private String dir;
    /**
     * AccessKey值
     */
	@TableField(value = "access_key",updateStrategy = FieldStrategy.NOT_EMPTY)
	private String accessKey;
    /**
     * SecretKey值
     */
	@TableField(value = "secret_key",updateStrategy = FieldStrategy.NOT_EMPTY)
	private String secretKey;

	/**
	 * 地域
	 */
	@TableField(value = "endpoint")
	private String endpoint;

	/**
	 * 区域
	 */
	@TableField(value = "region")
	private String region;
	/**
	 * 上传测试结果
	 */
	@TableField(value = "test_access",updateStrategy = FieldStrategy.NOT_NULL)
	private Boolean testAccess;

	/**
	 * 是否启用
	 */
	@TableField("enable")
	private Boolean enable;

	/**
	 * 用户测试上传的图片路径
	 */
	@TableField("test_web_url")
	private String testWebUrl;
}
