package com.mysiteforme.admin.util;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

/**
 * 文件摘要计算工具类
 * 提供文件MD5、SHA1等摘要算法的计算
 * 
 * @author wangl
 */
public class DigestUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ToolUtil.class);

    /**
     * 计算文件的MD5值
     * @param file 要计算的文件
     * @return 文件的MD5值
     */
    public static String fileMD5(MultipartFile file) {
        MessageDigest digest;
        InputStream in;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = file.getInputStream();
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            LOGGER.error("文件读写失败", e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("不支持的MD5算法", e);
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 计算文件的SHA1值
     * @param file 要计算的文件
     * @return 文件的SHA1值
     */
    public static String fileSHA1(MultipartFile file) {
        MessageDigest digest;
        InputStream in;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("SHA1");
            in = file.getInputStream();
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            LOGGER.error("文件读写失败", e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("不支持的SHA1算法", e);
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(20);
    }

    /**
     * 计算字符串的MD5值
     * @param str 要计算的字符串
     * @return 字符串的MD5值
     */
    public static String md5(String str) {
        // ... existing code ...
        return null; // Placeholder return, actual implementation needed
    }

    /**
     *
     */
    public static String getFileMD5(File file, String algorithm) {

        if (!file.isFile()) {
            return null;
        }

        MessageDigest digest;
        FileInputStream in;
        byte[] buffer = new byte[1024];
        int len;

        try {
            digest = MessageDigest.getInstance(algorithm);
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            LOGGER.error("文件读写失败", e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("不支持的摘要算法: {}", algorithm, e);
            return null;
        }

        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     */
    public static Map<String, String> getDirMD5(File dirFile, String algorithm, boolean listChild) {

        if (!dirFile.isDirectory()) {
            return null;
        }

        // <filepath,algCode>
        Map<String, String> pathAlgMap = Maps.newHashMap();
        String algCode;
        File[] files = dirFile.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && listChild) {
                    pathAlgMap.putAll(Objects.requireNonNull(getDirMD5(file, algorithm, true)));
                } else {
                    algCode = getFileMD5(file, algorithm);
                    if (algCode != null) {
                        pathAlgMap.put(file.getPath(), algCode);
                    }
                }
            }
        }
        return pathAlgMap;
    }
}
