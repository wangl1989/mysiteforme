/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:24:33
 * @ Description: 七牛云上传实现类
 */

package com.mysiteforme.admin.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Objects;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.ToolUtil;
import com.mysiteforme.admin.util.UploadType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.RescourceService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;

@Slf4j
@Service("qiniuService")
public class QiniuUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private final UploadBaseInfoService uploadBaseInfoService;

    private final RescourceService rescourceService;

    private static final String KB_FORMAT = "#.##kb";
    private static final String DEFAULT_CONTENT_TYPE = "unknown";

    @Autowired
    public QiniuUploadServiceImpl(UploadBaseInfoService uploadBaseInfoService, RescourceService rescourceService) {
        this.uploadBaseInfoService = uploadBaseInfoService;
        this.rescourceService = rescourceService;
    }

    /**
     * 获取七牛云上传管理器
     * @return 上传管理器实例
     */
    private UploadManager getUploadManager(){
        Configuration cfg = new Configuration();
        return new UploadManager(cfg);
    }

    /**
     * 获取七牛云认证对象
     * @return 认证对象
     * @throws MyException 当上传配置不存在时抛出异常
     */
    private Auth getAuth(){
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        if(uploadBaseInfo == null){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("上传信息配置不存在").build();
        }
        return Auth.create(uploadBaseInfo.getAccessKey(), uploadBaseInfo.getSecretKey());
    }

    /**
     * 获取上传凭证
     * @return 上传凭证字符串
     */
    private String getToken(){
        return getAuth().uploadToken(getUploadBaseInfo().getBucketName());
    }

    /**
     * 获取七牛云空间管理器
     * @return 空间管理器实例
     */
    private BucketManager getBucketManager(){
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        Configuration config = new Configuration();
        Auth auth = Auth.create(uploadBaseInfo.getAccessKey(), uploadBaseInfo.getSecretKey());
        return new BucketManager(auth,config);
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
        resource.setSource(UploadType.QINIU.getCode());
        return resource;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException {
        Objects.requireNonNull(file, MessageConstants.file.UPLOAD_EMPTY);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        String hash = new QETag().calcETag(file);

        // 检查文件是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getSource, UploadType.QINIU.getCode())
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

        byte[] data = file.getBytes();
        Response r = getUploadManager().put(data, filePath, getToken());

        // 创建资源记录
        String webUrl = uploadBaseInfo.getBasePath() + filePath;
        if(r.isOK()) {
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
     * 上传文件到七牛云
     * @param file 要上传的文件
     * @return 文件的访问URL
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 加密算法异常
     */
    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return upload(file,null);
    }

    /**
     * 从七牛云删除文件
     * @param path 文件路径
     * @return 删除是否成功
     */
    @Override
    public Boolean delete(String path) {
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        Rescource rescource = lambdaQuery()
                .eq(Rescource::getWebUrl,path)
                .eq(Rescource::getDelFlag, false)
                .eq(Rescource::getSource,UploadType.QINIU.getCode())
                .one();
        if(rescource == null){
            log.warn("地址：{}-对应的文件不在数据库中",path);
            return false;
        }
        path = path.replace(uploadBaseInfo.getBasePath(),"");
        try {
            //删除七牛云上的文件
            Response response = getBucketManager().delete(uploadBaseInfo.getBucketName(), path);
            if(response.isOK()) {
                //删除数据库记录
                return rescourceService.removeById(rescource.getId());
            }
        } catch (QiniuException e) {
            log.error("删除七牛云上的文件失败",e);
        }
        return false;
    }

    /**
     * 上传网络文件并上传到七牛云
     * @param url 网络文件URL
     * @return 上传后的文件访问URL
     */
    @Override
    public String uploadNetFile(String url) {
        Objects.requireNonNull(url, MessageConstants.file.WEB_URL_NOT_NULL);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();

        // 检查是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getOriginalNetUrl, url)
                .eq(Rescource::getSource, UploadType.QINIU.getCode())
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
        // 构建最终需要返回的详细文件地址
        String webUrl = uploadBaseInfo.getBasePath() + filePath;
        try {
            FetchRet fetchRet = getBucketManager().fetch(url, uploadBaseInfo.getBucketName(),filePath);
            if(fetchRet.key.equals(filePath)){
                Rescource resource = createResource(
                        fileName,
                        fetchRet.fsize,
                        fetchRet.hash,
                        fetchRet.mimeType,
                        webUrl,
                        url
                );
                save(resource);
            }
        } catch (QiniuException e) {
            log.error("【七牛云上传网络文件】上传网络文件失败",e);
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("上传网络文件失败"+e.getMessage()).build();
        }
        return webUrl;
    }

    /**
     * 上传本地图片到七牛云
     * @param localPath 本地文件路径
     * @return 上传后的文件访问URL
     * @throws MyException 当本地文件不存在或上传失败时抛出异常
     */
    @Override
    public String uploadLocalImg(String localPath) throws IOException, NoSuchAlgorithmException {
        Objects.requireNonNull(localPath, MessageConstants.file.LOCAL_URL_NOT_NULL);

        // 获取上传配置信息
        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();

        // 验证源文件
        Path sourceFile = Paths.get(localPath);
        if (!Files.exists(sourceFile)) {
            log.error("本地文件不存在, 上传路径为: {}", localPath);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.FILE_NOT_EXIST)
                    .build();
        }

        // 计算文件哈希值
        String hash = new QETag().calcETag(sourceFile.toFile());

        // 检查文件是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getSource, UploadType.QINIU.getCode())
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

        Response response = getUploadManager().put(sourceFile.toFile(),filePath,getToken());
        if(response.isOK()) {
            Rescource resource = createResource(
                    fileName,
                    Files.size(sourceFile),
                    hash,
                    extension,
                    webUrl,
                    null
            );
            save(resource);
            return webUrl;
        } else {
            log.error("【上传本地图片到七牛云】：上传文件到七牛云失败,返回数据:{}",response);
            return "";
        }

    }

    /**
     * 上传Base64编码的图片到七牛云
     * @param base64 Base64编码的图片数据
     * @return 上传后的文件访问URL
     * @throws MyException 当上传失败时抛出异常
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
        try {
            Response response = getUploadManager().put(fileData,filePath,getToken());
            if(response.isOK()){
                Rescource resource = createResource(
                        fileName,
                        fileData.length,
                        response.getResponse().header("hash"),
                        fileFormat,
                        webUrl,
                        null
                );
                save(resource);
            }
        } catch (QiniuException e) {
            log.error("七牛使用Base64方法上传文件失败",e);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.QINIU_UPLOAD_BASE64_EXCEPTION)
                    .build();
        }
        return webUrl;
    }

    @Override
    public Boolean testBaseInfoAccess(UploadBaseInfo uploadBaseInfo) {
        try {
            Path testFile = Paths.get("static/images/userface1.jpg");
            Auth auth = Auth.create(uploadBaseInfo.getAccessKey(), uploadBaseInfo.getSecretKey());
            String authstr = auth.uploadToken(uploadBaseInfo.getBucketName());
            InputStream inputStream = new ClassPathResource(testFile.toString()).getInputStream();
            String filePath = uploadBaseInfo.getDir() + "test.jpg";
            Response response = getUploadManager().put(inputStream,filePath,authstr,null,null);
            if(response.isOK()) {
                uploadBaseInfo.setTestWebUrl(filePath);
                return true;
            }
        } catch (IOException e) {
            log.error("七牛云测试文件上传失败: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public UploadBaseInfo getUploadBaseInfo() {
        return uploadBaseInfoService.getInfoByType(UploadType.QINIU.getCode());
    }
}
