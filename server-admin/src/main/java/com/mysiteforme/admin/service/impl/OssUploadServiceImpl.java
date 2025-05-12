/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:23:22
 * @ Description: 阿里云上传服务实现类 提供阿里云上传的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Objects;

import com.aliyun.oss.model.VoidResult;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.UploadType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.RescourceService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.mysiteforme.admin.util.ToolUtil;

@Slf4j
@Service("ossService")
public class OssUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private final RescourceService rescourceService;

    private final UploadBaseInfoService uploadBaseInfoService;

    private static final String KB_FORMAT = "#.##kb";
    private static final String DEFAULT_CONTENT_TYPE = "unknown";

    public OssUploadServiceImpl(RescourceService rescourceService, UploadBaseInfoService uploadBaseInfoService) {
        this.rescourceService = rescourceService;
        this.uploadBaseInfoService = uploadBaseInfoService;
    }

    private String generateFileName(String originalName) {
        String extension = StringUtils.isNotBlank(originalName)
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        return java.util.UUID.randomUUID() + extension;
    }

    private Rescource createResource(String fileName, long fileSize, String hash,
                                     String fileType, String webUrl, String originalUrl) {
        Rescource resource = new Rescource();
        resource.setFileName(fileName);
        resource.setFileSize(new DecimalFormat(KB_FORMAT).format(fileSize / 1024));
        resource.setHash(hash);
        resource.setFileType(StringUtils.defaultIfBlank(fileType, DEFAULT_CONTENT_TYPE));
        resource.setWebUrl(webUrl);
        if (originalUrl != null) {
            resource.setOriginalNetUrl(originalUrl);
        }
        resource.setSource(UploadType.OSS.getCode());
        return resource;
    }

    /**
     * 获取OSS客户端实例
     * @return OSS客户端
     */
    private OSS getOSSClient(){
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        return new OSSClientBuilder().build(uploadBaseInfo.getEndpoint(),uploadBaseInfo.getAccessKey(), uploadBaseInfo.getSecretKey());
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException {
        Objects.requireNonNull(file, MessageConstants.file.UPLOAD_EMPTY);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        String hash = new QETag().calcETag(file);

        // 检查文件是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getSource, UploadType.OSS.getCode())
                .eq(Rescource::getHash, hash)
                .one();
        if (existingResource != null) {
            return existingResource.getWebUrl();
        }

        // 处理文件名
        String finalFileName = StringUtils.isNotBlank(fileName)
                ? fileName
                : generateFileName(file.getOriginalFilename());

        // 获取上传路径
        String uploadDir = uploadBaseInfo.getDir();
        if (StringUtils.isBlank(uploadDir)) {
            throw MyException.builder()
                    .businessError(MessageConstants.file.FILE_DIR_NOT_EXIST)
                    .build();
        }

        uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        // 创建完整的文件路径
        String filePath = uploadDir + finalFileName;

        OSS oss = getOSSClient();
        PutObjectResult putResult = oss.putObject(uploadBaseInfo.getBucketName(), filePath, file.getInputStream());
        // 构建最终文件地址
        String webUrl = uploadBaseInfo.getBasePath() + filePath;
        if(StringUtils.isNotBlank(putResult.getETag())) {
            // 创建资源记录
            Rescource resource = createResource(
                    finalFileName,
                    file.getSize(),
                    hash,
                    file.getContentType(),
                    webUrl,
                    null
            );
            save(resource);
            return webUrl;
        } else {
            return "";
        }

    }

    /**
     * 上传文件到阿里云OSS
     * @param file 要上传的文件
     * @return 文件的访问URL
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 加密算法异常
     */
    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return this.upload(file,null);
    }

    /**
     * 从阿里云OSS删除文件
     * @param path 文件路径
     * @return 删除是否成功
     */
    @Override
    public Boolean delete(String path) {
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        Rescource rescource = lambdaQuery()
                .eq(Rescource::getWebUrl,path)
                .eq(Rescource::getDelFlag, false)
                .eq(Rescource::getSource,UploadType.OSS.getCode())
                .one();
        if(rescource == null){
            log.warn("删除阿里云OSS文件失败:地址：{}-对应的文件不在数据库中",path);
            return false;
        }
        path = path.replace(uploadBaseInfo.getBasePath(), "");
        OSS oss = getOSSClient();
        try {
            VoidResult result = oss.deleteObject(uploadBaseInfo.getBucketName(), path);
            if(StringUtils.isNotBlank(result.getRequestId())){
                return rescourceService.removeById(rescource.getId());
            }
        } catch (OSSException | ClientException e) {
            log.error("删除阿里云OSS文件失败: {}", e.getMessage(), e);
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("删除阿里云OSS文件失败").build();
        } finally {
            oss.shutdown();
        }
        return false;
    }

    /**
     * 上传网络文件并上传到阿里云OSS
     * @param url 网络文件URL
     * @return 上传后的文件访问URL
     * @throws IOException IO异常
     */
    @Override
    public String uploadNetFile(String url) throws IOException {
        Objects.requireNonNull(url, MessageConstants.file.WEB_URL_NOT_NULL);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();

        // 检查是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getOriginalNetUrl, url)
                .eq(Rescource::getSource, UploadType.OSS.getCode())
                .one();
        if (existingResource != null) {
            return existingResource.getWebUrl();
        }

        // 生成新文件名
        String fileName = generateFileName(url);

        // 获取上传路径
        String uploadDir = uploadBaseInfo.getDir();
        if (StringUtils.isBlank(uploadDir)) {
            throw MyException.builder()
                    .businessError(MessageConstants.file.FILE_DIR_NOT_EXIST)
                    .build();
        }
        uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        // 创建完整的文件路径
        String filePath = uploadDir + fileName;
        // 获取文件类型
        String fileType = StringUtils.substringAfterLast(url, ".");
        // 构建最终需要返回的详细文件地址
        String webUrl = uploadBaseInfo.getBasePath() + filePath;

        OSS oss = getOSSClient();
        try {
            PutObjectResult result = oss.putObject(uploadBaseInfo.getBucketName(), filePath, new URL(url).openStream());
            if(StringUtils.isNotBlank(result.getETag())){
                // 创建资源记录
                Rescource resource = createResource(
                        fileName,
                        result.getResponse().getContentLength(),
                        result.getETag(),
                        fileType,
                        webUrl,
                        url
                );
                save(resource);
                return webUrl;
            }
        } catch (OSSException | ClientException e) {
            log.error("删除阿里云OSS文件失败: {}", e.getMessage(), e);
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("删除阿里云OSS文件失败").build();
        } finally {
            oss.shutdown();
        }
        return "";
    }

    /**
     * 上传本地图片到阿里云OSS
     * @param localPath 本地文件路径
     * @return 上传后的文件访问URL
     */
    @Override
    public String uploadLocalImg(String localPath) throws IOException, NoSuchAlgorithmException {
        Objects.requireNonNull(localPath, MessageConstants.file.LOCAL_URL_NOT_NULL);
        // 获取上传配置信息
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        // 验证源文件
        Path sourceFile = Paths.get(localPath);
        if (!Files.exists(sourceFile)) {
            log.error("【阿里云上传本地文件】本地文件不存在, 上传路径为: {}", localPath);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.FILE_NOT_EXIST)
                    .build();
        }
        // 计算文件哈希值
        String hash = new QETag().calcETag(sourceFile.toFile());

        // 检查文件是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getSource, UploadType.OSS.getCode())
                .eq(Rescource::getHash, hash)
                .one();
        if (existingResource != null) {
            return existingResource.getWebUrl();
        }

        // 获取上传目录
        String uploadDir = uploadBaseInfo.getDir();
        if (StringUtils.isBlank(uploadDir)) {
            throw MyException.builder()
                    .businessError(MessageConstants.file.FILE_DIR_NOT_EXIST)
                    .build();
        }
        uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        // 生成新文件名
        String fileName = generateFileName(sourceFile.getFileName().toString());
        String extension = StringUtils.substringAfterLast(sourceFile.getFileName().toString(), ".");

        // 创建完整的文件路径
        String filePath = uploadDir + fileName;
        // 构建最终需要返回的详细文件地址
        String webUrl = uploadBaseInfo.getBasePath() + filePath;

        OSS oss = getOSSClient();
        PutObjectResult result = oss.putObject(uploadBaseInfo.getBucketName(), filePath, sourceFile.toFile());
        if(StringUtils.isNotBlank(result.getETag())){
            Rescource resource = createResource(
                    fileName,
                    Files.size(sourceFile),
                    hash,
                    extension,
                    webUrl,
                    null
            );
            save(resource);
            oss.shutdown();
            return webUrl;
        } else {
            log.error("【上传本地图片到七牛云】：上传文件到七牛云失败,返回数据:{}",result);
            return "";
        }
    }

    /**
     * 上传Base64编码的图片到阿里云OSS
     * @param base64 Base64编码的图片数据
     * @return 上传后的文件访问URL
     */
    @Override
    public String uploadBase64(String base64) {
        Objects.requireNonNull(base64, MessageConstants.file.FILE_UPLOAD_BASE64_NOT_NULL);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        String fileFormat = ToolUtil.getFileFormat(base64);
        String fileName = generateFileName("." + fileFormat);

        // 解码Base64数据
        byte[] fileData = java.util.Base64.getDecoder().decode(
                base64.contains(",") ? base64.split(",")[1] : base64
        );

        // 获取上传目录
        String uploadDir = uploadBaseInfo.getDir();
        if (StringUtils.isBlank(uploadDir)) {
            throw MyException.builder()
                    .businessError(MessageConstants.file.FILE_DIR_NOT_EXIST)
                    .build();
        }
        uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        // 创建完整的文件路径
        String filePath = uploadDir + fileName;
        // 构建最终需要返回的详细文件地址
        String webUrl = uploadBaseInfo.getBasePath() + filePath;
        OSS oss = getOSSClient();
        PutObjectResult result =  oss.putObject(uploadBaseInfo.getBucketName(),filePath, new ByteArrayInputStream(fileData));
        if(StringUtils.isNotBlank(result.getETag())){
            Rescource resource = createResource(
                    fileName,
                    fileData.length,
                    result.getETag(),
                    fileFormat,
                    webUrl,
                    null
            );
            save(resource);
        }
        return webUrl;
    }

    @Override
    public Boolean testBaseInfoAccess(UploadBaseInfo uploadBaseInfo) {
        Path testFile = Paths.get("static/images/userface1.jpg");
        try {
            String uploadPath = uploadBaseInfo.getDir()+"test.jpg";
            OSS oss = new OSSClientBuilder().build(uploadBaseInfo.getEndpoint(),uploadBaseInfo.getAccessKey(), uploadBaseInfo.getSecretKey());
            PutObjectResult result =  oss.putObject(uploadBaseInfo.getBucketName(), uploadPath, new ClassPathResource(testFile.toString()).getInputStream());
            if(StringUtils.isNotBlank(result.getETag())){
                uploadBaseInfo.setTestWebUrl(uploadPath);
                return true;
            }
            oss.shutdown();
        } catch (IOException e) {
            log.error("测试文件上传失败: {}", e.getMessage(), e);
        } catch (OSSException | ClientException e) {
            log.error("测试OSS连接失败: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public UploadBaseInfo getUploadBaseInfo() {
        return uploadBaseInfoService.getInfoByType(UploadType.OSS.getCode());
    }
}
