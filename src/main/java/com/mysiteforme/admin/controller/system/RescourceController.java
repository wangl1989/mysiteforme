/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:35:39
 * @ Description: 资源控制器 提供资源的增删改查功能 
 */

package com.mysiteforme.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.service.RescourceService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/system/rescource")
public class RescourceController extends BaseController{

    public RescourceController(RescourceService rescourceService) {
        this.rescourceService = rescourceService;
    }

    @GetMapping("list")
    @SysLog("跳转资源展示列表")
    public String list(){
        return "admin/system/rescource/list";
    }

    @PostMapping("list")
    @ResponseBody
    public LayerData<Rescource> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                     @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                     ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<Rescource> layerData = new LayerData<>();
        QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
        if(!map.isEmpty()){
            String type = (String) map.get("type");
            if(StringUtils.isNotBlank(type)) {
                wrapper.like("file_type", type);
            }
            String hash = (String)map.get("hash");
            if(StringUtils.isNotBlank(hash)){
                wrapper.eq("hash",hash);
            }
            String source = (String)map.get("source");
            if(StringUtils.isNotBlank(source)){
                wrapper.eq("source",source);
            }
        }
        IPage<Rescource> dataPage = rescourceService.page(new Page<>(page,limit),wrapper);
        layerData.setCount((int)dataPage.getTotal());
        layerData.setData(dataPage.getRecords());
        return layerData;
    }

    @PostMapping("delete")
    @SysLog("删除系统资源")
    @ResponseBody
    public RestResponse delete(@RequestParam("ids[]") List<Long> ids){
        if(ids == null || ids.isEmpty()){
            return RestResponse.failure("删除ID不能为空");
        }
        List<Rescource> rescources = rescourceService.listByIds(ids);
        if(rescources == null || rescources.isEmpty()){
            return RestResponse.failure("请求参数不正确");
        }
        for (Rescource rescource : rescources){
            String source = rescource.getSource();
            String path = rescource.getWebUrl();
            if(StringUtils.isNotEmpty(source) && StringUtils.isNotBlank(path)){
                switch (source) {
                    case "qiniu" -> qiniuService.delete(path);
                    case "oss" -> ossService.delete(path);
                    default -> localService.delete(path);
                }
            } else {
                return RestResponse.failure(rescource.getFileName()+"文件类型不存在");
            }
        }
        rescourceService.removeByIds(ids);
        return RestResponse.success();
    }

    @GetMapping("test")
    @SysLog("测试错误请求")
    public String error() throws Exception {
        throw new Exception("测试异常消息");
    }
}
