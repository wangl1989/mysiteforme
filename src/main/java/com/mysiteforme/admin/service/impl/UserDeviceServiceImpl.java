package com.mysiteforme.admin.service.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.entity.DTO.PconlineDTO;
import com.mysiteforme.admin.entity.DTO.WebServiceAdInfoDTO;
import com.mysiteforme.admin.entity.DTO.WebServiceDTO;
import com.mysiteforme.admin.entity.DTO.WebServiceResultDTO;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.dao.UserDeviceDao;
import com.mysiteforme.admin.entity.VO.DeviceInfoVO;
import com.mysiteforme.admin.entity.VO.DeviceTokenInfo;
import com.mysiteforme.admin.entity.request.PageListUserDeviceRequest;
import com.mysiteforme.admin.entity.response.LocationResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.TokenStorageService;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.service.UserDeviceService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.ResultCode;
import com.mysiteforme.admin.util.ToolUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceDao, UserDevice> implements UserDeviceService {

    private final TokenStorageService tokenStorageService;

    private final SiteService siteService;

    private final ObjectMapper objectMapper;

    @Override
    public IPage<UserDevice> selectPageUserDevice(PageListUserDeviceRequest request) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        if(request != null){
            if(StringUtils.isNotBlank(request.getDeviceName())) {
                wrapper.like(UserDevice::getDeviceName, request.getDeviceName());
            }
            if(StringUtils.isNotBlank(request.getDeviceId())){
                wrapper.eq(UserDevice::getDeviceId,request.getDeviceId());
            }
            if(StringUtils.isNotBlank(request.getDeviceName())){
                wrapper.like(UserDevice::getDeviceName,request.getDeviceName());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), UserDevice::getCreateDate);
        }else{
            request = new PageListUserDeviceRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

    @Override
    public LocationResponse getCurrrenntLocation() {
        HttpServletRequest request = getCurrentRequest();
        String ip = request.getRemoteAddr();
        if("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)){
            ip = "";
        }
        Site site = siteService.getCurrentSite();
        if(site == null || StringUtils.isBlank(site.getWebServicekey())){
            return backLocation(ip);
        } else {
            String result = HttpUtil.get(String.format(Constants.WEB_SERVICE_LOCATION_API,site.getWebServicekey(),StringUtils.isNotBlank(ip)? ip:""));
            if(StringUtils.isNotBlank(result)){
                try {
                    JavaType type = objectMapper.getTypeFactory().constructType(WebServiceDTO.class);
                    WebServiceDTO webService = objectMapper.readValue(result,type);
                    if(webService != null){
                        if(webService.getStatus() == 0){
                            WebServiceResultDTO res = webService.getResult();
                            if(res != null){
                                LocationResponse response = new LocationResponse();
                                response.setIp(res.getIp());
                                WebServiceAdInfoDTO info = res.getAd_info();
                                if(info != null){
                                    response.setProvince(info.getProvince());
                                    response.setCity(info.getCity());
                                    response.setDistrict(info.getDistrict());
                                    return response;
                                }
                            }else{
                                return backLocation(ip);
                            }
                        }else{
                            return backLocation(ip);
                        }
                    }else{
                        return backLocation(ip);
                    }
                } catch (JsonProcessingException e) {
                    log.error("webservice检测是否可用时，json转换异常");
                    throw MyException.builder().businessError(MessageConstants.Site.SITE_CURRENT_LOCATION_WEB_SERVICE_ERROR).build();
                }
            }
        }
        return null;
    }

    private LocationResponse backLocation(String ip){
        String result = HttpUtil.get(String.format(Constants.PCON_LINE_API,StringUtils.isNotBlank(ip)? ip:""));
        if(StringUtils.isNotBlank(result)){
            try {
                JavaType type = objectMapper.getTypeFactory().constructType(PconlineDTO.class);
                PconlineDTO pconline = objectMapper.readValue(result,type);
                if(pconline != null){
                    LocationResponse response = new LocationResponse();
                    response.setIp(pconline.getIp());
                    response.setProvince(pconline.getPro());
                    response.setCity(pconline.getCity());
                    response.setDistrict(pconline.getRegion());
                    return response;
                }
            } catch (JsonProcessingException e) {
                log.error("pconline检测是否可用时，json转换异常");
                throw MyException.builder().businessError(MessageConstants.Site.SITE_CURRENT_LOCATION_PCON_LINE_ERROR).build();
            }
        }
        return null;
    }

    // 获取当前请求的 HttpServletRequest
    public static HttpServletRequest getCurrentRequest() {
        try {
            // 从 RequestContextHolder 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (IllegalStateException e) {
            // 当前线程没有绑定请求（如异步任务、定时任务等）
            log.error("获取HttpServletRequest出现异常:{}",e.getMessage());
            throw MyException.builder().businessError(MessageConstants.System.SYSTEM_GET_HTTP_SERVLET_REQUEST_ERROR).build();
        }
    }

    @Override
    public void handleDeviceLogin(String userName, UserDevice deviceInfo) {
        // 设置设备信息
        if(StringUtils.isNotBlank(userName)) {
            LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserDevice::getUserName,userName);
            wrapper.eq(UserDevice::getDeviceId,deviceInfo.getDeviceId());
            // 1. 更新或创建MySQL中的设备记录
            List<UserDevice> userDeviceList = baseMapper.selectList(wrapper);
            UserDevice userDevice;
            if(userDeviceList.isEmpty()){
                userDevice = new UserDevice();
            }else{
                userDevice = userDeviceList.get(0);
            }
            userDevice.setUserName(userName);
            BeanUtils.copyProperties(deviceInfo,userDevice);
            userDevice.setUpdateDate(new Date());
            baseMapper.insertOrUpdate(userDevice);
        }else {
            log.error("未能获取到用户名");
            throw MyException.builder().code(ResultCode.INTERNAL_ERROR).msg(MessageConstants.System.SYSTEM_ERROR).build();
        }
    }

    @Override
    public List<UserDevice> getUserDevices(String userName) {
        QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name",userName);
        // 1. 获取MySQL中的设备基本信息
        List<UserDevice> userDeviceList = baseMapper.selectList(wrapper);
        // 2. 获取Redis中的令牌信息
        List<DeviceTokenInfo> deviceTokenInfoList = tokenStorageService.getAllUserDevices(userName);

        // 将 List 转换为 Map，方便查找
        Map<String, DeviceTokenInfo> tokenInfoMap = deviceTokenInfoList.stream()
                .collect(Collectors.toMap(DeviceTokenInfo::getDeviceId, info -> info));
        // 3. 组合数据
        return userDeviceList.stream()
                .map(device -> {
                    DeviceInfoVO vo = new DeviceInfoVO();
                    // 设置基本设备信息
                    BeanUtils.copyProperties(device, vo);

                    // 设置令牌信息（如果存在）
                    DeviceTokenInfo tokenInfo = tokenInfoMap.get(device.getDeviceId());
                    if (tokenInfo != null && StringUtils.isNotBlank(tokenInfo.getAccessToken())) {
                        vo.setOnline(true);
                        vo.setCurrentDevice(isCurrentDevice(tokenInfo.getAccessToken()));
                    } else {
                        vo.setOnline(false);
                        vo.setCurrentDevice(false);
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDevice extractDeviceInfo(String userName, HttpServletRequest request) {
        UserDevice deviceInfo = getInfoFromRequest(request);
        deviceInfo.setUserName(userName);
        // 获取IP地址
        String ip = ToolUtil.getClientIp(request);
        if ("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "127.0.0.1" ;
        }
        deviceInfo.setLastLoginIp(ip);
        // 地址关联地区
        if(!"127.0.0.1".equals(ip)){
            Map<String, String> map = ToolUtil.getAddressByIP(ip);
            if(!map.isEmpty()) {
                deviceInfo.setLastLoginLocation(String.join("-", map.values()));
            }
        }
        return deviceInfo;
    }

    @Override
    public void handleDeviceLogout(String userName, String deviceId) {
        // 1. 清除Redis中的令牌信息
        tokenStorageService.removeDeviceBinding(userName, deviceId);

        // 2. 更新MySQL中的最后登出时间（如果需要）
        QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name",userName);
        wrapper.eq("device_id",deviceId);
        wrapper.orderByDesc("create_date");
        // 1. 获取MySQL中的设备基本信息
        List<UserDevice> userDeviceList = baseMapper.selectList(wrapper);
        if(!userDeviceList.isEmpty()){
            UserDevice userDevice = userDeviceList.get(0);
            userDevice.setCreateDate(new Date());
            userDevice.setLoginOutDate(new Date());
            baseMapper.updateById(userDevice);
        }
    }

    private UserDevice getInfoFromRequest(HttpServletRequest request){
        UserDevice deviceInfo = new UserDevice();
        Map<String,String> osAndBrowserInfo = ToolUtil.getOsAndBrowserInfo(request);
        // 获取请求头信息agent
        if(StringUtils.isNotBlank(osAndBrowserInfo.get("agent"))) {
            deviceInfo.setUserAgent(osAndBrowserInfo.get("agent"));
        }
        // 获取os信息
        if(StringUtils.isNotBlank(osAndBrowserInfo.get("os"))) {
            deviceInfo.setBrowserInfo(osAndBrowserInfo.get("os"));
        }
        // 获取浏览器信息
        if(StringUtils.isNotBlank(osAndBrowserInfo.get("browser"))) {
            deviceInfo.setOsVersion(osAndBrowserInfo.get("browser"));
        }

        return deviceInfo;
    }

    /**
     * 判断是否是当前设备
     * 通过比对请求中的token来判断
     */
    private boolean isCurrentDevice(String tokenFromRedis) {
        try {
            // 从当前请求上下文中获取token
            String currentToken = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getCredentials()
                    .toString();

            return tokenFromRedis.equals(currentToken);
        } catch (Exception e) {
            log.warn("Failed to determine current device status", e);
            return false;
        }
    }

}