/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:30
 * @ Description: 站点服务实现类 提供站点的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.entity.DTO.*;
import com.mysiteforme.admin.entity.UploadBaseInfo;
import com.mysiteforme.admin.entity.response.SiteUploadTypeResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.UploadBaseInfoService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.SiteDao;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.service.SiteService;

import java.util.List;

@Slf4j
@Service("siteService")
@Transactional(rollbackFor = Exception.class)
public class SiteServiceImpl extends ServiceImpl<SiteDao, Site> implements SiteService {

    private final UploadBaseInfoService uploadBaseInfoService;

    private final ObjectMapper objectMapper;

    public SiteServiceImpl(UploadBaseInfoService uploadBaseInfoService,ObjectMapper objectMapper) {
        this.uploadBaseInfoService = uploadBaseInfoService;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取当前站点信息
     * 结果会被缓存
     * 只返回未删除的站点
     * @return 站点信息对象
     */
    @Cacheable(value = "system::site",key = "'currentSite'")
    @Override
    public Site getCurrentSite() {
        LambdaQueryWrapper<Site> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Site::getDelFlag,false);
        List<Site> siteList = list(wrapper);
        if(siteList.isEmpty()){
            throw MyException.builder().systemError(MessageConstants.System.SYSTEM_SITE_CONFIG_ERROR).build();
        }
        return siteList.get(0);
    }

    /**
     * 更新站点信息
     * 同时清除站点缓存
     * @param site 要更新的站点信息对象
     */
    @CacheEvict(value = "system::site",key = "'currentSite'")
    @Override
    public void updateSite(Site site) {
        baseMapper.updateById(site);
    }

    @Override
    public List<SiteUploadTypeResponse> getSiteUploadTypeList() {
        List<UploadBaseInfo> list = uploadBaseInfoService.lambdaQuery()
                .eq(UploadBaseInfo::getDelFlag, false)
                .list();
        return list.stream().map(info -> {
            SiteUploadTypeResponse response = new SiteUploadTypeResponse();
            response.setRemarks(info.getRemarks());
            response.setTypeCode(info.getType());
            return response;
        }).toList();
    }

    @Override
    public Boolean checkWebService(String key){
        String result = HttpUtil.get(String.format(Constants.WEB_SERVICE_LOCATION_API,key,""));
        if(StringUtils.isBlank(result)){
            return false;
        }else{
            try {
                JavaType type = objectMapper.getTypeFactory().constructType(WebServiceDTO.class);
                WebServiceDTO webService = objectMapper.readValue(result,type);
                log.debug("webservice的值为:{}",webService);
                if(webService != null){
                    if(webService.getStatus() == 0){
                        return true;
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("webservice检测是否可用时，json转换异常");
                throw MyException.builder().businessError(MessageConstants.Site.SITE_CHECK_WEB_SERVICE_KEY_ERROR).build();
            }
        }
        return false;
    }

}
