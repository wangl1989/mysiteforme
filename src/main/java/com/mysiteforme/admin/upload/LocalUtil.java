package com.mysiteforme.admin.upload;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.util.QETag;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by wangl on 2018/2/8.
 * todo: 本地上传
 */
@Component
public class LocalUtil implements UploadUtil {

    @Override
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String extName = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String contentType = file.getContentType();
        String filePath = "classpath:static/upload/";
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        byte[] data = file.getBytes();
        QETag tag = new QETag();
        Rescource rescource = new Rescource();
        String hash = tag.calcETag(file);
        EntityWrapper<RestResponse> wrapper = new EntityWrapper<>();
        wrapper.eq("hash",hash);
        rescource = rescource.selectOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(data);
        out.flush();
        out.close();
        String webUrl = "/static/upload/"+fileName;
        rescource.setFileName(fileName);
        rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
        rescource.setHash(hash);
        rescource.setFileType(contentType);
        rescource.setWebUrl(webUrl);
        rescource.setSource("本地上传");
        rescource.insert();
        return webUrl;
    }

    @Override
    public Boolean delet(String path) {
        path = path.replaceFirst("\\/","classpath:");
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        return null;
    }

    @Override
    public String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException {
        Rescource rescource = new Rescource();
        EntityWrapper<RestResponse> wrapper = new EntityWrapper<>();
        wrapper.eq("remarks",url);
        rescource = rescource.selectOne(wrapper);
        if(rescource != null){
            return rescource.getWebUrl();
        }
        String extName = url.substring(url.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String filePath = "classpath:static/upload/";
        URL neturl=new URL(url);
        HttpURLConnection conn=(HttpURLConnection)neturl.openConnection();
        conn.connect();
        BufferedInputStream br = new BufferedInputStream(conn.getInputStream());
        byte[] buf = new byte[1024];
        int len = 0;
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        while ((len = br.read(buf)) > 0) out.write(buf, 0, len);
        br.close();
        out.flush();
        out.close();
        conn.disconnect();
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
            rescource.setWebUrl(filePath);
            rescource.setSource("本地上传");
            rescource.insert();
        }
        return webUrl;
    }

    @Override
    public String uploadLocalImg(String localPath) {
        return null;
    }

    @Override
    public String uploadBase64(String base64) {
        return null;
    }
}
