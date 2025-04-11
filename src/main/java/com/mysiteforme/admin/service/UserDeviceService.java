package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.entity.request.PageListUserDeviceRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserDeviceService extends IService<UserDevice> {

    void handleDeviceLogin(String userName, UserDevice deviceInfo);

    List<UserDevice> getUserDevices(String userName);

    UserDevice extractDeviceInfo(String userName, HttpServletRequest request);

    void handleDeviceLogout(String userName, String deviceId);

    IPage<UserDevice> selectPageUserDevice(PageListUserDeviceRequest request);
}