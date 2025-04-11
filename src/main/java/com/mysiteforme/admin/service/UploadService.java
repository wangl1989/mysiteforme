/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:31:06
 * @ Description: 上传Service
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.UploadInfo;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface UploadService {
    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 文件的访问URL
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 加密算法异常
     */
    String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException;

    /**
     * 删除文件
     * @param path 文件路径
     * @return 删除是否成功
     */
    Boolean delete(String path);

    /**
     * 上传网络文件
     * @param url 网络文件URL
     * @return 上传后的文件访问URL
     * @throws IOException IO异常
     */
    String uploadNetFile(String url) throws IOException,NoSuchAlgorithmException;

    /**
     * 上传本地图片
     * @param localPath 本地文件路径
     * @return 上传后的文件访问URL
     */
    String uploadLocalImg(String localPath);

    /**
     * 上传Base64编码的图片
     * @param base64 Base64编码的图片数据
     * @return 上传后的文件访问URL
     */
    String uploadBase64(String base64);

    /**
     * 测试上传配置是否可用
     * @param uploadInfo 上传配置信息
     * @return 配置是否可用
     */
    Boolean testAccess(UploadInfo uploadInfo);


    /**
     * 测试基础信息是否可用
     * @param uploadBaseInfo 上传配置信息
     * @return 配置是否可用
     */
    Boolean testBaseInfoAccess(UploadBaseInfo uploadBaseInfo);


}
