package com.mysiteforme.admin.util;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wangl on 2018/1/14.
 * todo: 计算文件的MD5及SHA1值
 */
public class DigestUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ToolUtil.class);

    public static String getFileSha1(MultipartFile file) {

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
        } catch (Exception e) {
            LOGGER.error("getFileSha1 error", e);
            return null;
        }

        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(20);
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
        } catch (Exception e) {
            LOGGER.error("getFileMD5 error", e);
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
