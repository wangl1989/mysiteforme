package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.exception.MyException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service("qiniuService")
public class QiniuUploadServiceImpl extends UploadInfoServiceImpl implements UploadService {

    private UploadInfo uploadInfo = getOneInfo();

    private UploadManager getUploadManager(){
        Zone z = Zone.zone0();
        Configuration config = new Configuration(z);
        UploadManager uploadManager = new UploadManager(config);
        return uploadManager;
    }

    private BucketManager getBucketManager(){
        Zone z = Zone.zone0();
        Configuration config = new Configuration(z);
        Auth auth = Auth.create(uploadInfo.getQiniuAccessKey(), uploadInfo.getQiniuSecretKey());
        BucketManager bucketManager = new BucketManager(auth,config);
        return bucketManager;
    }

    private String getAuth(){
        if(uploadInfo == null){
            throw new MyException("上传信息配置不存在");
        }
        Auth auth = Auth.create(uploadInfo.getQiniuAccessKey(), uploadInfo.getQiniuSecretKey());
        String token = auth.uploadToken(uploadInfo.getQiniuBucketName());
        return token;
    }

    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        UploadInfo uploadInfo = getOneInfo();
        String fileName = "", extName = "", filePath = "";
        if (null != file && !file.isEmpty()) {
            extName = file.getOriginalFilename().substring(
                    file.getOriginalFilename().lastIndexOf("."));
            fileName = UUID.randomUUID() + extName;
            byte[] data = file.getBytes();
            QETag tag = new QETag();
            String hash = tag.calcETag(file);
            Rescource rescource = new Rescource();
            EntityWrapper<RestResponse> wrapper = new EntityWrapper<>();
            wrapper.eq("hash",hash);
            rescource = rescource.selectOne(wrapper);
            if( rescource!= null){
                return rescource.getWebUrl();
            }
            Response r = getUploadManager().put(data, fileName, getAuth());
            if (r.isOK()) {
                filePath = uploadInfo.getQiniuBasePath() + fileName;
                rescource = new Rescource();
                rescource.setFileName(fileName);
                rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
                rescource.setHash(hash);
                rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
                rescource.setWebUrl(filePath);
                rescource.setSource("qiniu");
                rescource.insert();
            }
        }
        return filePath;
    }

    @Override
    public Boolean delete(String path) {
        try {
            getBucketManager().delete(uploadInfo.getQiniuBucketName(), path);
            return true;
        } catch (QiniuException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException {
        String fileName = RandomUtil.randomUUID(),filePath="";
        try {
            FetchRet fetchRet = getBucketManager().fetch(url, uploadInfo.getQiniuBucketName());
            filePath = uploadInfo.getQiniuBasePath() + fetchRet.key;
            Rescource rescource = new Rescource();
            rescource.setFileName(fetchRet.key);
            rescource.setFileSize(new java.text.DecimalFormat("#.##").format(fetchRet.fsize/1024)+"kb");
            rescource.setHash(fetchRet.hash);
            rescource.setFileType(fetchRet.mimeType);
            rescource.setWebUrl(filePath);
            rescource.setSource("qiniu");
            rescource.insert();
        } catch (QiniuException e) {
            filePath = url;
            e.printStackTrace();
        }
        return filePath;
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
        rescource = rescource.selectOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        String filePath="",
                extName = "",
                name = UUID.randomUUID().toString();
        extName = file.getName().substring(
                file.getName().lastIndexOf("."));
        Response response = null;
        try {
            response = getUploadManager().put(file,name,getAuth());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        if(response.isOK()){
            filePath = uploadInfo.getQiniuBasePath() + name;
            rescource = new Rescource();
            rescource.setFileName(name);
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
        String fileName = RandomUtil.randomUUID(),filePath;
        byte[] data = Base64.decodeBase64(base64);
        try {
            getUploadManager().put(data,fileName,getAuth());
        } catch (IOException e) {
            e.printStackTrace();
        }
        filePath = uploadInfo.getQiniuBasePath()+fileName;
        return filePath;
    }
}
