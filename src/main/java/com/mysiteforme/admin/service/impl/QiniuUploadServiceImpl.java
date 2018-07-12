package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.RescourceService;
import com.mysiteforme.admin.service.UploadInfoService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.mysiteforme.admin.util.RestResponse;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;
import com.xiaoleilu.hutool.util.RandomUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

@Service("qiniuService")
public class QiniuUploadServiceImpl implements UploadService {

    @Autowired
    private UploadInfoService uploadInfoService;

    @Autowired
    private RescourceService rescourceService;

    private UploadInfo getUploadInfo(){
        return uploadInfoService.getOneInfo();
    }

    private UploadManager getUploadManager(){
        Zone z = Zone.zone0();
        Configuration config = new Configuration(z);
        return new UploadManager(config);
    }

    private BucketManager getBucketManager(){
        Zone z = Zone.zone0();
        Configuration config = new Configuration(z);
        Auth auth = Auth.create(getUploadInfo().getQiniuAccessKey(), getUploadInfo().getQiniuSecretKey());
        return new BucketManager(auth,config);
    }

    private String getAuth(){
        if(getUploadInfo() == null){
            throw new MyException("上传信息配置不存在");
        }
        Auth auth = Auth.create(getUploadInfo().getQiniuAccessKey(), getUploadInfo().getQiniuSecretKey());
        return auth.uploadToken(getUploadInfo().getQiniuBucketName());
    }

    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileName = "", extName = "", filePath = "";
        StringBuffer key = new StringBuffer();
        StringBuffer returnUrl = new StringBuffer(getUploadInfo().getQiniuBasePath());
        if (null != file && !file.isEmpty()) {
            extName = file.getOriginalFilename().substring(
                    file.getOriginalFilename().lastIndexOf("."));
            fileName = RandomUtil.randomUUID() + extName;
            byte[] data = file.getBytes();
            QETag tag = new QETag();
            String hash = tag.calcETag(file);
            Rescource rescource = new Rescource();
            EntityWrapper<RestResponse> wrapper = new EntityWrapper<>();
            wrapper.eq("hash",hash);
            wrapper.eq("source","qiniu");
            rescource = rescource.selectOne(wrapper);
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
            Response r = getUploadManager().put(data, key.toString(), getAuth());
            if (r.isOK()) {
                filePath = getUploadInfo().getQiniuBasePath() + fileName;
                rescource = new Rescource();
                rescource.setFileName(fileName);
                rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
                rescource.setHash(hash);
                rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
                rescource.setWebUrl(returnUrl.toString());
                rescource.setSource("qiniu");
                rescourceService.insert(rescource);
            }
        }
        return returnUrl.toString();
    }

    @Override
    public Boolean delete(String path) {
        EntityWrapper<Rescource> wrapper = new EntityWrapper<>();
        wrapper.eq("web_url",path);
        wrapper.eq("del_flag",false);
        wrapper.eq("source","qiniu");
        Rescource rescource = rescourceService.selectOne(wrapper);
        path = rescource.getOriginalNetUrl();
        try {
            getBucketManager().delete(getUploadInfo().getQiniuBucketName(), path);
            rescourceService.deleteById(rescource);
            return true;
        } catch (QiniuException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException {
        String fileName = RandomUtil.randomUUID();
        EntityWrapper<Rescource> wrapper = new EntityWrapper<>();
        wrapper.eq("source","qiniu");
        wrapper.eq("original_net_url",url);
        wrapper.eq("del_flag",false);
        Rescource rescource = rescourceService.selectOne(wrapper);
        if(rescource != null){
            return rescource.getWebUrl();
        }
        StringBuffer key = new StringBuffer();
        StringBuffer returnUrl = new StringBuffer(getUploadInfo().getQiniuBasePath());
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
            rescource.insert();
        } catch (QiniuException e) {
            e.printStackTrace();
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
        String hash = null;
        try {
            hash = tag.calcETag(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Rescource rescource = new Rescource();
        EntityWrapper<RestResponse> wrapper = new EntityWrapper<>();
        wrapper.eq("hash",hash);
        wrapper.eq("source","qiniu");
        rescource = rescource.selectOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        String filePath="",
                extName = "",
                name = RandomUtil.randomUUID();
        extName = file.getName().substring(
                file.getName().lastIndexOf("."));
        StringBuffer key = new StringBuffer();
        StringBuffer returnUrl = new StringBuffer(getUploadInfo().getQiniuBasePath());
        String qiniuDir = getUploadInfo().getQiniuDir();
        if(StringUtils.isNotBlank(qiniuDir)){
            key.append(qiniuDir).append("/");
            returnUrl.append(qiniuDir).append("/");
        }
        key.append(name).append(extName);
        returnUrl.append(name).append(extName);
        Response response = null;
        try {
            response = getUploadManager().put(file,key.toString(),getAuth());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        if(response.isOK()){
            filePath = returnUrl.toString();
            rescource = new Rescource();
            rescource.setFileName(name+extName);
            rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.length()/1024)+"kb");
            rescource.setHash(hash);
            rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
            rescource.setWebUrl(filePath);
            rescource.setSource("qiniu");
            rescource.insert();
        }
        return filePath;
    }

    @Override
    public String uploadBase64(String base64) {
        StringBuffer key = new StringBuffer();
        StringBuffer returnUrl = new StringBuffer(getUploadInfo().getQiniuBasePath());
        String qiniuDir = getUploadInfo().getQiniuDir();
        String fileName = RandomUtil.randomUUID(),filePath;
        if(StringUtils.isNotBlank(qiniuDir)){
            key.append(qiniuDir).append("/");
            returnUrl.append(qiniuDir).append("/");
        }
        key.append(fileName);
        returnUrl.append(fileName);
        byte[] data = Base64.decodeBase64(base64);
        try {
            getUploadManager().put(data,key.toString(),getAuth());
        } catch (IOException e) {
            e.printStackTrace();
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
            if(response.isOK()){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
