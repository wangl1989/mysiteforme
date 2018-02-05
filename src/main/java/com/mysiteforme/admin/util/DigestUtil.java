package com.mysiteforme.admin.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangl on 2018/1/14.
 * todo: 计算文件的MD5及SHA1值
 */
public class DigestUtil {

    public static String getFileSha1(MultipartFile file) {

        MessageDigest digest = null;
        InputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("SHA1");
            in = file.getInputStream();
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(20);
    }
    /**
     *
     * @param file
     * @param algorithm 所请求算法的名称  for example: MD5, SHA1, SHA-256, SHA-384, SHA-512 etc.
     * @return
     */
    public static String getFileMD5(File file, String algorithm) {

        if (!file.isFile()) {
            return null;
        }

        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;

        try {
            digest = MessageDigest.getInstance(algorithm);
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param dirFile
     * @param algorithm 所请求算法的名称  for example: MD5, SHA1, SHA-256, SHA-384, SHA-512 etc.
     * @param listChild 是否递归子目录中的文件
     * @return
     */
    public static Map<String, String> getDirMD5(File dirFile, String algorithm, boolean listChild) {

        if (!dirFile.isDirectory()) {
            return null;
        }

        // <filepath,algCode>
        Map<String, String> pathAlgMap = new HashMap<String, String>();
        String algCode;
        File files[] = dirFile.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory() && listChild) {
                pathAlgMap.putAll(getDirMD5(file, algorithm, listChild));
            } else {
                algCode = getFileMD5(file, algorithm);
                if (algCode != null) {
                    pathAlgMap.put(file.getPath(), algCode);
                }
            }
        }
        return pathAlgMap;
    }
}
