package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UploadBaseInfoRequest {

    @NotBlank(message = MessageConstants.UploadBaseInfo.TYPE_EMPTY)
    private String type;

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
    private String bucketName;

    /**
     * 文件存储目录
     */
    @NotBlank(message = MessageConstants.UploadBaseInfo.DIR_EMPTY)
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z/]*/$", message = MessageConstants.UploadBaseInfo.DIR_RULE_NOT_MATCH)
    private String dir;

    /**
     * AccessKey值
     */
    private String accessKey;

    /**
     * SecretKey值
     */
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

    /**
     * 测试图片地址
     */
    private String testWebUrl;

    /**
     * 备注
     */
    private String remarks;
}
