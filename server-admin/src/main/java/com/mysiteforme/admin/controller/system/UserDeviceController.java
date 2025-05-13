package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.entity.request.PageListUserDeviceRequest;
import com.mysiteforme.admin.service.UserDeviceService;
import com.mysiteforme.admin.util.LimitType;
import com.mysiteforme.admin.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/userDevice")
@RequiredArgsConstructor
@RateLimit(limit = 40, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping("list")
    public Result list(@RequestBody PageListUserDeviceRequest request){
        return  Result.success(userDeviceService.selectPageUserDevice(request));
    }

    /**
     * 获取当前登录用的设备列表
     * @return Result返回对象
     */
    @GetMapping("userDevices")
    public Result getUserDeviceList(){
        Long currentId = MySecurityUser.id();
        if(currentId == null || currentId == 0){
            return Result.unauthorized();
        }
        return Result.success(userDeviceService.getUserDevices(currentId));
    }

    @PostMapping("add")
    public Result addUserDevice(@RequestBody UserDevice userDevice) {
        userDeviceService.save(userDevice);
        return Result.success();
    }

    @GetMapping("detail")
    public Result getUserDevice(@RequestParam(value = "id",required = false)Long id) {
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        UserDevice userDevice = userDeviceService.getById(id);
        return Result.success(userDevice);
    }

    @PutMapping("update")
    public Result updateUserDevice(@RequestBody UserDevice userDevice) {
        userDeviceService.updateById(userDevice);
        return Result.success();
    }

    @DeleteMapping("delete")
    public Result deleteUserDevice(@RequestParam(value = "id",required = false)Long id) {
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        userDeviceService.removeById(id);
        return Result.success();
    }
}