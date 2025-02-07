package com.mysiteforme.admin.service.impl;

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
import com.mysiteforme.admin.util.QiniuFileUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;
import com.xiaoleilu.hutool.util.RandomUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service("qiniuService")
public class QiniuUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(QiniuUploadServiceImpl.class);

    private final UploadInfoService uploadInfoService;

    private final RescourceService rescourceService;

    @Autowired
    public QiniuUploadServiceImpl(UploadInfoService uploadInfoService, RescourceService rescourceService) {
        this.uploadInfoService = uploadInfoService;
        this.rescourceService = rescourceService;
    }

    private UploadInfo getUploadInfo(){
        return uploadInfoService.getOneInfo();
    }

    private UploadManager getUploadManager(){
        Configuration cfg = new Configuration();
        return new UploadManager(cfg);
    }

    private Auth getAuth(){
        if(getUploadInfo() == null){
            throw new MyException("上传信息配置不存在");
        }
        return Auth.create(getUploadInfo().getQiniuAccessKey(), getUploadInfo().getQiniuSecretKey());
    }

    private String getToken(){
        return getAuth().uploadToken(getUploadInfo().getQiniuBucketName());
    }

    private BucketManager getBucketManager(){
        Configuration config = new Configuration();
        Auth auth = Auth.create(getUploadInfo().getQiniuAccessKey(), getUploadInfo().getQiniuSecretKey());
        return new BucketManager(auth,config);
    }



    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileName, extName;
        StringBuilder key = new StringBuilder();
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getQiniuBasePath());
        if (null != file && !file.isEmpty()) {
            extName = Objects.requireNonNull(file.getOriginalFilename()).substring(
                    file.getOriginalFilename().lastIndexOf("."));
            fileName = RandomUtil.randomUUID() + extName;
            byte[] data = file.getBytes();
            QETag tag = new QETag();
            String hash = tag.calcETag(file);
            QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
            wrapper.eq("hash",hash);
            wrapper.eq("source","qiniu");
            Rescource rescource = getOne(wrapper);
            if( rescource!= null){
                return rescource.getWebUrl();
            }
            String qiniuDir = getUploadInfo().getQiniuDir();

            if(StringUtils.isNotBlank(qiniuDir)){
                key.append(qiniuDir).append("/");
                returnUrl.append(qiniuDir).append("/");
            }
            key.append(fileName);
            returnUrl.append(fileName);
            Response r = getUploadManager().put(data, key.toString(), getToken());
            if (r.isOK()) {
                getUploadInfo();
                rescource = QiniuFileUtil.getRescource(file, fileName, extName, hash);
                rescource.setWebUrl(returnUrl.toString());
                rescource.setSource("qiniu");
                rescourceService.save(rescource);
            }
        }
        return returnUrl.toString();
    }

    @Override
    public Boolean delete(String path) {
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("web_url",path);
        wrapper.eq("del_flag",false);
        wrapper.eq("source","qiniu");
        Rescource rescource = rescourceService.getOne(wrapper);
        path = rescource.getOriginalNetUrl();
        try {
            //删除七牛云上的文件
            getBucketManager().delete(getUploadInfo().getQiniuBucketName(), path);
            //删除数据库记录
            rescourceService.removeById(rescource.getId());
            return true;
        } catch (QiniuException e) {
            logger.error("删除七牛云上的文件失败",e);
            return false;
        }
    }

    @Override
    public String uploadNetFile(String url) {
        String fileName = RandomUtil.randomUUID();
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("source","qiniu");
        wrapper.eq("original_net_url",url);
        wrapper.eq("del_flag",false);
        Rescource rescource = rescourceService.getOne(wrapper);
        if(rescource != null){
            return rescource.getWebUrl();
        }
        StringBuilder key = new StringBuilder();
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getQiniuBasePath());
        try {
            String qiniuDir = getUploadInfo().getQiniuDir();

            if(StringUtils.isNotBlank(qiniuDir)){
                key.append(qiniuDir).append("/");
                returnUrl.append(qiniuDir).append("/");
            }
            key.append(fileName);
            returnUrl.append(fileName);
            FetchRet fetchRet = getBucketManager().fetch(url, getUploadInfo().getQiniuBucketName(),key.toString());
            rescource = new Rescource();
            rescource.setFileName(fetchRet.key);
            rescource.setFileSize(new java.text.DecimalFormat("#.##").format(fetchRet.fsize/1024)+"kb");
            rescource.setHash(fetchRet.hash);
            rescource.setFileType(fetchRet.mimeType);
            rescource.setWebUrl(returnUrl.toString());
            rescource.setSource("qiniu");
            rescource.setOriginalNetUrl(url);
            save(rescource);
        } catch (QiniuException e) {
            logger.error("上传网络文件失败",e);
            throw new MyException("上传网络文件失败"+e.getMessage());
        }
        return returnUrl.toString();
    }

    @Override
    public String uploadLocalImg(String localPath) {
        File file = new File(localPath);
        if(!file.exists()){
            throw new MyException("本地文件不存在");
        }
        QETag tag = new QETag();
        String hash;
        try {
            hash = tag.calcETag(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("计算文件hash失败",e);
            throw new MyException("计算文件hash失败"+e.getMessage());
        }
        LambdaQueryWrapper<Rescource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rescource::getHash,hash);
        wrapper.eq(Rescource::getSource,"qiniu");
        Rescource rescource = getOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        String filePath="",
                extName,
                name = RandomUtil.randomUUID();
        extName = file.getName().substring(
                file.getName().lastIndexOf("."));
        StringBuilder key = new StringBuilder();
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getQiniuBasePath());
        String qiniuDir = getUploadInfo().getQiniuDir();
        if(StringUtils.isNotBlank(qiniuDir)){
            key.append(qiniuDir).append("/");
            returnUrl.append(qiniuDir).append("/");
        }
        key.append(name).append(extName);
        returnUrl.append(name).append(extName);
        Response response;
        try {
            response = getUploadManager().put(file,key.toString(),getToken());
        } catch (QiniuException e) {
            logger.error("上传文件失败",e);
            throw new MyException("七牛上传文件失败"+e.getMessage());
        }
        if(response.isOK()){
            filePath = returnUrl.toString();
            rescource = new Rescource();
            rescource.setFileName(name+extName);
            QiniuFileUtil.saveSource(file, hash, rescource, filePath, extName);
            save(rescource);
        }
        return filePath;
    }

    @Override
    public String uploadBase64(String base64) {
        StringBuilder key = new StringBuilder();
        StringBuilder returnUrl = new StringBuilder(getUploadInfo().getQiniuBasePath());
        String qiniuDir = getUploadInfo().getQiniuDir();
        String fileName = RandomUtil.randomUUID();
        if(StringUtils.isNotBlank(qiniuDir)){
            key.append(qiniuDir).append("/");
            returnUrl.append(qiniuDir).append("/");
        }
        key.append(fileName);
        returnUrl.append(fileName);
        byte[] data = Base64.decodeBase64(base64);
        try {
            getUploadManager().put(data,key.toString(),getToken());
        } catch (IOException e) {
            logger.error("七牛使用Base64方法上传文件失败",e);
            throw new MyException("七牛使用Base64方法上传文件失败"+e.getMessage());
        }
        return returnUrl.toString();
    }

    @Override
    public Boolean testAccess(UploadInfo uploadInfo) {
        ClassPathResource classPathResource = new ClassPathResource("static/images/userface1.jpg");
        try {
            Auth auth = Auth.create(uploadInfo.getQiniuAccessKey(), uploadInfo.getQiniuSecretKey());
            String authstr =  auth.uploadToken(uploadInfo.getQiniuBucketName());
            InputStream inputStream = classPathResource .getInputStream();
            Response response = getUploadManager().put(inputStream,"test.jpg",authstr,null,null);
            return response.isOK();
        } catch (Exception e) {
            logger.error("测试七牛上传文件失败",e);
            return false;
        }
    }
}
