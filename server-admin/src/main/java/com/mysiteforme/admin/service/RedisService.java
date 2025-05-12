package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.request.PageListRedisRequest;
import com.mysiteforme.admin.entity.response.RedisDataResponse;

public interface RedisService {

    IPage<RedisDataResponse> getRedisDataPageList(PageListRedisRequest request);

    void deleteRedisData(String key);
}
