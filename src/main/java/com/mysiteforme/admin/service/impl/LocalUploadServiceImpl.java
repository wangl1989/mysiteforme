/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:22:46
 * @ Description: 本地上传服务实现类 提供本地上传的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.UploadType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.mysiteforme.admin.util.ToolUtil;

import static com.mysiteforme.admin.util.Constants.BASE_DIR;

@Service("localService")
@Slf4j
public class LocalUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private final UploadBaseInfoService uploadBaseInfoService;

    public LocalUploadServiceImpl(UploadBaseInfoService uploadBaseInfoService) {
        this.uploadBaseInfoService = uploadBaseInfoService;
    }

    // 提取常量
    private static final String KB_FORMAT = "#.##kb";
    private static final String DEFAULT_CONTENT_TYPE = "unknown";

    private String getStaticPath() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/");
        return resource.getFile().getAbsolutePath();
    }

    private File createUploadDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.FOLDER_CREATE_FAILED)
                    .build();
        }
        return dir;
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
        resource.setSource(UploadType.LOCAL.getCode());
        return resource;
    }

    private String generateFileName(String originalName) {
        String extension = StringUtils.isNotBlank(originalName)
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        return UUID.randomUUID() + extension;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException{
        Objects.requireNonNull(file, MessageConstants.file.UPLOAD_EMPTY);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        String hash = new QETag().calcETag(file);

        // 检查文件是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getSource, UploadType.LOCAL.getCode())
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

        // 创建完整的文件路径
        Path filePath = Paths.get(getStaticPath(), uploadDir, finalFileName).normalize();
        createUploadDirectory(filePath.getParent().toString());

        // 写入文件
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            log.error("【MultipartFile文件上传】文件写入失败", e);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.File_CREATE_FAILED)
                    .build();
        }

        // 创建资源记录
        String webUrl = BASE_DIR + uploadDir + finalFileName;
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
    }

    /**
     * 上传文件到本地服务器
     * @param file 要上传的文件
     * @return 文件的Web访问URL
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 加密算法异常
     */
    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return upload(file,null);
    }

    /**
     * 删除本地文件
     * @param path 文件路径
     * @return 删除是否成功
     */
    @Override
    public Boolean delete(String path) {
        try {
            Path filePath = Paths.get(getStaticPath(), path);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除文件失败", e);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.FILE_DELETE_ERROR)
                    .build();
        }
    }

    /**
     * 下载网络文件并上传到本地服务器
     * @param url 网络文件URL
     * @return 文件的Web访问URL
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 加密算法异常
     */
    @Override
    public String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException {
        Objects.requireNonNull(url, MessageConstants.file.WEB_URL_NOT_NULL);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();

        // 检查是否已存在
        Rescource existingResource = lambdaQuery()
                .eq(Rescource::getOriginalNetUrl, url)
                .eq(Rescource::getSource, UploadType.LOCAL.getCode())
                .one();
        if (existingResource != null) {
            return existingResource.getWebUrl();
        }

        String fileName = generateFileName(url);
        Path filePath = Paths.get(getStaticPath(), uploadBaseInfo.getDir(), fileName);
        createUploadDirectory(filePath.getParent().toString());

        // 下载并保存文件
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, filePath);
        }

        // 计算文件大小和hash
        File savedFile = filePath.toFile();
        String hash = new QETag().calcETag(savedFile);

        // 创建资源记录
        String webUrl = BASE_DIR + uploadBaseInfo.getDir() + fileName;
        String fileType = StringUtils.substringAfterLast(url, ".");

        Rescource resource = createResource(
                fileName,
                savedFile.length(),
                hash,
                fileType,
                webUrl,
                url
        );

        save(resource);
        return webUrl;
    }

    /**
     * 上传本地图片到服务器
     * @param localPath 本地图片路径
     * @return 文件的Web访问URL
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
                .eq(Rescource::getSource, UploadType.LOCAL.getCode())
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

        // 生成新文件名
        String fileName = generateFileName(sourceFile.getFileName().toString());
        String extension = StringUtils.substringAfterLast(sourceFile.getFileName().toString(), ".");

        // 创建目标路径
        Path targetPath = Paths.get(getStaticPath(), uploadDir, fileName);
        createUploadDirectory(targetPath.getParent().toString());

        // 复制文件
        try {
            Files.copy(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件复制失败", e);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg("文件上传失败")
                    .build();
        }

        // 构建Web访问URL
        String webUrl = BASE_DIR + uploadDir + fileName;

        // 创建并保存资源记录
        Rescource resource = createResource(
                fileName,
                Files.size(sourceFile),
                hash,
                extension,
                webUrl,
                localPath
        );

        save(resource);
        return webUrl;
    }

    /**
     * 上传Base64编码的图片
     * @param base64 Base64编码的图片数据
     * @return 文件的Web访问URL，上传失败返回null
     */
    @Override
    public String uploadBase64(String base64){
        Objects.requireNonNull(base64, MessageConstants.file.FILE_UPLOAD_BASE64_NOT_NULL);

        UploadBaseInfo uploadBaseInfo = getUploadBaseInfo();
        String fileFormat = ToolUtil.getFileFormat(base64);
        String fileName = generateFileName("." + fileFormat);

        // 解码Base64数据
        byte[] fileData = Base64.getDecoder().decode(
                base64.contains(",") ? base64.split(",")[1] : base64
        );

        Path filePath;
        try {
            // 保存文件
            filePath = Paths.get(getStaticPath(), uploadBaseInfo.getDir(), fileName);
            createUploadDirectory(filePath.getParent().toString());
            Files.write(filePath, fileData);
        }catch (IOException e) {
            log.error("【上传Base64编码的图片】文件写入失败", e);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.File_CREATE_FAILED)
                    .build();
        }
        try {
            // 创建资源记录
            String webUrl = BASE_DIR + uploadBaseInfo.getDir() + fileName;
            String hash = new QETag().calcETag(filePath.toFile());
            Rescource resource = createResource(
                    fileName,
                    fileData.length,
                    hash,
                    fileFormat,
                    webUrl,
                    null
            );

            save(resource);
            return webUrl;
        }catch (IOException e) {
            log.error("【上传Base64编码的图片】计算文件哈希值失败", e);
            throw MyException.builder()
                    .code(MyException.SERVER_ERROR)
                    .msg(MessageConstants.file.IO_EXCEPTION)
                    .build();
        }catch (NoSuchAlgorithmException e) {
            throw MyException.builder().businessError(MessageConstants.file.FILE_NOT_EXIST).build();
        }
    }

    @Override
    public Boolean testBaseInfoAccess(UploadBaseInfo uploadBaseInfo) {
        try {
            Path testFile = Paths.get("static/images/userface1.jpg");
            Path targetPath = Paths.get(uploadBaseInfo.getDir(), "test.jpg");
            if(!Files.exists(targetPath)){
                Files.createDirectories(targetPath.getParent());
            }
            try (InputStream in = new ClassPathResource(testFile.toString()).getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            uploadBaseInfo.setTestWebUrl(BASE_DIR + uploadBaseInfo.getDir() + "test.jpg");
            return true;
        } catch (IOException e) {
            log.error("测试文件上传失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public UploadBaseInfo getUploadBaseInfo() {
        return uploadBaseInfoService.getInfoByType(UploadType.LOCAL.getCode());
    }


}
