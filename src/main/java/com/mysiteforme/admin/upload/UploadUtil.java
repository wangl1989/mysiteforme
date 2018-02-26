package com.mysiteforme.admin.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wangl on 2018/2/8.
 * todo: 云上传工具
 */
public interface UploadUtil {
    /**
     * 文件上传方法
     * @param file MultipartFile文件对象
     * @return  文件在云上的地址
     */
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException;

    /**
     * 删除已经上传到云上的文件
     * @param path 文件地址
     * @return 是否删除成功
     */
    public Boolean delet(String path);

    /**
     * 上传网络文件
     * @param url 网络文件的地址
     * @return  文件在云上的地址
     */
    public String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException;

    /**
     * 上传本地指定路径的图片
     * @param localPath  (指的是用户的)本地路径
     * @return
     */
    public String uploadLocalImg(String localPath);

    /**
     * 上传base64格式的文件
     * @param base64 文件的base64编码
     * @return
     */
    public String uploadBase64(String base64);
}
