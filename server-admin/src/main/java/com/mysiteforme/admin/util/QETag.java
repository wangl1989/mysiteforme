/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:02:46
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 14:06:11
 * @ Description: 计算文件在七牛云存储上的 hash 值
 */

package com.mysiteforme.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.springframework.web.multipart.MultipartFile;

public class QETag {

    private final int CHUNK_SIZE = 1 << 22;

    /**
     * 计算给定字节数组的SHA-1哈希值
     *
     * @param data 要计算哈希值的字节数组
     * @return 计算得到的SHA-1哈希值的字节数组
     * @throws NoSuchAlgorithmException 如果SHA-1算法不可用
     */
    public byte[] sha1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

    /**
     * 对字节数组进行URL安全的Base64编码
     *
     * @param data 要编码的字节数组
     * @return 编码后的字符串
     */
    public String urlSafeBase64Encode(byte[] data) {
        String encodedString = DatatypeConverter.printBase64Binary(data);
        encodedString = encodedString.replace('+', '-').replace('/', '_');
        return encodedString;
    }

    /**
     * 计算输入流的ETag值
     *
     * @param inputStream 文件的输入流
     * @param fileLength 文件的长度
     * @return 计算得到的ETag值
     * @throws IOException 如果读取文件时发生IO错误
     * @throws NoSuchAlgorithmException 如果SHA-1算法不可用
     */
    private String calcETag(InputStream inputStream,long fileLength) throws IOException,
            NoSuchAlgorithmException {
        String etag;
        try (inputStream) {
            if (fileLength <= CHUNK_SIZE) {
                byte[] fileData = new byte[(int) fileLength];
                inputStream.read(fileData, 0, (int) fileLength);
                byte[] sha1Data = sha1(fileData);
                int sha1DataLen = sha1Data.length;
                byte[] hashData = new byte[sha1DataLen + 1];
                System.arraycopy(sha1Data, 0, hashData, 1, sha1DataLen);
                hashData[0] = 0x16;
                etag = urlSafeBase64Encode(hashData);
            } else {
                int chunkCount = (int) (fileLength / CHUNK_SIZE);
                if (fileLength % CHUNK_SIZE != 0) {
                    chunkCount += 1;
                }
                byte[] allSha1Data = new byte[0];
                for (int i = 0; i < chunkCount; i++) {
                    byte[] chunkData = new byte[CHUNK_SIZE];
                    int bytesReadLen = inputStream.read(chunkData, 0, CHUNK_SIZE);
                    byte[] bytesRead = new byte[bytesReadLen];
                    System.arraycopy(chunkData, 0, bytesRead, 0, bytesReadLen);
                    byte[] chunkDataSha1 = sha1(bytesRead);
                    byte[] newAllSha1Data = new byte[chunkDataSha1.length
                            + allSha1Data.length];
                    System.arraycopy(allSha1Data, 0, newAllSha1Data, 0,
                            allSha1Data.length);
                    System.arraycopy(chunkDataSha1, 0, newAllSha1Data,
                            allSha1Data.length, chunkDataSha1.length);
                    allSha1Data = newAllSha1Data;
                }
                byte[] allSha1DataSha1 = sha1(allSha1Data);
                byte[] hashData = new byte[allSha1DataSha1.length + 1];
                System.arraycopy(allSha1DataSha1, 0, hashData, 1,
                        allSha1DataSha1.length);
                hashData[0] = (byte) 0x96;
                etag = urlSafeBase64Encode(hashData);
            }
        }
        return etag;
    }

    /**
     * 计算MultipartFile的ETag值
     *
     * @param file MultipartFile对象
     * @return 计算得到的ETag值
     */
    public String calcETag(MultipartFile file) throws IOException,
            NoSuchAlgorithmException {
        long fileLength = file.getSize();
        InputStream inputStream = file.getInputStream();
        return calcETag(inputStream,fileLength);
    }

    /**
     * 计算File对象的ETag值
     *
     * @param file File对象
     * @return 计算得到的ETag值
     */
    public String calcETag(File file) throws IOException,
            NoSuchAlgorithmException {
        String tag = "";
        long fileLength = file.length();
        FileInputStream inputStream = new FileInputStream(file);
        tag = calcETag(inputStream,fileLength);
        return tag;
    }

    /**
     * 计算文件的ETag值
     *
     * @param fileName 文件名
     * @return 计算得到的ETag值
     */
    public String calcETag(String fileName) throws IOException,
            NoSuchAlgorithmException{
        String etag = "";
        File file = new File(fileName);
        if (!(file.exists() && file.isFile() && file.canRead())) {
            System.err.println("Error: File not found or not readable");
            return etag;
        }
        long fileLength = file.length();
        FileInputStream inputStream = new FileInputStream(file);
        etag = calcETag(inputStream,fileLength);
        return etag;
    }
}
