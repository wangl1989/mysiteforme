package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.DTO.*;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.dao.UserDeviceDao;
import com.mysiteforme.admin.entity.VO.DeviceTokenInfo;
import com.mysiteforme.admin.entity.request.PageListUserDeviceRequest;
import com.mysiteforme.admin.entity.response.DeviceInfoResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.TokenStorageService;
import com.mysiteforme.admin.service.UserCacheService;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceDao, UserDevice> implements UserDeviceService {

    private final TokenStorageService tokenStorageService;

    private final UserDao userDao;

    private final UserCacheService userCacheService;

    @Override
    public IPage<UserDevice> selectPageUserDevice(PageListUserDeviceRequest request) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        if(request != null){
            if(request.getUserId() != null) {
                wrapper.like(UserDevice::getUserId, request.getUserId());
            }
            if(StringUtils.isNotBlank(request.getDeviceId())){
                wrapper.eq(UserDevice::getDeviceId,request.getDeviceId());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), UserDevice::getCreateDate);
        }else{
            request = new PageListUserDeviceRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

    @Override
    public void handleDeviceLogin(Long userId, String deviceId) {
        // 设置设备信息
        if(userId != null && userId != 0L) {
            LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserDevice::getUserId,userId);
            wrapper.eq(UserDevice::getDeviceId,deviceId);
            // 1. 更新或创建MySQL中的设备记录
            List<UserDevice> userDeviceList = baseMapper.selectList(wrapper);
            UserDevice userDevice;
            if(userDeviceList.isEmpty()){
                userDevice = new UserDevice();
                userDevice.setDeviceId(deviceId);
                userDevice.setUserId(userId);
                userDevice.setFirstSeen(LocalDateTime.now());
            }else{
                userDevice = userDeviceList.get(0);
            }
            try {
                HttpServletRequest request = ToolUtil.getCurrentRequest();
                LocationDTO location =  userCacheService.getLocationByIp(ToolUtil.getClientIp(request));
                if(location != null){
                    userDevice.setLastLoginIp(userDevice.getThisLoginIp());
                    userDevice.setThisLoginIp(location.getIp());
                }
                AgentDTO agent = ToolUtil.getOsAndBrowserInfo(request);
                userDevice.setUserAgent(agent.getUserAgent());
            } catch (Exception ex){
                log.error("【保存用户设备信息】出现异常",ex);
            }
            // 设置活跃时间
            userDevice.setLastSeen(LocalDateTime.now());
            // 设置登录时间
            userDevice.setLastLoginTime(userDevice.getThisLoginTime());
            userDevice.setThisLoginTime(LocalDateTime.now());
            baseMapper.insertOrUpdate(userDevice);
        }else {
            log.error("未能获取到用户名");
            throw MyException.builder().code(ResultCode.INTERNAL_ERROR).msg(MessageConstants.System.SYSTEM_ERROR).build();
        }
    }

    @Override
    public List<DeviceInfoResponse> getUserDevices(Long userId) {
        User user = userDao.selectById(userId);
        if(user == null || user.getLocked() || user.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
        }
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId,userId);
        // 1. 获取MySQL中的设备基本信息
        List<UserDevice> userDeviceList = baseMapper.selectList(wrapper);
        // 2. 获取Redis中的令牌信息
        List<DeviceTokenInfo> deviceTokenInfoList = tokenStorageService.getAllUserDevices(user.getLoginName());

        // 将 List 转换为 Map，方便查找
        Map<String, DeviceTokenInfo> tokenInfoMap = deviceTokenInfoList.stream()
                .collect(Collectors.toMap(DeviceTokenInfo::getDeviceId, info -> info));
        // 3. 组合数据
        return userDeviceList.stream()
                .map(device -> {
                    DeviceInfoResponse response = new DeviceInfoResponse();
                    // 设置IP相关信息
                    LocationDTO location = userCacheService.getLocationByIp(device.getLastLoginIp());
                    BeanUtils.copyProperties(location,response);
                    // 设置令牌信息（如果存在）
                    DeviceTokenInfo tokenInfo = tokenInfoMap.get(device.getDeviceId());
                    if (tokenInfo != null && StringUtils.isNotBlank(tokenInfo.getAccessToken())) {
                        response.setOnline(true);
                        response.setCurrentDevice(isCurrentDevice(tokenInfo.getAccessToken()));
                    } else {
                        response.setOnline(false);
                        response.setCurrentDevice(false);
                    }
                    // 设置浏览器信息解析
                    if(StringUtils.isNotBlank(device.getUserAgent())) {
                        AgentDTO agent = ToolUtil.getOsAndBrowserInfo(device.getUserAgent());
                        BeanUtils.copyProperties(agent,response);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void handleDeviceLogout(Long userId, String deviceId) {
        // 1. 清除Redis中的令牌信息
        // tokenStorageService.removeDeviceBinding(userName, deviceId);

        // 2. 更新MySQL中的最后登出时间（如果需要）
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId,userId);
        wrapper.eq(UserDevice::getDeviceId,deviceId);
        // 1. 获取MySQL中的设备基本信息
        List<UserDevice> userDeviceList = baseMapper.selectList(wrapper);
        if(!userDeviceList.isEmpty()){
            UserDevice userDevice = userDeviceList.get(0);
            userDevice.setCreateDate(new Date());
            userDevice.setLoginOutDate(LocalDateTime.now());
            baseMapper.updateById(userDevice);
        }
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

    @Override
    public UserDevice getCurrentUserDevice(){
        Long userId = MySecurityUser.id();
        if(userId == null){
            throw MyException.builder().unauthorized().build();
        }
        try {
            HttpServletRequest request = ToolUtil.getCurrentRequest();
            String deviceId = request.getHeader(Constants.DEVICE_ID);
            if(StringUtils.isBlank(deviceId)){
                throw MyException.builder().businessError(MessageConstants.User.DEVICE_ID_REQUIRED).build();
            }
            LambdaQueryWrapper<UserDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserDevice::getUserId,userId);
            lambdaQueryWrapper.eq(UserDevice::getDeviceId,deviceId);
            List<UserDevice> list = list(lambdaQueryWrapper);
            if(!list.isEmpty()){
                return list.get(0);
            }
        }catch (Exception e){
            log.error("获取用户当前设备出错:",e);
        }
        return null;
    }

}