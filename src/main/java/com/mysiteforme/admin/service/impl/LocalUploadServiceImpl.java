/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:22:46
 * @ Description: 本地上传服务实现类 提供本地上传的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.QETag;
import com.mysiteforme.admin.util.ToolUtil;
import com.xiaoleilu.hutool.util.RandomUtil;



@Service("localService")
public class LocalUploadServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(LocalUploadServiceImpl.class);

    // 上传基础
    private final static String UPLOAD_PATH = "/static/upload/";

    /**
     * 上传文件到本地服务器
     * @param file 要上传的文件
     * @return 文件的Web访问URL
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 加密算法异常
     */
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
        String originalFilename = file.getOriginalFilename();
        if(StringUtils.isBlank(originalFilename)){
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件名错误").build();
        }
        String extName = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String contentType = file.getContentType();
        String filePath = ResourceUtils.getURL("classpath:").getPath() + UPLOAD_PATH ;
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            if (!targetFile.mkdirs()){
                logger.error("创建文件夹失败");
                throw MyException.builder().code(MyException.SERVER_ERROR).msg("创建文件夹失败").build();
            }
        }
        try (FileOutputStream out = new FileOutputStream(filePath + fileName)) {
            out.write(data);
            out.flush();
        }
        String webUrl = UPLOAD_PATH + fileName;
        rescource = new Rescource();
        rescource.setFileName(fileName);
        rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
        rescource.setHash(hash);
        rescource.setFileType(contentType);
        rescource.setWebUrl(webUrl);
        rescource.setSource("local");
        baseMapper.insert(rescource);
        return webUrl;
    }

    /**
     * 删除本地文件
     * @param path 文件路径
     * @return 删除是否成功
     */
    @Override
    public Boolean delete(String path) {
        // 获取项目的真实路径
        try {
            String projectPath = ResourceUtils.getURL("classpath:").getPath();
            path = projectPath+path;
            File file = new File(path);
            if(file.exists()){
                return file.delete();
            }else{
                return false;
            }
        }catch (IOException exception){
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件夹删除出现异常").build();
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
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("original_net_url",url);
        wrapper.eq("source","local");
        Rescource rescource = getOne(wrapper);
        if(rescource != null){
            return rescource.getWebUrl();
        }
        String extName = url.substring(url.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extName;
        String filePath = ResourceUtils.getURL("classpath:").getPath() + UPLOAD_PATH ;
        File uploadDir = new File(filePath);
        if(!uploadDir.exists()){
            if(!uploadDir.mkdirs()){
                throw MyException.builder().code(MyException.SERVER_ERROR).msg("创建文件夹失败").build();
            }
        }
        URL neturl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)neturl.openConnection();
        conn.connect();
        try (BufferedInputStream br = new BufferedInputStream(conn.getInputStream());
             FileOutputStream out = new FileOutputStream(filePath + fileName)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = br.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            
            String webUrl = UPLOAD_PATH + fileName;
            rescource = new Rescource();
            rescource.setFileName(fileName);
            rescource.setFileSize(new java.text.DecimalFormat("#.##").format(br.read(buf)/1024)+"kb");
            rescource.setHash(new QETag().calcETag(new File(filePath+fileName)));
            rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
            rescource.setWebUrl(webUrl);
            rescource.setOriginalNetUrl(url);
            rescource.setSource("local");
            baseMapper.insert(rescource);
            return webUrl;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 上传本地图片到服务器
     * @param localPath 本地图片路径
     * @return 文件的Web访问URL
     */
    @Override
    public String uploadLocalImg(String localPath) {
        File file = new File(localPath);
        if(!file.exists()){
            logger.error("本地文件不存在,上传路径为：{}",localPath);
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件不存在").build();
        }
        QETag tag = new QETag();
        String hash = null;
        try {
            hash = tag.calcETag(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("计算文件哈希值失败", e);
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件哈希计算失败").build();
        }
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        wrapper.eq("hash",hash);
        Rescource rescource = getOne(wrapper);
        if( rescource!= null){
            return rescource.getWebUrl();
        }
        StringBuilder sb = null;
        String filePath = null;
        try {
            sb = new StringBuilder(ResourceUtils.getURL("classpath:").getPath());
            filePath = sb.append(UPLOAD_PATH).toString();
        } catch (IOException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件上传流出现问题").build();
        }
        
        StringBuilder name = new StringBuilder(RandomUtil.randomUUID());
        StringBuilder returnUrl = new StringBuilder(UPLOAD_PATH);
        String  extName = file.getName().substring(file.getName().lastIndexOf("."));
        sb.append(name).append(extName);
        File uploadDir = null;
        if (filePath != null) {
            uploadDir = new File(filePath);
        }
        if (uploadDir != null && !uploadDir.exists()) {
            if(!uploadDir.mkdir()){
                logger.error("文件夹创建失败");
                throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件夹创建失败").build();
            }
        }
        try (InputStream input = new FileInputStream(file);
             FileOutputStream out = new FileOutputStream(sb.toString())) {
            byte[] buf = new byte[input.available()];
            int len;
            while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("文件上传流出现问题").build();
        }
        returnUrl.append(name).append(extName);
        return returnUrl.toString();
    }

    /**
     * 上传Base64编码的图片
     * @param base64 Base64编码的图片数据
     * @return 文件的Web访问URL，上传失败返回null
     */
    @Override
    public String uploadBase64(String base64) {
        StringBuilder webUrl = new StringBuilder(UPLOAD_PATH);
        String fileFormat = ToolUtil.getFileFormat(base64);
        try {
            // 去除base64字符串的前缀（如果有）
            if (base64.contains(",")) {
                base64 = base64.split(",")[1];
            }
            //Base64解码
            byte[] b = Base64.getDecoder().decode(base64);
            for(int i=0;i<b.length;++i) {
                if(b[i]<0) {//调整异常数据
                    b[i]+= (byte) 256;
                }
            }
            //生成jpeg图片
            String filePath = ResourceUtils.getURL("classpath:").getPath() + UPLOAD_PATH ;
            File targetFileDir = new File(filePath);
            if(!targetFileDir.exists()){
                if(targetFileDir.mkdirs()){
                    throw MyException.builder().code(MyException.SERVER_ERROR).msg("创建文件夹失败").build();
                }
            }
            StringBuilder sb = new StringBuilder(targetFileDir.getPath());
            sb.append(File.separator);
            String fileName = RandomUtil.randomUUID()+"."+ fileFormat;
            sb.append(fileName);
            String imgFilePath = sb.toString();//新生成的图片
            try (OutputStream out = Files.newOutputStream(Paths.get(imgFilePath))) {
                out.write(b);
                out.flush();
            }
            return webUrl.append(fileName).toString();
        }
        catch (IOException e)
        {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("上传Base64图片失败").build();
        }
    }

    /**
     * 测试上传配置是否可用
     * @param uploadInfo 上传配置信息
     * @return 配置是否可用
     */
    @Override
    public Boolean testAccess(UploadInfo uploadInfo) {
        return null;
    }
}
