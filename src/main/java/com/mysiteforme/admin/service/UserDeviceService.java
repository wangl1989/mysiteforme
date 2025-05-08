package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.entity.request.PageListUserDeviceRequest;
import com.mysiteforme.admin.entity.response.DeviceInfoResponse;

import java.util.List;

public interface UserDeviceService extends IService<UserDevice> {

    void handleDeviceLogin(Long userId, String deviceId);

    List<DeviceInfoResponse> getUserDevices(Long userId);

    void handleDeviceLogout(Long userId, String deviceId);

    IPage<UserDevice> selectPageUserDevice(PageListUserDeviceRequest request);

    UserDevice getCurrentUserDevice();

}