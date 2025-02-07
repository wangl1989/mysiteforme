package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.UploadInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface UploadService {
    /**
     * 文件上传方法
     * @param file MultipartFile文件对象
     * @return  文件在云上的地址
     */
    String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException;

    /**
     * 删除已经上传到云上的文件
     * @param path 文件地址
     * @return 是否删除成功
     */
    Boolean delete(String path);

    /**
     * 上传网络文件
     * @param url 网络文件的地址
     * @return  文件在云上的地址
     */
    String uploadNetFile(String url) throws IOException, NoSuchAlgorithmException;

    /**
     * 上传本地指定路径的图片
     */
    String uploadLocalImg(String localPath);

    /**
     * 上传base64格式的文件
     */
    String uploadBase64(String base64);

    /**
     * 上传测试
     */
    Boolean testAccess(UploadInfo uploadInfo);
}
