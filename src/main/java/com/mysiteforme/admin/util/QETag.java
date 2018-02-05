package com.mysiteforme.admin.util;

/**
 * Created by wangl on 2018/1/14.
 * todo:计算文件在七牛云存储上的 hash 值
 */

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class QETag {

    private final int CHUNK_SIZE = 1 << 22;

    public byte[] sha1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

    public String urlSafeBase64Encode(byte[] data) {
        String encodedString = DatatypeConverter.printBase64Binary(data);
        encodedString = encodedString.replace('+', '-').replace('/', '_');
        return encodedString;
    }

//    public String calcETag(String fileName) throws IOException,
//            NoSuchAlgorithmException {
//        String etag = "";
//        File file = new File(fileName);
//        if (!(file.exists() && file.isFile() && file.canRead())) {
//            System.err.println("Error: File not found or not readable");
//            return etag;
//        }
//        long fileLength = file.length();
//        FileInputStream inputStream = new FileInputStream(file);
//        if (fileLength <= CHUNK_SIZE) {
//            byte[] fileData = new byte[(int) fileLength];
//            inputStream.read(fileData, 0, (int) fileLength);
//            byte[] sha1Data = sha1(fileData);
//            int sha1DataLen = sha1Data.length;
//            byte[] hashData = new byte[sha1DataLen + 1];
//            System.arraycopy(sha1Data, 0, hashData, 1, sha1DataLen);
//            hashData[0] = 0x16;
//            etag = urlSafeBase64Encode(hashData);
//        } else {
//            int chunkCount = (int) (fileLength / CHUNK_SIZE);
//            if (fileLength % CHUNK_SIZE != 0) {
//                chunkCount += 1;
//            }
//            byte[] allSha1Data = new byte[0];
//            for (int i = 0; i < chunkCount; i++) {
//                byte[] chunkData = new byte[CHUNK_SIZE];
//                int bytesReadLen = inputStream.read(chunkData, 0, CHUNK_SIZE);
//                byte[] bytesRead = new byte[bytesReadLen];
//                System.arraycopy(chunkData, 0, bytesRead, 0, bytesReadLen);
//                byte[] chunkDataSha1 = sha1(bytesRead);
//                byte[] newAllSha1Data = new byte[chunkDataSha1.length
//                        + allSha1Data.length];
//                System.arraycopy(allSha1Data, 0, newAllSha1Data, 0,
//                        allSha1Data.length);
//                System.arraycopy(chunkDataSha1, 0, newAllSha1Data,
//                        allSha1Data.length, chunkDataSha1.length);
//                allSha1Data = newAllSha1Data;
//            }
//            byte[] allSha1DataSha1 = sha1(allSha1Data);
//            byte[] hashData = new byte[allSha1DataSha1.length + 1];
//            System.arraycopy(allSha1DataSha1, 0, hashData, 1,
//                    allSha1DataSha1.length);
//            hashData[0] = (byte) 0x96;
//            etag = urlSafeBase64Encode(hashData);
//        }
//        inputStream.close();
//        return etag;
//    }

    public String calcETag(InputStream inputStream,long fileLength) throws IOException,
            NoSuchAlgorithmException {
        String etag = "";
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
        inputStream.close();
        return etag;
    }

    public String calcETag(MultipartFile file) throws IOException,
            NoSuchAlgorithmException {
        String etag = "";
        long fileLength = file.getSize();
        InputStream inputStream = file.getInputStream();
        etag = calcETag(inputStream,fileLength);
        return etag;
    }

    public String calcETag(File file) throws IOException,
            NoSuchAlgorithmException {
        String tag = "";
        long fileLength = file.length();
        FileInputStream inputStream = new FileInputStream(file);
        tag = calcETag(inputStream,fileLength);
        return tag;
    }
}
