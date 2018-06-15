package com.mysiteforme.admin.controller.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Log;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2018/1/13.
 * todo:
 */
@Controller
@RequestMapping("admin/system/log")
public class LogController extends BaseController{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    @GetMapping("list")
    public String list(){
        return "admin/system/log/list";
    }

    @PostMapping("list")
    @ResponseBody
    public LayerData<Log> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<Log> layerData = new LayerData<>();
        EntityWrapper<Log> wrapper = new EntityWrapper<>();
        if(!map.isEmpty()){
            String keys = (String) map.get("type");
            if(StringUtils.isNotBlank(keys)) {
                wrapper.eq("type", keys);
            }
            String title = (String) map.get("title");
            if(StringUtils.isNotBlank(title)){
                wrapper.like("title",title);
            }
            String username = (String)map.get("username");
            if(StringUtils.isNotBlank(username)){
                wrapper.eq("username",username);
            }
            String httpMethod = (String)map.get("method");
            if(StringUtils.isNotBlank(httpMethod)){
                wrapper.eq("http_method",httpMethod);
            }
        }
        Page<Log> logPage = logService.selectPage(new Page<>(page,limit),wrapper);
        layerData.setCount(logPage.getTotal());
        layerData.setData(logPage.getRecords());
        return  layerData;
    }

    @RequiresPermissions("system:logs:delete")
    @PostMapping("delete")
    @ResponseBody
    public RestResponse delete(@RequestParam("ids[]") List<Long> ids){
        if(ids == null || ids.size()==0){
            return RestResponse.failure("id不能为空");
        }
        logService.deleteBatchIds(ids);
        return RestResponse.success();
    }

    @GetMapping("pvs")
    @ResponseBody
    public RestResponse getPV(){
        List<Integer> pvs = logService.selectSelfMonthData();
        return RestResponse.success().setData(pvs);
    }
}
