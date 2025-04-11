package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UploadBaseInfoRequest {

    @NotBlank(message = MessageConstants.UploadBaseInfo.TYPE_EMPTY)
    private String type;

    /**
     * 本地window系统上传路径
     */
    private String localWindowUrl;

    /**
     * 本地LINUX系统上传路径
     */
    private String localLinuxUrl;

    /**
     * 前缀路径，例子如下:
     * (<a href="https://weizheng0301-1257429161.cos.ap-shanghai.myqcloud.com/">...</a>)
     * (<a href="https://weizhengzs.s3.bitiful.net/">...</a>)
     * (<a href="https://weizheng0301-1257429161.cos.ap-shanghai.myqcloud.com/">...</a>)
     */
    @NotBlank(message = MessageConstants.UploadBaseInfo.BASE_PATH_EMPTY)
    private String basePath;

    /**
     * bucket的目录名称
     */
    @NotBlank(message = MessageConstants.UploadBaseInfo.BUCKET_NAME_EMPTY)
    private String bucketName;

    /**
     * 文件存储目录
     */
    @NotBlank(message = MessageConstants.UploadBaseInfo.DIR_EMPTY)
    private String dir;

    /**
     * AccessKey值
     */
    @NotBlank(message = MessageConstants.UploadBaseInfo.ACCESS_KEY_EMPTY)
    private String accessKey;

    /**
     * SecretKey值
     */
    @NotBlank(message = MessageConstants.UploadBaseInfo.SECRET_KEY_EMPTY)
    private String secretKey;

    /**
     * 地域
     */
    private String endpoint;

    /**
     * 区域
     */
    private String region;

    /**
     * 上传测试结果
     */
    private Boolean testAccess;

    /**
     * 是否启用
     */
    private Boolean enable;
}
