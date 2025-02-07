package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.xiaoleilu.hutool.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service("localService")
public class LocalUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(LocalUploadServiceImpl.class);

    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        byte[] data = file.getBytes();
        QETag tag = new QETag();
        String hash = tag.calcETag(file);
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("hash",hash);
        wrapper.eq("source","local");
        wrapper.eq("del_flag",false);
        Rescource rescource = getOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        String extName = Objects.requireNonNull(file.getOriginalFilename()).substring(
                file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String contentType = file.getContentType();
        String filePath = ResourceUtils.getURL("classpath:").getPath() + "static/upload/" ;
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            if (!targetFile.mkdirs()){
                logger.error("创建文件夹失败");
                throw new MyException("创建文件夹失败");
            }
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(data);
        out.flush();
        out.close();
        String webUrl = "/static/upload/"+fileName;
        rescource = new Rescource();
        rescource.setFileName(fileName);
        rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
        rescource.setHash(hash);
        rescource.setFileType(contentType);
        rescource.setWebUrl(webUrl);
        rescource.setSource("local");
        save(rescource);
        return webUrl;
    }

    @Override
    public Boolean delete(String path) {
        path = path.replaceFirst("/","classpath:");
        File file = new File(path);
        if(file.exists()){
            return file.delete();
        }else{
            return false;
        }
    }

    @Override
    public String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException {
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("original_net_url",url);
        wrapper.eq("source","local");
        Rescource rescource = getOne(wrapper);
        if(rescource != null){
            return rescource.getWebUrl();
        }
        String extName = url.substring(url.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String filePath = ResourceUtils.getURL("classpath:").getPath() + "static/upload/" ;
        File uploadDir = new File(filePath);
        if(!uploadDir.exists()){
            if(!uploadDir.mkdirs()){
                throw new MyException("创建文件夹失败");
            }
        }
        URL neturl=new URL(url);
        HttpURLConnection conn=(HttpURLConnection)neturl.openConnection();
        conn.connect();
        BufferedInputStream br = new BufferedInputStream(conn.getInputStream());
        byte[] buf = new byte[1024];
        int len;
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        while ((len = br.read(buf)) > 0) out.write(buf, 0, len);
        File targetFile = new File(filePath+fileName);
        String webUrl = "";
        if(targetFile.exists()){
            webUrl = "/static/upload/"+fileName;
            rescource = new Rescource();
            QETag tag = new QETag();
            rescource.setFileName(fileName);
            rescource.setFileSize(new java.text.DecimalFormat("#.##").format(br.read(buf)/1024)+"kb");
            rescource.setHash(tag.calcETag(targetFile));
            rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
            rescource.setWebUrl(webUrl);
            rescource.setOriginalNetUrl(url);
            rescource.setSource("local");
            save(rescource);
        }
        br.close();
        out.flush();
        out.close();
        conn.disconnect();
        return webUrl;
    }

    @Override
    public String uploadLocalImg(String localPath) {
        File file = new File(localPath);
        if(!file.exists()){
            logger.error("本地文件不存在,上传路径为：{}",localPath);
            throw new MyException("本地文件不存在");
        }
        QETag tag = new QETag();
        String hash = null;
        try {
            hash = tag.calcETag(file);
        } catch (Exception e) {
            logger.error("上传七牛云发生异常", e);
        }
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("hash",hash);
        Rescource rescource = getOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        StringBuilder sb = null;
        try {
            sb = new StringBuilder(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            logger.error("获取文件出现异常", e);
        }
        String filePath = null;
        if (sb != null) {
            filePath = sb.append("static/upload/").toString();
        }
        StringBuilder name = new StringBuilder(RandomUtil.randomUUID());
        StringBuilder returnUrl = new StringBuilder("/static/upload/");
        String  extName;
        extName = file.getName().substring(
                file.getName().lastIndexOf("."));
        if (sb != null) {
            sb.append(name).append(extName);
        }
        File uploadDir = null;
        if (filePath != null) {
            uploadDir = new File(filePath);
        }
        if (uploadDir != null && !uploadDir.exists()) {
            if(!uploadDir.mkdir()){
                logger.error("文件夹创建失败");
                throw new MyException("文件夹创建失败");
            }
        }
        try {
            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[input.available()];
            int len;
            FileOutputStream out = null;
            if (sb != null) {
                out = new FileOutputStream(sb.toString());
            }
            while ((len = input.read(buf)) > 0) if (out != null) {
                out.write(buf, 0, len);
            }
            input.close();
            if (out != null) {
                out.flush();
            }
            if (out != null) {
                out.close();
            }
        } catch (FileNotFoundException e) {
            logger.error("文件未找到", e);
        } catch (IOException e) {
            logger.error("文件上传流出现问题", e);
        }
        returnUrl.append(name).append(extName);
        return returnUrl.toString();
    }

    @Override
    public String uploadBase64(String base64) {
        StringBuilder webUrl=new StringBuilder("/static/upload/");
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(base64);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+= (byte) 256;
                }
            }
            //生成jpeg图片
            String filePath = ResourceUtils.getURL("classpath:").getPath() + "static/upload/" ;
            File targetFileDir = new File(filePath);
            if(!targetFileDir.exists()){
                if(targetFileDir.mkdirs()){
                    throw new MyException("创建文件夹失败");
                }
            }
            StringBuilder sb = new StringBuilder(filePath);
            sb.append(RandomUtil.randomUUID());
            sb.append(".jpg");
            String imgFilePath = sb.toString();//新生成的图片
            OutputStream out = Files.newOutputStream(Paths.get(imgFilePath));
            out.write(b);
            out.flush();
            out.close();
            return webUrl.append(sb).toString();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public Boolean testAccess(UploadInfo uploadInfo) {
        return null;
    }
}
