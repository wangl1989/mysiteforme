package com.mysiteforme.admin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.RescourceService;
import com.mysiteforme.admin.service.UploadInfoService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.mysiteforme.admin.util.ToolUtil;
import com.xiaoleilu.hutool.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service("ossService")
public class OssUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(OssUploadServiceImpl.class);

    private final RescourceService rescourceService;

    private final UploadInfoService uploadInfoService;

    @Autowired
    public OssUploadServiceImpl(RescourceService rescourceService, UploadInfoService uploadInfoService) {
        this.rescourceService = rescourceService;
        this.uploadInfoService = uploadInfoService;
    }

    private UploadInfo getUploadInfo(){
        return uploadInfoService.getOneInfo();
    }

    private OSS getOSSClient(){
        return new OSSClientBuilder().build(getUploadInfo().getOssEndpoint(),getUploadInfo().getOssKeyId(), getUploadInfo().getOssKeySecret());
    }

    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileName,realNames = "";
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getOssBasePath());
        String ossDir = getUploadInfo().getOssDir();
        try {
            fileName = file.getOriginalFilename();
            //上传文件
            StringBuilder realName = new StringBuilder(UUID.randomUUID().toString());
            String fileExtension = null;
            if (fileName != null) {
                fileExtension = fileName.substring(fileName.lastIndexOf("."));
            }
            realName.append(fileExtension);
            QETag tag = new QETag();
            String hash = tag.calcETag(file);
            QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
            wrapper.eq("hash",hash);
            wrapper.eq("source","oss");
            Rescource rescource = getOne(wrapper);
            if( rescource!= null){
                return rescource.getWebUrl();
            }

            InputStream is = file.getInputStream();

            long fileSize = file.getSize();
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            if (fileName != null) {
                metadata.setContentType(ToolUtil.getContentType(fileName));
            }
            metadata.setContentDisposition("filename/filesize=" + realNames + "/" + fileSize + "Byte.");

            StringBuilder key = new StringBuilder();
            if(ossDir != null && !ossDir.isEmpty()){
                key.append(ossDir).append("/");
                returnUrl.append(ossDir).append("/");
            }
            key.append(realName);
            returnUrl.append(realName);
            PutObjectResult putResult = getOSSClient().putObject(getUploadInfo().getOssBucketName(), key.toString(), is, metadata);
            //解析结果
            System.out.println("md5码为"+putResult.getETag());
            rescource = new Rescource();
            rescource.setFileName(realName.toString());
            rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
            rescource.setHash(hash);
            rescource.setFileType(StringUtils.isBlank(fileExtension)?"unknown":fileExtension);
            rescource.setWebUrl(returnUrl.toString());
            rescource.setSource("oss");
            save(rescource);
            getOSSClient().shutdown();
            is.close();
        } catch (Exception e) {
            logger.error("上传阿里云OSS服务器异常.{}", e.getMessage(), e);
            throw new MyException("上传阿里云OSS服务器异常." + e.getMessage());
        }
        return returnUrl.toString();
    }

    @Override
    public Boolean delete(String path) {
        path = path.replace(getUploadInfo().getOssBasePath(), "");
        String ossDir = getUploadInfo().getOssDir();
        StringBuilder sb = new StringBuilder();
        if(ossDir != null && !ossDir.isEmpty()){
            sb.append(ossDir).append("/");
        }
        String key = path.replace(sb.toString(),"");
        try {
            getOSSClient().deleteObject(getUploadInfo().getOssBucketName(), path);
            QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
            wrapper.eq("file_name",key);
            wrapper.eq("source","oss");
            return rescourceService.remove(wrapper);
        }catch (Exception e){
            logger.error("删除阿里云文件出现异常.{}", e.getMessage(), e);
            throw new MyException("删除阿里云文件出现异常." + e.getMessage());
        }
    }

    @Override
    public String uploadNetFile(String url) throws IOException {
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("source","oss");
        wrapper.eq("original_net_url",url);
        Rescource rescource = rescourceService.getOne(wrapper);
        if(rescource != null){
            return rescource.getWebUrl();
        }
        String ossDir = getUploadInfo().getOssDir(),
                fileName = RandomUtil.randomUUID();
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getOssBasePath());
        StringBuilder key = new StringBuilder();
        if(ossDir != null && !ossDir.isEmpty()){
            key.append(ossDir).append("/");
        }
        key.append(fileName).append(".jpg");
        StringBuilder sb = new StringBuilder(fileName);
        InputStream inputStream = new URL(url).openStream();
        PutObjectResult putObjectResult = getOSSClient().putObject(getUploadInfo().getOssBucketName(), key.toString(), inputStream);
        returnUrl.append(key);
        rescource = new Rescource();
        rescource.setFileName(sb.append(".jpg").toString());
        rescource.setHash(putObjectResult.getETag());
        rescource.setWebUrl(returnUrl.toString());
        rescource.setOriginalNetUrl(url);
        rescource.setSource("oss");
        save(rescource);
        inputStream.close();

        getOSSClient().shutdown();
        return returnUrl.toString();
    }

    @Override
    public String uploadLocalImg(String localPath) {
        File file = new File(localPath);
        if(!file.exists()){
            logger.error("本地文件不存在");
            throw new MyException("本地文件不存在");
        }
        QETag tag = new QETag();
        String hash = null;
        try {
            hash = tag.calcETag(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("阿里云上传文件出错",e);
        }
        LambdaQueryWrapper<Rescource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rescource::getHash,hash)
                .eq(Rescource::getSource,"oss");
        Rescource rescource = getOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        String  extName,
                ossDir = getUploadInfo().getOssDir(),
                name = UUID.randomUUID().toString();
        extName = file.getName().substring(
                file.getName().lastIndexOf("."));
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getOssBasePath());
        StringBuilder key = new StringBuilder();
        if(ossDir != null && !ossDir.isEmpty()){
            key.append(ossDir).append("/");
        }
        key.append(name).append(".").append(extName);
        StringBuilder realName = new StringBuilder(name);
        getOSSClient().putObject(getUploadInfo().getOssBucketName(), key.toString(), file);
        returnUrl.append(realName);
        rescource = new Rescource();
        rescource.setFileName(realName.append(".").append(extName).toString());
        rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.length()/1024)+"kb");
        rescource.setHash(hash);
        rescource.setFileType(extName);
        rescource.setWebUrl(returnUrl.toString());
        rescource.setSource("oss");
        save(rescource);
        getOSSClient().shutdown();
        return null;
    }

    @Override
    public String uploadBase64(String base64) {
        //base64数据转换为byte[]类型
        byte[] asBytes = Base64.getDecoder().decode(base64);
        InputStream sbs = new ByteArrayInputStream(asBytes);
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getOssBasePath());
        StringBuilder key = new StringBuilder();
        StringBuilder fileName = new StringBuilder(RandomUtil.randomUUID());
        String ossDir = getUploadInfo().getOssDir();
        int fileSize = asBytes.length;
        //创建上传Object的Metadata
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(sbs.available());
            metadata.setContentEncoding("utf-8");
            metadata.setContentType("image/png");
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            if(ossDir != null && !ossDir.isEmpty()){
                key.append(ossDir).append("/");
                returnUrl.append(ossDir).append("/");
            }
            key.append(fileName);
            getOSSClient().putObject(getUploadInfo().getOssBucketName(), key.toString(), sbs, metadata);
            returnUrl.append(fileName);
        } catch (IOException e) {
            logger.error("上传阿里云OSS服务器异常.{}", e.getMessage(), e);
        } finally {
            try {
                sbs.close();
            } catch (IOException e) {
                logger.error("上传阿里云OSS出现IO异常.{}", e.getMessage(), e);
            }
            getOSSClient().shutdown();
        }
        return returnUrl.toString();
    }

    @Override
    public Boolean testAccess(UploadInfo uploadInfo) {
        ClassPathResource classPathResource = new ClassPathResource("static/images/userface1.jpg");
        try {
            OSS oss = new OSSClientBuilder().build(uploadInfo.getOssEndpoint(),uploadInfo.getOssKeyId(), uploadInfo.getOssKeySecret());
            InputStream inputStream = classPathResource .getInputStream();
            oss.putObject(uploadInfo.getOssBucketName(), "test.jpg", inputStream, null);
            oss.shutdown();
            return true;
        } catch (Exception e) {
            logger.error("测试上传阿里云OSS出现异常.{}", e.getMessage(), e);
            return false;
        }
    }
}
