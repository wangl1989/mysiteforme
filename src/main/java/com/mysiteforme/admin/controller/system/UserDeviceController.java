package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.entity.request.PageListUserDeviceRequest;
import com.mysiteforme.admin.service.UserDeviceService;
import com.mysiteforme.admin.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/userDevice")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping("list")
    public Result list(@RequestBody PageListUserDeviceRequest request){
        return  Result.success(userDeviceService.selectPageUserDevice(request));
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