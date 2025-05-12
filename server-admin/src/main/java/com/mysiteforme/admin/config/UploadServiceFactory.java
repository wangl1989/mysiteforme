package com.mysiteforme.admin.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.UploadType;

@Component
public class UploadServiceFactory {
    private final Map<String, UploadService> serviceMap = new HashMap<>();

    public UploadServiceFactory(
        @Qualifier("qiniuService") UploadService qiniuService,
        @Qualifier("ossService") UploadService ossService,
        @Qualifier("localService") UploadService localService){
        serviceMap.put(UploadType.QINIU.getCode(), qiniuService);
        serviceMap.put(UploadType.OSS.getCode(), ossService);
        serviceMap.put(UploadType.LOCAL.getCode(), localService);
    }

    /**
     * 根据服务类型获取对应的上传服务实例
     *
     * @param type 服务类型，用于从服务映射中获取对应的上传服务实例
     * @return 返回对应服务类型的上传服务实例，如果映射中不存在该类型，则返回null
     */
    public UploadService getUploadService(String type){
        return serviceMap.get(type);
    }
}
