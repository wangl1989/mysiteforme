package com.mysiteforme.admin.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wangl on 2018/2/8.
 * todo: 本地上传
 */
public class LocalUtil implements UploadUtil {
    @Override
    public String upload(MultipartFile file) {
        return null;
    }

    @Override
    public Boolean delet(String path) {
        return null;
    }

    @Override
    public String uploadNetFile(String url) {
        return null;
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
